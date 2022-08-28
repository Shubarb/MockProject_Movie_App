package com.example.mockproject.view.interfaceview

import android.graphics.Bitmap

interface ProfileListener {
    fun onSaveProfile(name: String, email: String, dob: String, gender: String, imgBitmap: Bitmap?)
}