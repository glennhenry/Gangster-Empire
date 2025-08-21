package dev.gangster.utils

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Int.toBoolean(): Boolean {
    return this == 1
}
