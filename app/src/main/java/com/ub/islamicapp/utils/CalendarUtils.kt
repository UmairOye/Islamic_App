package com.ub.islamicapp.utils

import android.icu.util.IslamicCalendar

data class CalendarDay(
    val dayOfMonth: Int,
    val gregorianDay: Int? = null,
    val isCurrentMonth: Boolean,
    val isToday: Boolean
)

data class MonthData(
    val monthName: String,
    val monthIndex: Int,
    val year: Int,
    val days: List<CalendarDay>,
    val gregorianYear: Int? = null,
    val gregorianMonthName: String? = null,
    val gregorianDayOfMonth: Int? = null
)

object CalendarUtils {

    fun getGregorianMonthData(offsetMonths: Int): MonthData {
        val calendar = java.util.Calendar.getInstance()
        val today = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        calendar.add(java.util.Calendar.MONTH, offsetMonths)

        val targetMonth = calendar.get(java.util.Calendar.MONTH)
        val targetYear = calendar.get(java.util.Calendar.YEAR)

        val monthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        // Adjust so Sunday is the first day of the week
        // get(DAY_OF_WEEK) returns Sunday=1, Monday=2, ... Saturday=7
        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        val emptyPrefixCount = firstDayOfWeek - 1 // Sunday=0 empty, Monday=1 empty... Saturday=6 empty

        val days = mutableListOf<CalendarDay>()

        for (i in 0 until emptyPrefixCount) {
            days.add(CalendarDay(0, null, false, false))
        }

        for (i in 1..daysInMonth) {
            val isToday = (offsetMonths == 0 && i == today)
            days.add(CalendarDay(i, null, true, isToday))
        }

        while (days.size % 7 != 0) {
             days.add(CalendarDay(0, null, false, false))
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

            val todayHijri = calendar.get(IslamicCalendar.DAY_OF_MONTH)

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

            // Adjust so Sunday is the first day of the week
            val firstDayOfWeek = calendar.get(IslamicCalendar.DAY_OF_WEEK)
            val emptyPrefixCount = firstDayOfWeek - 1 // Sunday=0, Monday=1, ... Saturday=6

            val days = mutableListOf<CalendarDay>()
            for (i in 0 until emptyPrefixCount) {
                days.add(CalendarDay(0, null, false, false))
            }

            for (i in 1..daysInMonth) {
                calendar.set(IslamicCalendar.DAY_OF_MONTH, i)
                val gregorianDate = java.util.Calendar.getInstance()
                gregorianDate.timeInMillis = calendar.timeInMillis
                val gregorianDay = gregorianDate.get(java.util.Calendar.DAY_OF_MONTH)

                val isToday = (offsetMonths == 0 && i == todayHijri)
                days.add(CalendarDay(i, gregorianDay, true, isToday))
            }

            while (days.size % 7 != 0) {
                 days.add(CalendarDay(0, null, false, false))
            }

            // Get Gregorian details for the current displayed day (let's say the 1st of the Hijri month)
            calendar.set(IslamicCalendar.DAY_OF_MONTH, 1)
            val gregorianHeaderDate = java.util.Calendar.getInstance()
            gregorianHeaderDate.timeInMillis = calendar.timeInMillis
            val gregorianHeaderMonthNames = arrayOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

            return MonthData(
                monthName = monthNames[targetMonth],
                monthIndex = targetMonth,
                year = targetYear,
                days = days,
                gregorianYear = gregorianHeaderDate.get(java.util.Calendar.YEAR),
                gregorianMonthName = gregorianHeaderMonthNames[gregorianHeaderDate.get(java.util.Calendar.MONTH)],
                gregorianDayOfMonth = gregorianHeaderDate.get(java.util.Calendar.DAY_OF_MONTH)
            )
        } else {
            return MonthData("Unknown", 0, 1445, emptyList())
        }
    }
}
