package com.ub.islamicapp.data.repository

import android.app.Application
import android.location.Geocoder
import com.ub.islamicapp.domain.model.PrayerTime
import com.ub.islamicapp.domain.model.PrayerTimes
import com.ub.islamicapp.domain.repository.PrayerRepository
import com.ub.islamicapp.utils.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class PrayerRepositoryImpl @Inject constructor(
    private val context: Application
) : PrayerRepository {

    private var cachedLat: Double? = null
    private var cachedLng: Double? = null
    private var cachedLocationName: String? = null

    override suspend fun getPrayerTimes(latitude: Double, longitude: Double): PrayerTimes {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val timeZone = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000.0 * 60 * 60)

        // Calculate Prayer Times
        val times = PrayerTimeCalculator.getPrayerTimes(year, month, day, latitude, longitude, timeZone)

        // Location Info (with caching to avoid repeated Geocoder network calls every minute)
        var locName = "Current Location"
        val distThreshold = 0.001 // roughly 100 meters

        if (cachedLat != null && cachedLng != null &&
            abs(cachedLat!! - latitude) < distThreshold &&
            abs(cachedLng!! - longitude) < distThreshold &&
            cachedLocationName != null) {
            locName = cachedLocationName!!
        } else {
            try {
                locName = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val city = address.locality ?: address.subAdminArea ?: ""
                        val country = address.countryName ?: ""
                        if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }
                    } else {
                        "Current Location"
                    }
                }
                cachedLat = latitude
                cachedLng = longitude
                cachedLocationName = locName
            } catch (e: Exception) {
                e.printStackTrace()
                locName = cachedLocationName ?: "Current Location"
            }
        }

        // Current time format
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTimeStr = timeFormat.format(calendar.time)

        val prayerNamesOrder = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
        val prayers = mutableListOf<PrayerTime>()

        var nextPrayerStr = ""
        var timeRemainingStr = ""

        var foundNext = false
        val currentMillis = calendar.timeInMillis
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMin = calendar.get(Calendar.MINUTE)

        for (name in prayerNamesOrder) {
            val timeStr = times[name] ?: "--:--"
            if (timeStr == "--:--") {
                prayers.add(PrayerTime(name = name, time = timeStr, isCompleted = false))
                continue
            }

            val parts = timeStr.split(":")
            val pHour = parts[0].toInt()
            val pMin = parts[1].toInt()

            val isCompleted = (currentHour > pHour) || (currentHour == pHour && currentMin >= pMin)
            prayers.add(PrayerTime(name = name, time = timeStr, isCompleted = isCompleted))

            if (!isCompleted && !foundNext) {
                foundNext = true
                nextPrayerStr = name

                val pCal = Calendar.getInstance()
                pCal.set(Calendar.HOUR_OF_DAY, pHour)
                pCal.set(Calendar.MINUTE, pMin)

                val diff = pCal.timeInMillis - currentMillis
                val hoursRem = diff / (1000 * 60 * 60)
                val minsRem = (diff / (1000 * 60)) % 60
                timeRemainingStr = "$name $hoursRem hour $minsRem min left"
            }
        }

        // Handle case where all prayers today are completed (next is Fajr tomorrow)
        if (!foundNext) {
            nextPrayerStr = "Fajr"
            val fajrTimeStr = times["Fajr"] ?: "--:--"

            if (fajrTimeStr != "--:--") {
                val parts = fajrTimeStr.split(":")
                val pHour = parts[0].toInt()
                val pMin = parts[1].toInt()

                val pCal = Calendar.getInstance()
                pCal.add(Calendar.DAY_OF_YEAR, 1)
                pCal.set(Calendar.HOUR_OF_DAY, pHour)
                pCal.set(Calendar.MINUTE, pMin)

                val diff = pCal.timeInMillis - currentMillis
                val hoursRem = diff / (1000 * 60 * 60)
                val minsRem = (diff / (1000 * 60)) % 60
                timeRemainingStr = "Fajr $hoursRem hour $minsRem min left"
            } else {
                timeRemainingStr = "Fajr -- hour -- min left"
            }
        }

        // Hijri Date Formatting
        val hijriDateStr = try {
            val islamicCalendar = android.icu.util.IslamicCalendar()
            val hDay = islamicCalendar.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
            val hMonth = islamicCalendar.get(android.icu.util.IslamicCalendar.MONTH)
            val hYear = islamicCalendar.get(android.icu.util.IslamicCalendar.YEAR)

            val monthNames = arrayOf(
                "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
                "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
                "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
            )
            "$hDay ${monthNames[hMonth]} $hYear H"
        } catch (e: Exception) {
            "Unknown Hijri Date"
        }

        return PrayerTimes(
            locationName = locName,
            hijriDate = hijriDateStr,
            currentTime = currentTimeStr,
            timeRemaining = timeRemainingStr,
            prayers = prayers,
            nextPrayer = nextPrayerStr
        )
    }

    override fun getLocationName(latitude: Double, longitude: Double): Flow<String> = flow {
        try {
            val locName = withContext(Dispatchers.IO) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: address.subAdminArea ?: ""
                    val country = address.countryName ?: ""
                    if (city.isNotEmpty() && country.isNotEmpty()) "$city, $country" else city.ifEmpty { country }
                } else {
                    "Unknown Location"
                }
            }
            emit(locName)
        } catch (e: Exception) {
            emit("Unknown Location")
        }
    }
}
