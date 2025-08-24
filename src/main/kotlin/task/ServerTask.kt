package dev.gangster.task

import dev.gangster.socket.core.Connection

/**
 * Represent tasks that runs on the server.
 *
 * Implementations of this interface can push data to clients through client's [Connection]
 * if needed.
 */
interface ServerTask {
    /**
     * Unique identifier for each task template.
     */
    val key: TaskTemplate

    /**
     * Default config for the task.
     */
    val defaultConfig: TaskConfig

    /**
     * A scheduler override from the default [ServerTaskDispatcher] implemented by task if needed.
     */
    val scheduler: TaskScheduler?

    /**
     * Run the task. Task do not need to schedule its running as scheduling is done by [TaskScheduler].
     *
     * @param connection the player's socket connection to send message if needed.
     */
    suspend fun run(connection: Connection, finalConfig: TaskConfig)
}
