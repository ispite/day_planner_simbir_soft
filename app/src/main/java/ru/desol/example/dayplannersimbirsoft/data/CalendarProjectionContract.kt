package ru.desol.example.dayplannersimbirsoft.data

import android.provider.CalendarContract

object CalendarProjectionContract {
    val CALENDAR_PROJECTION = arrayOf(
        CalendarContract.Calendars._ID,
        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
        CalendarContract.Calendars.NAME,
        CalendarContract.Calendars.CALENDAR_COLOR,
        CalendarContract.Calendars.VISIBLE,
        CalendarContract.Calendars.SYNC_EVENTS,
        CalendarContract.Calendars.ACCOUNT_NAME,
        CalendarContract.Calendars.ACCOUNT_TYPE,
    )
    const val PROJECTION_ID_INDEX = 0
    const val PROJECTION_DISPLAY_NAME_INDEX = 1
    const val PROJECTION_NAME_INDEX = 2
    const val PROJECTION_CALENDAR_COLOR_INDEX = 3
    const val PROJECTION_VISIBLE_INDEX = 4
    const val PROJECTION_SYNC_EVENTS_INDEX = 5
    const val PROJECTION_ACCOUNT_NAME_INDEX = 6
    const val PROJECTION_ACCOUNT_TYPE_INDEX = 7
}