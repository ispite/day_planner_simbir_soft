package ru.desol.example.dayplannersimbirsoft.data

data class Hour(
    val id: Int,
    val title: String,
    val doingsInHour: List<Doing>? = null
)