package com.ub.islamicapp.utils

import java.util.Calendar
import java.util.TimeZone
import kotlin.math.*

/**
 * Offline Prayer Time Calculator
 * Implementing standard astronomical algorithms to compute prayer times.
 * Default calculation method: Muslim World League (Fajr 18.0 deg, Isha 17.0 deg)
 */
object PrayerTimeCalculator {

    private const val FAJR_ANGLE = 18.0
    private const val ISHA_ANGLE = 18.0

    private const val ASR_FACTOR = 2.0

    private fun dsin(d: Double): Double = sin(Math.toRadians(d))
    private fun dcos(d: Double): Double = cos(Math.toRadians(d))
    private fun dtan(d: Double): Double = tan(Math.toRadians(d))
    private fun darcsin(x: Double): Double = Math.toDegrees(asin(x))
    private fun darccos(x: Double): Double = Math.toDegrees(acos(x))
    private fun darctan(x: Double): Double = Math.toDegrees(atan(x))
    private fun darccot(x: Double): Double = Math.toDegrees(atan(1.0 / x))

    private fun fixAngle(a: Double): Double {
        var angle = a - 360.0 * floor(a / 360.0)
        angle = if (angle < 0) angle + 360.0 else angle
        return angle
    }

    private fun fixHour(a: Double): Double {
        var hour = a - 24.0 * floor(a / 24.0)
        hour = if (hour < 0) hour + 24.0 else hour
        return hour
    }

    private fun dtime(d: Double): DoubleArray {
        var d1 = d
        if (d1.isNaN()) return doubleArrayOf(Double.NaN, Double.NaN)
        d1 = fixHour(d1 + 0.5 / 60.0)
        val hours = floor(d1)
        val minutes = floor((d1 - hours) * 60.0)
        return doubleArrayOf(hours, minutes)
    }

    /**
     * Compute the Julian Day from the Gregorian calendar.
     */
    private fun calculateJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    /**
     * Sun Equation
     */
    private fun sunPosition(jd: Double): DoubleArray {
        val d = jd - 2451545.0
        val g = fixAngle(357.529 + 0.98560028 * d)
        val q = fixAngle(280.459 + 0.98564736 * d)
        val l = fixAngle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g))

        val e = 23.439 - 0.00000036 * d

        val ra = darctan(dcos(e) * dtan(l)) / 15.0
        val rightAscension = fixHour(ra + (fixAngle(l) / 15.0 - ra))
        val eqTime = q / 15.0 - rightAscension

        val declination = darcsin(dsin(e) * dsin(l))

        return doubleArrayOf(declination, eqTime)
    }

    /**
     * Equation of time (in hours)
     */
    private fun equationOfTime(jd: Double): Double {
        return sunPosition(jd)[1]
    }

    /**
     * Solar declination
     */
    private fun sunDeclination(jd: Double): Double {
        return sunPosition(jd)[0]
    }

    /**
     * Compute the time of the transit for a specific angle
     */
    private fun computeTime(angle: Double, time: Double, lat: Double, lng: Double, jd: Double, timeZone: Double, isSunrise: Boolean = false, isAsr: Boolean = false): Double {
        val d = jd - 2451545.0 + (time - timeZone) / 24.0
        val dec = sunDeclination(jd + (time - timeZone) / 24.0)
        val eqt = equationOfTime(jd + (time - timeZone) / 24.0)

        val noon = 12 + timeZone - (lng / 15.0) - eqt

        if (angle == 0.0 && !isAsr) {
            return noon
        }

        val v: Double
        if (isAsr) {
            val h = darccot(ASR_FACTOR + dtan(abs(lat - dec)))
            v = 1.0 / 15.0 * darccos((dsin(h) - dsin(lat) * dsin(dec)) / (dcos(lat) * dcos(dec)))
        } else {
            val cosH = (dsin(angle) - dsin(lat) * dsin(dec)) / (dcos(lat) * dcos(dec))
            if (cosH < -1 || cosH > 1) {

                return Double.NaN
            }
            v = 1.0 / 15.0 * darccos(cosH)
        }

        return if (isSunrise) noon - v else noon + v
    }

    /**
     * Get Prayer Times for a given date and location.
     * Returns a map of Prayer Name to formatted Time String (HH:mm)
     */
    fun getPrayerTimes(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        timeZone: Double
    ): Map<String, String> {
        val jd = calculateJulianDate(year, month, day) - longitude / (15.0 * 24.0)

        var fajr = 5.0
        var sunrise = 6.0
        var dhuhr = 12.0
        var asr = 13.0
        var sunset = 18.0
        var maghrib = 18.0
        var isha = 18.0

        for (i in 0 until 1) {
            val t = doubleArrayOf(fajr, sunrise, dhuhr, asr, sunset, maghrib, isha)

            dhuhr = computeTime(0.0, t[2], latitude, longitude, jd, timeZone)
            fajr = computeTime(-FAJR_ANGLE, t[0], latitude, longitude, jd, timeZone, isSunrise = true)
            sunrise = computeTime(-0.833, t[1], latitude, longitude, jd, timeZone, isSunrise = true)
            asr = computeTime(0.0, t[3], latitude, longitude, jd, timeZone, isAsr = true)
            sunset = computeTime(-0.833, t[4], latitude, longitude, jd, timeZone)
            maghrib = computeTime(-0.833, t[5], latitude, longitude, jd, timeZone)
            isha = computeTime(-ISHA_ANGLE, t[6], latitude, longitude, jd, timeZone)
        }

        for (i in 0 until 2) {
            val t = doubleArrayOf(fajr, sunrise, dhuhr, asr, sunset, maghrib, isha)

            dhuhr = computeTime(0.0, t[2], latitude, longitude, jd, timeZone)
            fajr = computeTime(-FAJR_ANGLE, t[0], latitude, longitude, jd, timeZone, isSunrise = true)
            sunrise = computeTime(-0.833, t[1], latitude, longitude, jd, timeZone, isSunrise = true)
            asr = computeTime(0.0, t[3], latitude, longitude, jd, timeZone, isAsr = true)
            sunset = computeTime(-0.833, t[4], latitude, longitude, jd, timeZone)
            maghrib = computeTime(-0.833, t[5], latitude, longitude, jd, timeZone)
            isha = computeTime(-ISHA_ANGLE, t[6], latitude, longitude, jd, timeZone)
        }

        return mapOf(
            "Fajr" to formatTime(fajr),
            "Sunrise" to formatTime(sunrise),
            "Dhuhr" to formatTime(dhuhr),
            "Asr" to formatTime(asr),
            "Maghrib" to formatTime(maghrib + (3.0 / 60.0)),
            "Isha" to formatTime(isha)
        )
    }

    private fun formatTime(time: Double): String {
        if (time.isNaN()) return "--:--"
        var t = fixHour(time + 0.5 / 60.0)
        val hours = floor(t).toInt()
        val minutes = floor((t - hours) * 60).toInt()

        return String.format("%02d:%02d", hours, minutes)
    }
}
