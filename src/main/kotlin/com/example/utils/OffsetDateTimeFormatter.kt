package com.example.utils

import java.time.OffsetDateTime
import java.time.format.TextStyle
import java.util.*

class OffsetDateTimeFormatter {
    companion object StartDateParser {
        fun parse(date: OffsetDateTime): String {
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val dayOfMonth = date.dayOfMonth
            val month = date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            val year = date.year

            return "$dayOfWeek, $month $dayOfMonth, $year"
        }
    }
}