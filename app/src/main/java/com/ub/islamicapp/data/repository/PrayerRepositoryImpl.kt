package com.ub.islamicapp.data.repository

import android.app.Application
import android.location.Geocoder
import android.os.Build
import com.ub.islamicapp.domain.model.HijriDate
import com.ub.islamicapp.domain.model.PrayerTime
import com.ub.islamicapp.domain.model.PrayerTimes
import com.ub.islamicapp.domain.model.TimeRemaining
import com.ub.islamicapp.domain.repository.PrayerRepository
import com.ub.islamicapp.utils.PrayerTimeCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

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

        val times = PrayerTimeCalculator.getPrayerTimes(year, month, day, latitude, longitude, timeZone)

        var locName: String? = null
        val distThreshold = 0.001

        if (cachedLat != null && cachedLng != null &&
            Math.abs(cachedLat!! - latitude) < distThreshold &&
            Math.abs(cachedLng!! - longitude) < distThreshold &&
            cachedLocationName != null) {
            locName = cachedLocationName
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
                    } else null
                }
                if (locName != null) {
                    cachedLat = latitude
                    cachedLng = longitude
                    cachedLocationName = locName
                }
            } catch (e: Exception) {
                locName = cachedLocationName
            }
        }

        val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTimeStr = timeFormat24.format(calendar.time)

        val prayerNamesOrder = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
        val prayers = mutableListOf<PrayerTime>()

        var nextPrayerStr = ""
        var timeRemaining: TimeRemaining? = null
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

            if (!isCompleted && !foundNext && name != "Sunrise") {
                foundNext = true
                nextPrayerStr = name

                val pCal = Calendar.getInstance()
                pCal.set(Calendar.HOUR_OF_DAY, pHour)
                pCal.set(Calendar.MINUTE, pMin)

                val diff = pCal.timeInMillis - currentMillis
                val hoursRem = (diff / (1000 * 60 * 60)).toInt()
                val minsRem = ((diff / (1000 * 60)) % 60).toInt()
                timeRemaining = TimeRemaining(name, hoursRem, minsRem)
            }
        }

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
                val hoursRem = (diff / (1000 * 60 * 60)).toInt()
                val minsRem = ((diff / (1000 * 60)) % 60).toInt()
                timeRemaining = TimeRemaining("Fajr", hoursRem, minsRem)
            }
        }

        val hijriDate = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val islamicCalendar = android.icu.util.IslamicCalendar()
                val hDay = islamicCalendar.get(android.icu.util.IslamicCalendar.DAY_OF_MONTH)
                val hMonth = islamicCalendar.get(android.icu.util.IslamicCalendar.MONTH)
                val hYear = islamicCalendar.get(android.icu.util.IslamicCalendar.YEAR)
                HijriDate(hDay, hMonth, hYear, null)
            } else {
                HijriDate(0, 0, 0, null)
            }
        } catch (e: Exception) {
             HijriDate(0, 0, 0, null)
        }

        return PrayerTimes(
            locationName = locName ?: "",
            hijriDate = hijriDate,
            currentTime = currentTimeStr,
            timeRemaining = timeRemaining,
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
                    ""
                }
            }
            emit(locName ?: "")
        } catch (e: Exception) {
            emit("")
        }
    }
}
