package ru.desol.example.dayplannersimbirsoft.data

import android.provider.CalendarContract

object EventProjectionContract {
    val EVENT_PROJECTION = arrayOf(
        CalendarContract.Events._ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.EVENT_LOCATION,
        CalendarContract.Events.STATUS,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.DURATION,
        CalendarContract.Events.ALL_DAY,
        CalendarContract.Events.AVAILABILITY,
        CalendarContract.Events.RRULE,
        CalendarContract.Events.DISPLAY_COLOR,
        CalendarContract.Events.VISIBLE,
    )
    const val PROJECTION_TITLE_INDEX = 1
    const val PROJECTION_EVENT_LOCATION_INDEX = 2
    const val PROJECTION_STATUS_INDEX = 3
    const val PROJECTION_DTSTART_INDEX = 4
    const val PROJECTION_DTEND_INDEX = 5
    const val PROJECTION_DURATION_INDEX = 6
    const val PROJECTION_ALL_DAY_INDEX = 7
    const val PROJECTION_AVAILABILITY_INDEX = 8
    const val PROJECTION_RRULE_INDEX = 9
    const val PROJECTION_DISPLAY_COLOR_INDEX = 10
    const val PROJECTION_VISIBLE_INDEX_EVENT = 11
}