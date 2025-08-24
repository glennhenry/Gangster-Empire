package dev.gangster.task

import kotlinx.coroutines.Job

/**
 * An instance of server task.
 *
 * @property playerId the player the task belongs to.
 * @property taskKey the [ServerTask] identifier.
 * @property config the configuration of the task.
 * @property job coroutine reference for the task.
 * @property onComplete callback after task has finished running.
 */
data class TaskInstance(
    val playerId: Long,
    val taskKey: TaskTemplate,
    val config: TaskConfig,
    val job: Job,
    val onComplete: (() -> Unit)?
)
