package com.ub.islamicapp.utils

import android.icu.util.Calendar
import android.icu.util.IslamicCalendar
import java.util.Locale

data class CalendarDay(
    val dayOfMonth: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

data class MonthData(
    val monthName: String,
    val monthIndex: Int,
    val year: Int,
    val days: List<CalendarDay>
)

object CalendarUtils {

    fun getGregorianMonthData(offsetMonths: Int): MonthData {
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        calendar.add(java.util.Calendar.MONTH, offsetMonths)

        val targetMonth = calendar.get(java.util.Calendar.MONTH)
        val targetYear = calendar.get(java.util.Calendar.YEAR)

        val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK) // 1 = Sunday, etc.

        val days = mutableListOf<CalendarDay>()

        // Offset for empty cells before 1st day. (Assuming Monday is first day of UI row)
        // If Sunday is first day, empty cells = firstDayOfWeek - 1. We will assume Sunday is first day.
        val emptyPrefixCount = firstDayOfWeek - 1
        for (i in 0 until emptyPrefixCount) {
            days.add(CalendarDay(0, false, false)) // Using 0 to represent empty
        }

        for (i in 1..daysInMonth) {
            val isToday = (offsetMonths == 0 && i == today)
            days.add(CalendarDay(i, true, isToday))
        }

        // Fill rest of the grid
        while (days.size % 7 != 0) {
             days.add(CalendarDay(0, false, false))
        }

        return MonthData(
            monthName = monthNames[targetMonth],
            monthIndex = targetMonth,
            year = targetYear,
            days = days
        )
    }

    fun getHijriMonthData(offsetMonths: Int): MonthData {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val calendar = IslamicCalendar()
            val today = calendar.get(IslamicCalendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(IslamicCalendar.MONTH)
            val currentYear = calendar.get(IslamicCalendar.YEAR)

            calendar.add(IslamicCalendar.MONTH, offsetMonths)

            val targetMonth = calendar.get(IslamicCalendar.MONTH)
            val targetYear = calendar.get(IslamicCalendar.YEAR)

            val monthNames = arrayOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
            )

            val daysInMonth = calendar.getActualMaximum(IslamicCalendar.DAY_OF_MONTH)
            calendar.set(IslamicCalendar.DAY_OF_MONTH, 1)
            val firstDayOfWeek = calendar.get(IslamicCalendar.DAY_OF_WEEK)

            val days = mutableListOf<CalendarDay>()
            val emptyPrefixCount = firstDayOfWeek - 1
            for (i in 0 until emptyPrefixCount) {
                days.add(CalendarDay(0, false, false))
            }

            for (i in 1..daysInMonth) {
                val isToday = (offsetMonths == 0 && i == today)
                days.add(CalendarDay(i, true, isToday))
            }

            while (days.size % 7 != 0) {
                 days.add(CalendarDay(0, false, false))
            }

            return MonthData(
                monthName = monthNames[targetMonth],
                monthIndex = targetMonth,
                year = targetYear,
                days = days
            )
        } else {
            return MonthData("Unknown", 0, 1445, emptyList())
        }
    }
}
