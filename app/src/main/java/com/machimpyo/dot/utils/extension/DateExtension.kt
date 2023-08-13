package com.machimpyo.dot.utils.extension

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val localDateAtMidnight = LocalDate.of(zonedDateTime.year, zonedDateTime.month, zonedDateTime.dayOfMonth)
    Log.e("TAG", "LocalDate로 변환: $this, ${localDateAtMidnight.toMillis()}")
    return localDateAtMidnight
}

fun LocalDate.toMillis(): Long {
    val localDateTime = LocalDateTime.of(this, LocalTime.MIDNIGHT)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = localDateTime.atZone(zoneId)
    Log.e("TAG", "밀리로 변환: ${zonedDateTime.toInstant().toEpochMilli()}")
    return zonedDateTime.toInstant().toEpochMilli()
}

fun Long.toFormattedDate(formatter: DateTimeFormatter? = null): String {
    val _formatter = formatter ?: DateTimeFormatter.ofPattern("yyyy'년' MM'월' dd'일'", Locale.getDefault())
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
    return _formatter.format(localDateTime)
}
fun LocalDate.toFormattedDate(formatter: DateTimeFormatter? = null): String {
    val _formatter = formatter ?: DateTimeFormatter.ofPattern("yyyy'년' MM'월' dd'일'", Locale.getDefault())
    return this.format(_formatter)
}

fun LocalDateTime.toFormattedDate(formatter: DateTimeFormatter? = null): String {
    val _formatter = formatter ?: DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm", Locale.getDefault())
    return this.format(_formatter)
}

fun LocalDate.toDDay(): Long {
    val today = LocalDate.now(ZoneId.systemDefault())
    return ChronoUnit.DAYS.between(today, this)
}