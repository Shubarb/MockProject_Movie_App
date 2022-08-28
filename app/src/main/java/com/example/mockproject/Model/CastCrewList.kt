package com.example.mockproject.Model

import com.google.gson.annotations.SerializedName

data class CastCrewList (
    @SerializedName("id") var id: Int? = null,
    @SerializedName("cast") var castList: List<CastAndCrew> = arrayListOf(),
    @SerializedName("crew") var crewList: List<CastAndCrew> = arrayListOf()
)