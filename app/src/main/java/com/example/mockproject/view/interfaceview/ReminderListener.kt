package com.example.mockproject.view.interfaceview

import com.example.mockproject.Model.Movie

interface ReminderListener {
    fun onCLickItemReminder(movie: Movie)
    fun onLoadReminder()
    fun onLongCLickItemReminder(position: Int): Boolean
}