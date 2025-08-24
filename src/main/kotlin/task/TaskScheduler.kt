package dev.gangster.task

import dev.gangster.socket.core.Connection

/**
 * Entity that can schedule task.
 *
 * By default, the scheduling of task is done by [DefaultTaskScheduler].
 * However, if task scheduling is complex, the particular [ServerTask] can override the implementation.
 */
interface TaskScheduler {
    suspend fun schedule(task: ServerTask, connection: Connection, cfg: TaskConfig)
}
