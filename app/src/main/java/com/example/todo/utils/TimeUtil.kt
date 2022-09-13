package com.example.todo.utils

import android.icu.util.GregorianCalendar
import java.text.SimpleDateFormat
import java.util.*

object TimeUtil {
    fun format(date: Date?): String {
        val timeFormatter = SimpleDateFormat("EEEE dd/MM/yyyy | HH:mm", Locale.ROOT)
        return date?.let { timeFormatter.format(it).toString() } ?: "Chưa hẹn giờ"
    }

    fun currentTime(): Date? = GregorianCalendar().time
}