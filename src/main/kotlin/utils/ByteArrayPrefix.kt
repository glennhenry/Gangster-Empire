package dev.gangster.utils

import java.nio.charset.Charset

fun ByteArray.startsWithBytes(prefix: ByteArray): Boolean {
    if (this.size < prefix.size) return false
    for (i in prefix.indices) {
        if (this[i] != prefix[i]) return false
    }
    return true
}

fun ByteArray.startsWithString(prefix: String, charset: Charset = Charsets.UTF_8): Boolean {
    val prefixBytes = prefix.toByteArray(charset)
    if (this.size < prefixBytes.size) return false
    for (i in prefixBytes.indices) {
        if (this[i] != prefixBytes[i]) return false
    }
    return true
}
