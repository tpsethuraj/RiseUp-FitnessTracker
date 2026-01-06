package com.riseup_fitnesstracker.data.model

data class FitnessData(
    val steps: Int = 0,
    val calories: Float = 0f,
    val distance: Float = 0f, // in meters
    val waterIntake: Int = 0 // in ml
)
