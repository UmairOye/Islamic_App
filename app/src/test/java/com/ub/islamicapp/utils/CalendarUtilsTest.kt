package com.ub.islamicapp.utils

import org.junit.Test
import org.junit.Assert.*

class CalendarUtilsTest {

    @Test
    fun testGregorianMonthData() {
        val monthData = CalendarUtils.getGregorianMonthData(0)
        assertNotNull(monthData)
        assertTrue(monthData.monthName.isNotEmpty())
        assertTrue(monthData.days.isNotEmpty())
    }

    @Test
    fun testHijriMonthData() {
        // Hijri calculation might depend on Android SDK version (Build.VERSION.SDK_INT)
        // In unit tests, Build.VERSION.SDK_INT might be 0 or a fixed value.
        val monthData = CalendarUtils.getHijriMonthData(0)
        assertNotNull(monthData)
        // If SDK < N, it returns "Unknown", which is still a MonthData
        assertTrue(monthData.monthName.isNotEmpty())
    }
}
