package com.example.mockproject.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.mockproject.Model.Movie

class DataBaseOpenHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int,
    ): SQLiteOpenHelper(context,name,factory,version) {

        companion object{
            private var MOVIE_TABLE = "movie_table"
            private var MOVIE_ID = "movie_id"
            private var MOVIE_TITLE = "movie_name"
            private var MOVIE_RATING = "movie_rating"
            private var MOVIE_OVERVIEW = "movie_overview"
            private var MOVIE_DATE = "movie_date"
            private var MOVIE_IMAGE_POSTER = "movie_image"
            private var MOVIE_ADULT = "movie_adult"
            private var MOVIE_FAVORITE = "movie_favorite"
            private var REMINDER_TIME = "movie_reminder_time"
            private var REMINDER_TABLE = "reminder_table"
            private var REMINDER_DATE_HOUR = "movie_reminder_time_display"

            private var TAG = "MovieDB"

        }

    override fun onCreate(p0: SQLiteDatabase) {
        val createTableMovie = "CREATE TABLE $MOVIE_TABLE ( " +
                "$MOVIE_ID INTEGER PRIMARY KEY, " +
                "$MOVIE_TITLE TEXT, " +
                "$MOVIE_OVERVIEW TEXT, " +
                "$MOVIE_RATING REAL, " +
                "$MOVIE_DATE TEXT, " +
                "$MOVIE_IMAGE_POSTER TEXT, " +
                "$MOVIE_ADULT INTEGER, " +
                "$MOVIE_FAVORITE INTEGER )"

        val createTableReminder = "CREATE TABLE $REMINDER_TABLE ( " +
                "$MOVIE_ID INTEGER PRIMARY KEY, " +
                "$MOVIE_TITLE TEXT, " +
                "$MOVIE_OVERVIEW TEXT, " +
                "$MOVIE_RATING REAL, " +
                "$MOVIE_DATE TEXT, " +
                "$MOVIE_IMAGE_POSTER TEXT, " +
                "$MOVIE_ADULT INTEGER, " +
                "$MOVIE_FAVORITE INTEGER, " +
                "$REMINDER_TIME TEXT, " +
                "$REMINDER_DATE_HOUR TEXT)"

        p0.execSQL(createTableMovie)
        p0.execSQL(createTableReminder)
        Log.d("TAGTAGTAG10","onCreate: ")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        val dropTableMovie = "DROP TABLE IF EXISTS $MOVIE_TABLE"
        val dropTableReminder = "DROP TABLE IF EXISTS $REMINDER_TABLE"
        p0!!.execSQL(dropTableMovie)
        p0.execSQL(dropTableReminder)
        onCreate(p0)
    }

    fun addMovie(movie: Movie): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MOVIE_ID,movie.id)
        contentValues.put(MOVIE_TITLE, movie.title)
        contentValues.put(MOVIE_OVERVIEW, movie.overview)
        contentValues.put(MOVIE_RATING, movie.voteAverage)
        contentValues.put(MOVIE_DATE, movie.releaseDate)
        contentValues.put(MOVIE_IMAGE_POSTER, movie.posterPath)
        if(movie.adult){
            contentValues.put(MOVIE_ADULT,0)
        }else{
            contentValues.put(MOVIE_ADULT,1)
        }
        contentValues.put(MOVIE_FAVORITE,0)
        val success = db.insert(MOVIE_TABLE,null,contentValues)
        db.close()
        return success.toInt()
    }

    fun addReminder(movie: Movie): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MOVIE_ID,movie.id)
        contentValues.put(MOVIE_TITLE, movie.title)
        contentValues.put(MOVIE_OVERVIEW, movie.overview)
        contentValues.put(MOVIE_RATING, movie.voteAverage)
        contentValues.put(MOVIE_DATE, movie.releaseDate)
        contentValues.put(MOVIE_IMAGE_POSTER, movie.posterPath)
        if(movie.adult){
            contentValues.put(MOVIE_ADULT,0)
        }else{
            contentValues.put(MOVIE_ADULT,1)
        }
        if(movie.isFavorite)
            contentValues.put(MOVIE_FAVORITE,0)
        else
            contentValues.put(MOVIE_FAVORITE,1)

        contentValues.put(REMINDER_TIME, movie.reminderTime)
        contentValues.put(REMINDER_DATE_HOUR,movie.reminderTimeDisplay)
        val success = db.insert(REMINDER_TABLE,null,contentValues)
        db.close()
        return success.toInt()
    }

    fun updateReminder(movie: Movie): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(REMINDER_TIME, movie.reminderTime)
        contentValues.put(REMINDER_DATE_HOUR,movie.reminderTimeDisplay)
        val whereArgs = arrayOf<String>(java.lang.String.valueOf(movie.id))
        val success = db.update(REMINDER_TABLE,
            contentValues,
            "movie_id = ?", whereArgs)
        db.close()
        return success
    }

    fun getListMovie() : ArrayList<Movie>{
        val listMovie: ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $MOVIE_TABLE"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie: Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do{
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 0,
                    cursor.getInt(7) == 0
                )
                listMovie.add(movie)
            }while (cursor.moveToNext())
        }
        return listMovie
    }

    fun checkReminderExist(movieId:Int): Int{
        var listMovie: ArrayList<Movie> = ArrayList()
        val selectQuery =
            "SELECT * FROM $REMINDER_TABLE WHERE $MOVIE_ID = $movieId"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie: Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return 0
        }
        if(cursor.moveToFirst()){
            do{
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 0,
                    cursor.getInt(7) == 0
                )
                listMovie.add(movie)
                if(listMovie.size > 0) return 1
            }while (cursor.moveToNext())
        }
        return 0
    }

    fun getListReminder() : ArrayList<Movie>{
        val listMovie: ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $REMINDER_TABLE"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie: Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do{
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 0,
                    cursor.getInt(7) == 0,
                    cursor.getString(8),
                    cursor.getString(9)
                )
                listMovie.add(movie)
            }while (cursor.moveToNext())
        }
        return listMovie
    }

    fun getReminderMovieID(id: Int): ArrayList<Movie>{
        val movieReminderList: ArrayList<Movie> = ArrayList()
        val selectQuery = "SELECT * FROM $REMINDER_TABLE WHERE $MOVIE_ID = $id"
        val db = this.readableDatabase
        val cursor : Cursor
        var movie: Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return movieReminderList
        }
        if(cursor.moveToFirst()){
            do{
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 0,
                    cursor.getInt(7) == 0,
                    cursor.getString(8),
                    cursor.getString(9)
                )
                movieReminderList.add(movie)
            }while (cursor.moveToNext())
        }
        return movieReminderList

    }

    fun getListMovieByCondition(
        rate: String,
        releaseYear: String,
        sortBy: String
    ): ArrayList<Movie>{
        var listMovie: ArrayList<Movie> = ArrayList()
        var condition = ""
        if(rate != "" || releaseYear != "" || sortBy != ""){
            condition += "WHERE "
        }
        if(rate != "") condition += "$MOVIE_RATING <= rate"
        if(releaseYear != "") condition += "AND $MOVIE_DATE LIKE %$releaseYear%"
        if(sortBy != "") condition += "ORDER BY $sortBy ASC"
        val selectQuery = "SELECT * FROM $MOVIE_TABLE $condition"
        val db = this.readableDatabase
        val cursor: Cursor
        var movie: Movie
        try {
            cursor = db.rawQuery(selectQuery,null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if(cursor.moveToFirst()){
            do{
                movie = Movie(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getInt(6) == 0,
                    cursor.getInt(7) == 0
                )
                listMovie.add(movie)
            }while (cursor.moveToNext())
        }
        return listMovie
    }

    fun deleteMovie(id: Int):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MOVIE_ID,id)
        val success = db.delete(MOVIE_TABLE,"$MOVIE_ID = $id", null)
        db.close()
        return success
    }

    fun deleteReminderByMovieId(id: Int): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(MOVIE_ID,id)
        val success =
            db.delete(REMINDER_TABLE,"$MOVIE_ID = $id ", null)
        db.close()
        return success
    }
}