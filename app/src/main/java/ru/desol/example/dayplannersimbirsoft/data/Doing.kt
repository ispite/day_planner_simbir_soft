package ru.desol.example.dayplannersimbirsoft.data

import com.squareup.moshi.Json
import java.sql.Timestamp

data class Doing(
    @Json(name = "id")
    val id: Int,

    @Json(name = "date_start")
    val dateStart: Timestamp,

    @Json(name = "date_finish")
    val dateFinish: Timestamp,

    @Json(name = "name")
    val name: String,

    @Json(name = "description")
    val description: String
)
