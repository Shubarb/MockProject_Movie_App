package com.example.mockproject.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.example.mockproject.BroadcastReceiver.AlarmReceiver
import com.example.mockproject.BroadcastReceiver.notificationID
import com.example.mockproject.Constant.Constant
import com.example.mockproject.Model.Movie

class NotificationUtil {
    fun createNotification(movie: Movie,reminderTime: Long,context: Context){
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra(Constant.BUNDLE_ID_KEY,movie.id)
        intent.putExtra(Constant.BUNDLE_TITLE_KEY,movie.title)
        intent.putExtra(Constant.BUNDLE_RATE_KEY,movie.voteAverage)
        intent.putExtra(Constant.BUNDLE_RELEASE_KEY,movie.releaseDate)

        val pendingIntent = PendingIntent.getBroadcast(
            context, movie.id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =  context.getSystemService(Context.ALARM_SERVICE)as AlarmManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                reminderTime,
                pendingIntent)
        }
    }

    fun cancelNotification(notificationID: Int,context: Context){
        val intent = Intent(context,AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, notificationID, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager =  context.getSystemService(Context.ALARM_SERVICE)as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}