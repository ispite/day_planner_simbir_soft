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
import timber.log.Timber
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone


class MainFragment : Fragment(R.layout.fragment_main) {

    private val binding: FragmentMainBinding by viewBinding(CreateMethod.INFLATE)

    // Todo lateinit или nullable?
//    private var hoursAdapter: HoursAdapter? = null
    private lateinit var hoursAdapter: HoursAdapter

//    private lateinit var calendarItemAdapter: CalendarItemAdapter

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

//        getEvents()
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
            getEvents()
            setToday()
        }
    }

    val second = 1000

    private fun setToday() {
        val oneDay = 1000 * 60 * 60 * 24
        val today = binding.calendar.date
//        val asd = binding.calendar.dateTextAppearance

//        val todayTimestamp = Timestamp(today)
        val instant = Instant.ofEpochMilli(today)
        val todayDate =
            instant.atZone(TimeZone.getDefault().toZoneId()).toLocalDate().atStartOfDay()
        val todayMillis = todayDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val tomorrowMillis = todayMillis + oneDay
//        val todayMillis = todayDate.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()

        Timber.d("today =$today todayMillis =$todayMillis")

//        val date = LocalDate.of(year, month + 1, day).atStartOfDay()
//        val dateEnd = LocalDate.of(year, month + 1, day + 1).atStartOfDay()
//        val timeInMilliseconds = date.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
//        val timeInMillisecondsEnd = dateEnd.atOffset(ZoneOffset.UTC).toInstant()
//            .toEpochMilli() //- second // отнимаю секунду
//            Timber.d("Date in milli :: FOR API >= 26 >>> $timeInMilliseconds")
        // TODO получить текущую дату и события к ней
        val todayTimestamp = Timestamp(todayMillis)
        val tomorrowTimestamp = Timestamp(tomorrowMillis)
        Timber.d("todayMillis =$todayMillis tomorrowMillis =$tomorrowMillis")
//            Timber.d("timeInMillisecondsEnd =$timeInMillisecondsEnd")
//            todoList.map { it.dateStart }
        // TODO подозрительное решение
//        val newList = todoList.filter {
//                Timber.d("dateStart =${it.dateStart.time}")
////                it.dateStart in todayTimestamp..tomorrowTimestamp
////                it.dateStart in todayTimestamp..tomorrowTimestamp
//            it.dateStart >= todayTimestamp && it.dateStart < tomorrowTimestamp
//        }
////            Timber.d("dateStart =${it.dateStart.time}")
//        newList.forEach { Timber.d("dateStart =${it.dateStart} millis =${it.dateStart.time}") }
//        Timber.d("newList =$newList")
//        val hoursList = fillDay(todayTimestamp, newList)
//        hoursAdapter.submitList(hoursList)

        fillRecyclerView(todayTimestamp, tomorrowTimestamp)
    }

    private fun setupListeners() {
        binding.calendar.setOnDateChangeListener { calendarView, year, month, day ->
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val date = LocalDate.of(year, month + 1, day).atStartOfDay()
            val dateEnd = LocalDate.of(year, month + 1, day + 1).atStartOfDay()
            val timeInMilliseconds = date.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
            val timeInMillisecondsEnd = dateEnd.atOffset(ZoneOffset.UTC).toInstant()
                .toEpochMilli() //- second // отнимаю секунду
//            Timber.d("Date in milli :: FOR API >= 26 >>> $timeInMilliseconds")
            // TODO получить текущую дату и события к ней
            val todayTimestamp = Timestamp(timeInMilliseconds)
            val tomorrowTimestamp = Timestamp(timeInMillisecondsEnd)
            Timber.d("year =$year month =$month day =$day timeInMilliseconds =$timeInMilliseconds")
//            Timber.d("timeInMillisecondsEnd =$timeInMillisecondsEnd")
//            todoList.map { it.dateStart }
            // TODO подозрительное решение
//            val newList = todoList.filter {
////                Timber.d("dateStart =${it.dateStart.time}")
////                it.dateStart in todayTimestamp..tomorrowTimestamp
////                it.dateStart in todayTimestamp..tomorrowTimestamp
//                it.dateStart >= todayTimestamp && it.dateStart < tomorrowTimestamp
//            }
////            Timber.d("dateStart =${it.dateStart.time}")
//            newList.forEach { Timber.d("dateStart =${it.dateStart} millis =${it.dateStart.time}") }
//            Timber.d("newList =$newList")
//            val hoursList = fillDay(todayTimestamp, newList)
//            hoursAdapter.submitList(hoursList)

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
                    Timber.d("doing =${doing.dateStart.time} hour =$hour nextHour =$nextHour")
                    doingsInHour.add(doing)
                }

            }
            Hour(it, "Title: $it", doingsInHour)
        }
        return fullDay
    }

    private fun fillRecyclerView(todayTimestamp: Timestamp, tomorrowTimestamp: Timestamp) {
        val newList = todoList.filter {
            Timber.d("dateStart =${it.dateStart.time}")
            it.dateStart >= todayTimestamp && it.dateStart < tomorrowTimestamp
        }
//            Timber.d("dateStart =${it.dateStart.time}")
        newList.forEach { Timber.d("dateStart =${it.dateStart} millis =${it.dateStart.time}") }
        Timber.d("newList =$newList")
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

    // TODO доделать переход
    private fun toDetailDoing(doing: Doing) {

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
        newList?.forEach { Timber.d("calendars =$it") }
        calendarItems.postValue(newList)
    }

    private fun getEvents() {
        calendarItems.value?.forEach { Timber.d("calendarItem =$it") }
        val selectionArgs = calendarItems.value?.map { it.id.toString() }?.toTypedArray()
//        val selectionArgs = arrayOf("3")
        val uri = CalendarContract.Events.CONTENT_URI
//        val selection = "(${CalendarContract.Events.CALENDAR_ID} = ?)"
        val selection =
            "(${CalendarContract.Events.CALENDAR_ID} = ?) OR (${CalendarContract.Events.CALENDAR_ID} = ?) OR (${CalendarContract.Events.CALENDAR_ID} = ?)"
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

            Timber.d(
                "eventId =$eventId title =$title eventLocation =$eventLocation" +
                        "status =$status dtStart =$dtStart dtEnd =$dtEnd duration =$duration" +
                        "allDay =$allDay availability =$availability rRule =$rRule " +
                        "displayColor =$displayColor visible =$visible description =$description"
            )
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