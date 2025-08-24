package dev.gangster.task

import kotlin.time.Duration

/**
 * The configuration for a particular task.
 */
data class TaskConfig(
    /**
     * The identifier for the task within the [TaskTemplate].
     */
    val targetTask: String,

    /**
     * The time delay before running the task on the first time after it's ready.
     * If null task will be run immediately after it's ready.
     */
    val initialRunDelay: Duration,

    /**
     * The time delay if the task is repeatable. If null then task only run once.
     */
    val repeatDelay: Duration? = null,

    /**
     * Extra data for task. The tasks should be responsible for reading the data.
     */
    val extra: Map<String, Any>,
) {
    /**
     * DSL builder for task config.
     *
     * Can use it like:
     * ```
     * val config = taskConfig {
     *     targetTask = "SendPing"
     *     initialRunDelay = 1.seconds
     *     repeatDelay = 5.seconds
     *     extra("payload", "pong")
     *     extra("count", 42)
     * }
     * ```
     */
    class Builder {
        var targetTask: String = ""
        var initialRunDelay: Duration = Duration.ZERO
        var repeatDelay: Duration? = null
        private val extras: MutableMap<String, Any> = mutableMapOf()

        fun extra(key: String, value: Any) {
            extras[key] = value
        }

        fun build() = TaskConfig(
            targetTask = targetTask,
            initialRunDelay = initialRunDelay,
            repeatDelay = repeatDelay,
            extra = extras.toMap()
        )
    }

    /**
     * Run the following block of [TaskConfig] builder.
     */
    companion object {
        fun taskConfig(block: Builder.() -> Unit): TaskConfig {
            return Builder().apply(block).build()
        }
    }
}
