package com.example.mockproject.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.Constant.APIConstant
import com.example.mockproject.Model.Movie
import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.view.interfaceview.ReminderListener
import com.squareup.picasso.Picasso

class ReminderAdapter(
    private var listMovieReminder: ArrayList<Movie>,
    private var type: Int,
    private var dataBaseOpenHelper: DataBaseOpenHelper
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    companion object{
        const val REMINDER_ALL = 1
        const val REMINDER_PROFILE = 0
    }

    private lateinit var mReminderListener: ReminderListener

    fun setReminderListener(mReminderListener: ReminderListener){
        this.mReminderListener = mReminderListener
    }

    fun updateData(listMovieReminder: ArrayList<Movie>){
        this.listMovieReminder = listMovieReminder
        notifyDataSetChanged()
    }

    fun deleteItem(index: Int){
        val movie = listMovieReminder[index]
        if(dataBaseOpenHelper.deleteReminderByMovieId(movie.id)> -1){
            listMovieReminder.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ReminderHolder(
                type,
                mReminderListener,
                LayoutInflater.from(parent.context).inflate(R.layout.reminder_item,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReminderHolder).bindData(position,listMovieReminder[position])
    }

    override fun getItemCount(): Int {
        return if(type == REMINDER_ALL){
            listMovieReminder.size
        }else{
            if(listMovieReminder.size >3) 3 else listMovieReminder.size
        }
    }

    class ReminderHolder(
        private var reminderType: Int,
        var mReminderListener: ReminderListener,
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        private var imgPoster: ImageView = itemView.findViewById(R.id.poster_img_remind)
        private var title: TextView = itemView.findViewById(R.id.txt_title_remind)
        private var releaseData: TextView = itemView.findViewById(R.id.txt_release_remind)
        private var timeReminder: TextView = itemView.findViewById(R.id.txt_date_remind)

        fun bindData(position:Int,movie:Movie){
            if(reminderType == REMINDER_PROFILE){
                imgPoster.visibility = View.GONE
            }else{
                imgPoster.visibility = View.VISIBLE
                val url = APIConstant.BASE_IMG_URL + movie.posterPath
                Picasso.get().load(url).into(imgPoster)
                itemView.setOnClickListener{ mReminderListener.onCLickItemReminder(movie) }
                itemView.setOnLongClickListener{ mReminderListener.onLongCLickItemReminder(position) }
            }

            title.text = movie.title
            "${movie.releaseDate}".also { releaseData.text = it }
            timeReminder.text = movie.reminderTimeDisplay
        }
    }



}