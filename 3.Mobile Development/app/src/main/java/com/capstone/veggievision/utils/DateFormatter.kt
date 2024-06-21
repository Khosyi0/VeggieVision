package com.dicoding.newsapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDateScan(currentTimestampMillis: Long, targetTimeZone: String): String {
        try {
            val instant = Instant.ofEpochMilli(currentTimestampMillis)

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
                .withZone(ZoneId.of(targetTimeZone))

            return formatter.format(instant)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}