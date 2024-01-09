package ru.desol.example.dayplannersimbirsoft.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.desol.example.dayplannersimbirsoft.R
import ru.desol.example.dayplannersimbirsoft.data.CalendarItem
import ru.desol.example.dayplannersimbirsoft.data.Hour
import ru.desol.example.dayplannersimbirsoft.databinding.FragmentMainBinding
import ru.desol.example.dayplannersimbirsoft.ui.main.adapters.CalendarItemAdapter
import ru.desol.example.dayplannersimbirsoft.ui.main.adapters.HoursAdapter
import timber.log.Timber

class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(CreateMethod.INFLATE)

    // Todo lateinit или nullable?
//    private var hoursAdapter: HoursAdapter? = null
    private lateinit var hoursAdapter: HoursAdapter

    //    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var calendarItemAdapter: CalendarItemAdapter

    val calendarItems: MutableList<CalendarItem> = mutableListOf()


    // Setup permission request launcher
    private var requestPermissionLauncher: ActivityResultLauncher<String>? =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it == true) {
                getCalendars()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please allow this app to access your calendar",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupRecyclerView()

        // MOCK
        mockHours()
//        hoursAdapter.submitList(hoursList)

        checkPermission()

        return binding.root
    }

    private fun setupRecyclerView() {
//        hoursAdapter = HoursAdapter()
//        with(binding.todoRecyclerView) {
//            adapter = hoursAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }

//        calendarItemAdapter = CalendarItemAdapter()
//        with(binding.todoRecyclerView) {
//
//            adapter = calendarItemAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//            setHasFixedSize(true)
//        }


        hoursAdapter = HoursAdapter()
//        hoursAdapter = HoursAdapter()
        calendarItemAdapter = CalendarItemAdapter(::chooseCalendar)
        with(binding.todoRecyclerView) {
            adapter = ConcatAdapter(calendarItemAdapter, calendarItemAdapter)
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

    }

    private fun checkPermission() {
        requestPermissionLauncher?.launch(Manifest.permission.READ_CALENDAR)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCalendars()
        } else {
            requestPermissionLauncher?.launch(Manifest.permission.READ_CALENDAR)
        }
    }

    private val hoursList: MutableList<Hour> = mutableListOf()
    private fun mockHours() {
        for (i in 1..12) {
            hoursList.add(Hour(i, "Title: $i"))
        }
    }

    private fun chooseCalendar(calendarItemId: Long) {
        Timber.d("calendar item =$calendarItemId")
        calendarItemAdapter.clearList()
        getEvents(calendarItemId.toString())
    }

    private fun getCalendars() {
        calendarItemAdapter.clearList()
        calendarItems.clear()
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
//        val cur = requireContext().contentResolver.query(
        val cur = requireActivity().contentResolver.query(
            uri,
            CALEND_PROJECTION,
            selection, selectionArgs,
            null,
        )
        while (cur?.moveToNext() == true) {
            val calId = cur.getLong(PROJECTION_ID_INDEX)
            val displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val name = cur.getString(PROJECTION_NAME_INDEX)
            val color = cur.getInt(PROJECTION_CALENDAR_COLOR_INDEX)
            val visible = cur.getInt(PROJECTION_VISIBLE_INDEX)
            val syncEvents = cur.getInt(PROJECTION_SYNC_EVENTS_INDEX)
            val accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            val accountType = cur.getString(PROJECTION_ACCOUNT_TYPE_INDEX)

            val calendarItem = CalendarItem(
                id = calId,
                name = name,
                displayName = displayName,
                color = color,
                visible = visible == 1,
                syncEvents = syncEvents == 1,
                accountName = accountName,
                accountType = accountType,
            )
            calendarItems.add(calendarItem)
            calendarItemAdapter.addItemToList(calendarItem)
        }
        cur?.close()
//        calendarItems.forEach { Timber.d("calendarItems =$it") }
    }

    private fun getEvents(calendarId: String) {
//        eventItemAdapter.clearData()
//        calendarItems.forEach { Timber.d("calendarItems =$it") }

        val calendIds = calendarItems.map { it.id.toString() }.toTypedArray()
//        val calendIds = calendarItems.map { it.id.toString() }.joinToString(",")
        calendIds.forEach { Timber.d("calendIds =$it") }
        val uri = CalendarContract.Events.CONTENT_URI
//        val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?)"
        val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?) OR (${CalendarContract.Events.CALENDAR_ID} = ?) OR (${CalendarContract.Events.CALENDAR_ID} = ?)"
//        val selectionArgs = arrayOf(calendarId)
        val selectionArgs = calendIds
        val cur = requireActivity().contentResolver.query(
            uri,
            EVENT_PROJECTION,
            selection,
            selectionArgs,
//            null,
//            arrayOf("(${CalendarContract.Events.CALENDAR_ID} = ?)"),
//            arrayOf("?"),
            null,
        )
        while (cur?.moveToNext() == true) {
            val eventId = cur.getLong(PROJECTION_ID_INDEX)
            val title = cur.getStringOrNull(PROJECTION_TITLE_INDEX)
            val eventLocation = cur.getStringOrNull(PROJECTION_EVENT_LOCATION_INDEX)
            val status = cur.getIntOrNull(PROJECTION_STATUS_INDEX)
            val dtStart = cur.getLongOrNull(PROJECTION_DTSTART_INDEX)
            val dtEnd = cur.getLongOrNull(PROJECTION_DTEND_INDEX)
            val duration = cur.getStringOrNull(PROJECTION_DURATION_INDEX)
            val allDay = cur.getIntOrNull(PROJECTION_ALL_DAY_INDEX) == 1
            val availability = cur.getIntOrNull(PROJECTION_AVAILABILITY_INDEX)
            val rRule = cur.getStringOrNull(PROJECTION_RRULE_INDEX)
            val displayColor = cur.getIntOrNull(PROJECTION_DISPLAY_COLOR_INDEX)
            val visible = cur.getIntOrNull(PROJECTION_VISIBLE_INDEX_EVENT) == 1

//            eventItemAdapter.pushData(
//                EventItem(
//                    id = eventId,
//                    title = title,
//                    eventLocation = eventLocation,
//                    status = status,
//                    dtStart = dtStart,
//                    dtEnd = dtEnd,
//                    duration = duration,
//                    allDay = allDay,
//                    availability = availability,
//                    rRule = rRule,
//                    displayColor = displayColor,
//                    visible = visible,
//                )
//            )
            Timber.d(
                "eventId =$eventId title =$title eventLocation =$eventLocation" +
                        "status =$status dtStart =$dtStart dtEnd =$dtEnd duration =$duration" +
                        "allDay =$allDay availability =$availability rRule =$rRule " +
                        "displayColor =$displayColor visible =$visible"
            )
        }
        cur?.close()
    }

    companion object {
        private val CALEND_PROJECTION = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE,
            CalendarContract.Calendars.SYNC_EVENTS,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.ACCOUNT_TYPE,
        )
        private const val PROJECTION_ID_INDEX = 0
        private const val PROJECTION_DISPLAY_NAME_INDEX = 1
        private const val PROJECTION_NAME_INDEX = 2
        private const val PROJECTION_CALENDAR_COLOR_INDEX = 3
        private const val PROJECTION_VISIBLE_INDEX = 4
        private const val PROJECTION_SYNC_EVENTS_INDEX = 5
        private const val PROJECTION_ACCOUNT_NAME_INDEX = 6
        private const val PROJECTION_ACCOUNT_TYPE_INDEX = 7


        private val EVENT_PROJECTION = arrayOf(
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
        private const val PROJECTION_TITLE_INDEX = 1
        private const val PROJECTION_EVENT_LOCATION_INDEX = 2
        private const val PROJECTION_STATUS_INDEX = 3
        private const val PROJECTION_DTSTART_INDEX = 4
        private const val PROJECTION_DTEND_INDEX = 5
        private const val PROJECTION_DURATION_INDEX = 6
        private const val PROJECTION_ALL_DAY_INDEX = 7
        private const val PROJECTION_AVAILABILITY_INDEX = 8
        private const val PROJECTION_RRULE_INDEX = 9
        private const val PROJECTION_DISPLAY_COLOR_INDEX = 10
        private const val PROJECTION_VISIBLE_INDEX_EVENT = 11
    }
}