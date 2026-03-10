package com.ub.islamicapp.utils

/**
 * Provides static historical events based on the Hijri calendar.
 * Month indexes are 0-based: 0 = Muharram, 1 = Safar, ..., 8 = Ramadan, ..., 11 = Dhu al-Hijjah
 */
object IslamicEventsProvider {

    // Map of Month Index to Map of Day -> Event String
    private val hijriEvents: Map<Int, Map<Int, String>> = mapOf(
        0 to mapOf( // Muharram
            1 to "Islamic New Year.",
            10 to "Day of Ashura. Fasting is recommended. The day Musa (AS) was saved from Pharaoh, and the martyrdom of Husayn ibn Ali."
        ),
        1 to mapOf( // Safar
            28 to "Martyrdom of Imam Hasan (AS)." // Various opinions, just a major one
        ),
        2 to mapOf( // Rabi' al-Awwal
            12 to "Mawlid an-Nabi (Birth of Prophet Muhammad PBUH) [according to Sunni tradition]."
        ),
        6 to mapOf( // Rajab
            27 to "Isra and Mi'raj (The Night Journey and Ascension of Prophet Muhammad PBUH)."
        ),
        7 to mapOf( // Sha'ban
            15 to "Mid-Sha'ban (Laylat al-Bara'at). A night of forgiveness and mercy."
        ),
        8 to mapOf( // Ramadan
            1 to "First day of Ramadan. Fasting begins.",
            17 to "The Battle of Badr (2 AH).",
            20 to "Conquest of Makkah (8 AH).",
            27 to "Observed as Laylat al-Qadr (The Night of Decree) by many, when the Quran was first revealed."
        ),
        9 to mapOf( // Shawwal
            1 to "Eid al-Fitr. Celebration marking the end of Ramadan."
        ),
        10 to mapOf( // Dhu al-Qi'dah
        ),
        11 to mapOf( // Dhu al-Hijjah
            8 to "First day of Hajj (Tarwiyah).",
            9 to "Day of Arafah. The most important day of Hajj. Fasting is highly recommended for non-pilgrims.",
            10 to "Eid al-Adha (Festival of Sacrifice) and the Day of Nahr.",
            11 to "First of the Days of Tashreeq.",
            12 to "Second of the Days of Tashreeq.",
            13 to "Third of the Days of Tashreeq."
        )
    )

    fun getEventForDate(monthIndex: Int, day: Int): String? {
        return hijriEvents[monthIndex]?.get(day)
    }
}
