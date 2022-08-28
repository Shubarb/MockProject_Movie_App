package com.example.mockproject.view.interfaceview

import com.example.mockproject.Model.Movie

interface FavoriteListener {
    fun onUpdateFromFavorite(movie: Movie)
}