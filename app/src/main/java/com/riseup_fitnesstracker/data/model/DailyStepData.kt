package com.riseup_fitnesstracker.data.model

import java.time.LocalDate

data class DailyStepData(
    val dayName: String,
    val steps: Int,
    val goalReached: Boolean = false,
    val date: LocalDate? = null
)
