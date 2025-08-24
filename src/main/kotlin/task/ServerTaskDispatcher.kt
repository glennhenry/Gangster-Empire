package dev.gangster.task

import dev.gangster.socket.core.Connection
import dev.gangster.utils.LogConfigSocketError
import dev.gangster.utils.LogSource
import dev.gangster.utils.Logger
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

/**
 * Manages and dispatches registered [ServerTask]s for each client [Connection].
 *
 * This is used to register tasks that the server runs independently, usually
 * to push messages to the connected clients (e.g., time update, real-time events).
 *
 * @property registeredTasks keep tracks registered tasks
 * @property defaultConfigs config for each task. The default config can be overridden from [runTask].
 * @property runningInstances list of globally unique running tasks
 */
class ServerTaskDispatcher {
    /**
     * Map of task's template key to the server task implementation.
     */
    private val registeredTasks = mutableMapOf<TaskTemplate, ServerTask>()

    /**
     * Map of task's template key to the default tasks configuration.
     */
    private val defaultConfigs = mutableMapOf<TaskTemplate, TaskConfig>()

    /**
     * Map of unique task ID to the task instance.
     */
    private val runningInstances = mutableMapOf<UUID, TaskInstance>()

    /**
     * Instance of the default task scheduler, used when the following task does not provide one.
     */
    private val defaultScheduler = DefaultTaskScheduler()

    /**
     * Register a task that can be run afterward using [runTask].
     */
    fun register(task: ServerTask) {
        registeredTasks[task.key] = task
        defaultConfigs[task.key] = task.defaultConfig
    }

    /**
     * Run a task for the socket connection, returning the task ID (UUID).
     *
     * @return task instance ID.
     */
    fun runTask(
        connection: Connection,
        taskTemplateKey: TaskTemplate,
        cfgBuilder: (TaskConfig) -> TaskConfig?,
        onComplete: (() -> Unit)? = null
    ): UUID {
        val task = requireNotNull(registeredTasks[taskTemplateKey]) { "Task not registered: $taskTemplateKey" }
        val defaultCfg =
            requireNotNull(defaultConfigs[taskTemplateKey]) { "Missing default config for $taskTemplateKey" }
        val cfg = cfgBuilder(defaultCfg) ?: defaultCfg

        val taskId = UUID.randomUUID()

        val job = connection.scope.launch {
            try {
                Logger.debug(LogSource.SOCKET) { "Push task ${task.key} is going to run." }
                val scheduler = task.scheduler ?: defaultScheduler
                scheduler.schedule(task, connection, cfg)
            } catch (_: CancellationException) {
                Logger.debug(LogSource.SOCKET) { "Push task '${task.key}' was cancelled." }
            } catch (e: Exception) {
                Logger.error(LogConfigSocketError) { "Error running push task '${task.key}': $e" }
            } finally {
                Logger.debug(LogSource.SOCKET) { "Push task ${task.key} has finished running." }
                runningInstances.remove(taskId)
                onComplete?.invoke()
            }
        }

        runningInstances[taskId] = TaskInstance(connection.playerId, taskTemplateKey, cfg, job, onComplete)
        return taskId
    }

    /**
     * Remove task from the running instances and stop the coroutine job.
     */
    fun stopTask(taskId: UUID) {
        runningInstances.remove(taskId)?.job?.cancel()
    }

    /**
     * Remove all running tasks for the [playerId] and stop each coroutine job.
     */
    fun stopAllTasksForPlayer(playerId: Int) {
        runningInstances
            .filterValues { it.playerId == playerId }
            .forEach { (taskId, _) -> stopTask(taskId) }
    }

    /**
     * Remove all running tasks in the server.
     */
    fun stopAllPushTasks() {
        runningInstances.forEach { (taskId, _) -> stopTask(taskId) }
    }

    fun close() {
        registeredTasks.clear()
        defaultConfigs.clear()
        stopAllPushTasks()
    }
}
