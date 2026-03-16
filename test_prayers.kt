import com.ub.islamicapp.utils.PrayerTimeCalculator

fun main() {
    val map = PrayerTimeCalculator.getPrayerTimes(2026, 3, 15, 33.5651, 73.0169, 5.0)
    println(map)
}
