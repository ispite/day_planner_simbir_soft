package ru.desol.example.dayplannersimbirsoft.data

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

@Parcelize
data class Doing(
    @Json(name = "id")
    val id: Long,

    @Json(name = "date_start")
    val dateStart: Timestamp,

    @Json(name = "date_finish")
    val dateFinish: Timestamp,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String
) : Parcelable
