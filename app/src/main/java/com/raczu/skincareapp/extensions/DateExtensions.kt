package com.raczu.skincareapp.extensions

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toInstant(): Instant = Instant.ofEpochMilli(this)

fun Instant.toHumanReadableString(pattern: String = "dd MMM yyyy HH:mm"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}