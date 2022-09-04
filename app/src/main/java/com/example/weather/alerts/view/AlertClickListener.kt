package com.example.weather.alerts.view

import com.example.weather.model.CustomAlert

interface AlertClickListener {
    fun onDeleteClick(customAlert: CustomAlert)
}