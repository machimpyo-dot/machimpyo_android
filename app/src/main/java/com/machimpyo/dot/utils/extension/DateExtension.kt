package com.machimpyo.dot.utils.extension

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    val zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId)
    val localDateAtMidnight = LocalDate.of(zonedDateTime.year, zonedDateTime.month, zonedDateTime.dayOfMonth)
    Log.e("TAG", "LocalDate로 변환: $this, ${localDateAtMidnight.toLong()}")
    return localDateAtMidnight
}

fun randomLocalDate(): LocalDate {
    val year = (2022..2023).random()
    val month = (1..12).random()
    val dayOfMonth = when(month) {
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 1..29 else 1..28
        4, 6, 9, 11 -> 1..30
        else -> 1..31
    }.random()

    return LocalDate.of(year, month, dayOfMonth)
}

fun LocalDate.toLong(): Long {

    return this.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

//    val localDateTime = LocalDateTime.of(this, LocalTime.MIDNIGHT)
//    val zoneId = ZoneId.systemDefault()
//    val zonedDateTime = localDateTime.atZone(zoneId)
//    Log.e("TAG", "밀리로 변환: ${zonedDateTime.toInstant().toEpochMilli()}")
//    return zonedDateTime.toInstant().toEpochMilli()
}

//fun Long.toFormattedDate(formatter: DateTimeFormatter? = null): String {
//    val _formatter = formatter ?: DateTimeFormatter.ofPattern("yyyy'년' MM'월' dd'일'", Locale.getDefault())
//    val instant = Instant.ofEpochMilli(this)
//    val zoneId = ZoneId.systemDefault()
//    val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
//    return _formatter.format(localDateTime)
//}
fun LocalDate.toFormattedDate(format: String = "yyyy'년' MM'월' dd'일'"): String {
    val formatter = DateTimeFormatter.ofPattern(format, Locale.getDefault())
    return this.format(formatter)
}

fun LocalDateTime.toFormattedDate(formatter: DateTimeFormatter? = null): String {
    val _formatter = formatter ?: DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm", Locale.getDefault())
    return this.format(_formatter)
}

fun LocalDate.toDDay(): Long {
    val today = LocalDate.now(ZoneId.systemDefault())
    return ChronoUnit.DAYS.between(today, this)
}