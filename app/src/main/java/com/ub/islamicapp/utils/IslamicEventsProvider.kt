package com.ub.islamicapp.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ub.islamicapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStreamReader

object IslamicEventsProvider {

    data class EventDetail(
        val date: String?,
        val text: String
    )

    data class EventCategory(
        val Fazail: List<EventDetail>?,
        val Amaal: List<EventDetail>?,
        val Rasm: List<EventDetail>?,
        val Waqiat: List<EventDetail>?
    )

    private var eventsData: Map<String, List<EventCategory>>? = null

    fun init(context: Context) {
        if (eventsData == null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = context.resources.openRawResource(R.raw.islamic_calendar)
                    val reader = InputStreamReader(inputStream)
                    val type = object : TypeToken<Map<String, List<EventCategory>>>() {}.type
                    eventsData = Gson().fromJson(reader, type)
                    reader.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Gets an event description for a given month and day.
     * monthIndex is 0-based: 0 = Muharram, ..., 11 = Dhu al-Hijjah
     */
    fun getEventForDate(monthIndex: Int, day: Int): String? {
        val monthKey = (monthIndex + 1).toString()
        val categories = eventsData?.get(monthKey) ?: return null

        for (category in categories) {

            category.Waqiat?.let { waqiat ->
                val regex = Regex("\\b$day\\b")
                val matchingWaqia = waqiat.find { event ->
                    event.date != null && regex.containsMatchIn(event.date)
                }
                if (matchingWaqia != null) {
                    return matchingWaqia.text
                }
            }

            if (day == 1) {
                category.Fazail?.firstOrNull()?.text?.let { return it }
                category.Amaal?.firstOrNull()?.text?.let { return it }
            }
        }

        return null
    }
}