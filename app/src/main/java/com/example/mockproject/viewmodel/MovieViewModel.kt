package com.example.mockproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mockproject.Model.Movie

class MovieViewModel: ViewModel() {
    var movielist: MutableLiveData<ArrayList<Movie>> = MutableLiveData()
    var movielistDB: MutableLiveData<ArrayList<Movie>> = MutableLiveData()
    var movieFavoriteNumber: MutableLiveData<Int> = MutableLiveData()

    init {
        movielist.value = ArrayList()
        movielistDB.value = ArrayList()
        movieFavoriteNumber.value = 0
    }
}