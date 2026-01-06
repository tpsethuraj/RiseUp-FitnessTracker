package com.riseup_fitnesstracker.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.riseup_fitnesstracker.data.model.FitnessData

class FitnessPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)

    fun saveFitnessData(data: FitnessData) {
        prefs.edit().apply {
            putInt("steps", data.steps)
            putFloat("calories", data.calories)
            putFloat("distance", data.distance)
            putInt("water", data.waterIntake)
            apply()
        }
    }

    fun getFitnessData(): FitnessData {
        return FitnessData(
            steps = prefs.getInt("steps", 0),
            calories = prefs.getFloat("calories", 0f),
            distance = prefs.getFloat("distance", 0f),
            waterIntake = prefs.getInt("water", 0)
        )
    }

    fun setStepGoal(goal: Int) {
        prefs.edit().putInt("step_goal", goal).apply()
    }

    fun getStepGoal(): Int {
        return prefs.getInt("step_goal", 10000)
    }

    fun setWaterGoal(goal: Int) {
        prefs.edit().putInt("water_goal", goal).apply()
    }

    fun getWaterGoal(): Int {
        return prefs.getInt("water_goal", 2000)
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
    }

    fun getThemeMode(): String {
        return prefs.getString("theme_mode", "System") ?: "System"
    }
}
