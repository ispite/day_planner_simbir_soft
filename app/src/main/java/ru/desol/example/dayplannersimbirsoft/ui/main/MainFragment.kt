package ru.desol.example.dayplannersimbirsoft.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import ru.desol.example.dayplannersimbirsoft.R
import ru.desol.example.dayplannersimbirsoft.data.CalendarItem
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.CALENDAR_PROJECTION
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_ACCOUNT_NAME_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_ACCOUNT_TYPE_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_CALENDAR_COLOR_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_DISPLAY_NAME_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_ID_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_NAME_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_SYNC_EVENTS_INDEX
import ru.desol.example.dayplannersimbirsoft.data.CalendarProjectionContract.PROJECTION_VISIBLE_INDEX
import ru.desol.example.dayplannersimbirsoft.data.Doing
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.EVENT_PROJECTION
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_ALL_DAY_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_AVAILABILITY_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_DESCRIPTION
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_DISPLAY_COLOR_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_DTEND_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_DTSTART_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_DURATION_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_EVENT_LOCATION_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_RRULE_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_STATUS_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_TITLE_INDEX
import ru.desol.example.dayplannersimbirsoft.data.EventProjectionContract.PROJECTION_VISIBLE_INDEX_EVENT
import ru.desol.example.dayplannersimbirsoft.data.Hour
import ru.desol.example.dayplannersimbirsoft.databinding.FragmentMainBinding
import ru.desol.example.dayplannersimbirsoft.ui.main.adapters.HoursAdapter
import ru.desol.example.dayplannersimbirsoft.utils.map
import ru.desol.example.dayplannersimbirsoft.utils.toast
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone


class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(CreateMethod.INFLATE)

    private lateinit var hoursAdapter: HoursAdapter

    private val calendarItems = MutableLiveData<List<CalendarItem>?>()
    private val todoList = mutableListOf<Doing>()

    // Setup permission request launcher
    private var requestPermissionLauncher: ActivityResultLauncher<String>? =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it == true) getCalendars()
            else toast("Please allow this app to access your calendar")
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        setupObservers()

        setupRecyclerView()

        setupListeners()

        checkPermission()

        return binding.root
    }

    private fun setupRecyclerView() {
        hoursAdapter = HoursAdapter() { toDetailDoing(it) }
        with(binding.todoRecyclerView) {
            adapter = hoursAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupObservers() {
        calendarItems.observe(viewLifecycleOwner) {
            it?.let { getEvents(it) }
            setToday()
        }
    }


    private fun setToday() {
        val oneDay = 1000 * 60 * 60 * 24
        val today = binding.calendar.date
        val instant = Instant.ofEpochMilli(today)
        val todayDate =
            instant.atZone(TimeZone.getDefault().toZoneId()).toLocalDate().atStartOfDay()
        val todayMillis = todayDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val tomorrowMillis = todayMillis + oneDay

        val todayTimestamp = Timestamp(todayMillis)
        val tomorrowTimestamp = Timestamp(tomorrowMillis)

        fillRecyclerView(todayTimestamp, tomorrowTimestamp)
    }

    private fun setupListeners() {
        binding.calendar.setOnDateChangeListener { calendarView, year, month, day ->
            val date = LocalDate.of(year, month + 1, day).atStartOfDay()
            val dateEnd = LocalDate.of(year, month + 1, day + 1).atStartOfDay()
            val timeInMilliseconds = date.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
            val timeInMillisecondsEnd = dateEnd.atOffset(ZoneOffset.UTC).toInstant()
                .toEpochMilli()

            val todayTimestamp = Timestamp(timeInMilliseconds)
            val tomorrowTimestamp = Timestamp(timeInMillisecondsEnd)

            fillRecyclerView(todayTimestamp, tomorrowTimestamp)
        }
    }

    private fun fillDay(startOfDay: Timestamp, todayDoings: List<Doing>): List<Hour> {
        val millis = startOfDay.time
        val hourMultiplier = 1000 * 60 * 60
        val fullDay: MutableList<Hour> = MutableList(24) {
            val doingsInHour = mutableListOf<Doing>()
            for (doing in todayDoings) {
                val hour = millis + it * hourMultiplier
                val nextHour = millis + (it + 1) * hourMultiplier
                if ((hour) <= doing.dateStart.time &&
                    doing.dateStart.time < (nextHour)
                ) {
                    doingsInHour.add(doing)
                }

            }
            Hour(it, "Title: $it", doingsInHour)
        }
        return fullDay
    }

    private fun fillRecyclerView(todayTimestamp: Timestamp, tomorrowTimestamp: Timestamp) {
        val newList = todoList.filter {
            it.dateStart >= todayTimestamp && it.dateStart < tomorrowTimestamp
        }
        val hoursList = fillDay(todayTimestamp, newList)
        hoursAdapter.submitList(hoursList)
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

    private fun toDetailDoing(doing: Doing) {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToDoingDetailsFragment(doing)
        )
    }

    private fun getCalendars() {
        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection = ""
        val selectionArgs = emptyArray<String>()
        val cur = requireActivity().contentResolver.query(
            uri,
            CALENDAR_PROJECTION,
            selection, selectionArgs,
            null,
        )

        val newList = cur?.map {
            val calId = cur.getLong(PROJECTION_ID_INDEX)
            val displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val name = cur.getString(PROJECTION_NAME_INDEX)
            val color = cur.getInt(PROJECTION_CALENDAR_COLOR_INDEX)
            val visible = cur.getInt(PROJECTION_VISIBLE_INDEX)
            val syncEvents = cur.getInt(PROJECTION_SYNC_EVENTS_INDEX)
            val accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            val accountType = cur.getString(PROJECTION_ACCOUNT_TYPE_INDEX)

            CalendarItem(
                id = calId,
                name = name,
                displayName = displayName,
                color = color,
                visible = visible == 1,
                syncEvents = syncEvents == 1,
                accountName = accountName,
                accountType = accountType,
            )
        }
        cur?.close()
        calendarItems.postValue(newList)
    }

    private fun getEvents(calendarItems: List<CalendarItem>) {
        val selectionArgs = calendarItems.map { it.id.toString() }.toTypedArray()
        val size = calendarItems.size
        val uri = CalendarContract.Events.CONTENT_URI
        val stringTemplate = "(${CalendarContract.Events.CALENDAR_ID} = ?)"
        val or = " OR "

        val sb = StringBuilder()
        for (i in 0..<size) {
            if (i == 0) sb.append(stringTemplate)
            else sb.append(or).append(stringTemplate)
        }
        val selection = sb.toString()
        val cur = requireActivity().contentResolver.query(
            uri,
            EVENT_PROJECTION,
            selection,
            selectionArgs,
            null
        )
        val doings = cur?.map {
            val eventId = cur.getLong(PROJECTION_ID_INDEX)
            val title = cur.getStringOrNull(PROJECTION_TITLE_INDEX) ?: ""
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
            val description = cur.getStringOrNull(PROJECTION_DESCRIPTION) ?: ""

            val dateStart = Timestamp(dtStart ?: 0L)
            val dateFinish = Timestamp(dtEnd ?: 0L)
            Doing(
                id = eventId,
                dateStart = dateStart,
                dateFinish = dateFinish,
                name = title,
                description = description
            )
        }
        cur?.close()
        if (doings != null) {
            todoList.addAll(doings)
        }
    }
}