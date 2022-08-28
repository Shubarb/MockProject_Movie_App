package com.example.androidtesst.view.interfaceview

import com.example.mockproject.Model.Movie

interface HomeListener {
    fun onUpdateFromMovie(movie: Movie,isFavorite:Boolean)
//    fun onUpdateListMovie(
//        typeMovie: String,
//        rate: String,
//        releaseYear: String,
//        sortBy: String
//    )
    fun onUpdateTitleMovie(movieTitle: String,isTitle: Boolean)
}