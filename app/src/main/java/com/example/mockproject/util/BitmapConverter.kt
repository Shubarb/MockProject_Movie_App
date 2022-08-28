package com.example.mockproject.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.*

class BitmapConverter {
    fun encodeBase64(image: Bitmap): String?{
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG,100,baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.encodeToString(b,Base64.DEFAULT)
    }

    fun decodeBase64(input: String?): Bitmap?{
        val decodeByte = Base64.decode(input,0)
        return BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.size)
    }
}