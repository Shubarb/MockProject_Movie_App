package com.example.mockproject.view.interfaceview

import com.example.mockproject.Model.Movie

interface DetailListener {
    fun onUpdateFromDetail(movie: Movie, isFavorite: Boolean)
    fun onAddReminder()
    fun onUpdateTitleFromDetail(movieTitle: String)
}