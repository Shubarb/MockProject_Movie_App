package com.example.mockproject.view.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.API.ApiInterface
import com.example.mockproject.API.RetrofitClient
import com.example.mockproject.BroadcastReceiver.*
import com.example.mockproject.Constant.APIConstant
import com.example.mockproject.Constant.Constant
import com.example.mockproject.Model.CastAndCrew
import com.example.mockproject.Model.CastCrewList
import com.example.mockproject.Model.Movie
import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.util.NotificationUtil
import com.example.mockproject.view.CastAndCrewAdapter
import com.example.mockproject.view.interfaceview.BadgeListener
import com.example.mockproject.view.interfaceview.DetailListener
import com.example.mockproject.view.interfaceview.ReminderListener
import com.example.mockproject.view.interfaceview.ToolbarTitleListener
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class DetailFragment(private var mDataBaseOpenHelper: DataBaseOpenHelper) : Fragment()
    ,View.OnClickListener{

    private lateinit var mMovie: Movie
    private lateinit var mCastAndCrewList: ArrayList<CastAndCrew>

    private lateinit var mFavoriteBtn: ImageButton
    private lateinit var mDateText: TextView
    private lateinit var mRateText: TextView
    private lateinit var mPosterImg: ImageView
    private lateinit var mOverview: TextView
    private lateinit var mReminderBtn: ImageView
    private lateinit var mReminderTxt: TextView
    private lateinit var mCastRecyclerView: RecyclerView
    private lateinit var mCastAndCrewAdapter: CastAndCrewAdapter

    private var mReminderExisted : Boolean = false
    private lateinit var mMovieReminder : Movie

    private var mSaveDay= 0
    private var mSaveMonth= 0
    private var mSaveYear= 0
    private var mSaveHour= 0
    private var mSaveMinute= 0

    private lateinit var mBadgeListener: BadgeListener
    private lateinit var mDetailListener: DetailListener
    private lateinit var mReminderListener: ReminderListener
    private lateinit var mToolbarTitleListener: ToolbarTitleListener

    fun setToolbarTitleListener(toolbarTitleListener: ToolbarTitleListener){
        this.mToolbarTitleListener = toolbarTitleListener
    }

    fun setBadgeListener(badgeListener: BadgeListener){
        this.mBadgeListener = badgeListener
    }

    fun setRemindListener(remindListener: ReminderListener){
        this.mReminderListener = remindListener
    }

    fun updateMovie(movieId: Int){
        if(movieId == mMovie.id){
            mMovie.isFavorite = false
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
    }

    fun setDetailListener(detailListener: DetailListener){
        this.mDetailListener = detailListener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_detail, container, false)
        val bundle = this.arguments
        if(bundle != null){
            mMovie = bundle.getSerializable("movieDetail")as Movie
            val movieReminderList = mDataBaseOpenHelper.getReminderMovieID(mMovie.id)
            if(movieReminderList.isEmpty()){
                mReminderExisted = false
            }else{
                mReminderExisted = true
                mMovieReminder = movieReminderList[0]
            }
        }

        mFavoriteBtn = view.findViewById(R.id.btn_favorite_detail)
        mDateText = view.findViewById(R.id.txt_release_detail)
        mRateText = view.findViewById(R.id.txt_rate_detail)
        mPosterImg = view.findViewById(R.id.img_poster_detail)
        mReminderBtn = view.findViewById(R.id.btn_reminder_detail)
        mReminderTxt = view.findViewById(R.id.reminder_time_text)
        mOverview = view.findViewById(R.id.txt_overview_detail)
        mCastRecyclerView = view.findViewById(R.id.rcv_cast_detail)

        if(mMovie.isFavorite){
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_rate_24)
        }else
            mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)

        mFavoriteBtn.setOnClickListener(this)
        mDateText.text = mMovie.releaseDate
        "${mMovie.voteAverage}/10".also { mRateText.text = it }
        val url =APIConstant.BASE_IMG_URL + mMovie.posterPath
        Picasso.get().load(url).into(mPosterImg)
        mReminderBtn.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                createReminder()
            }
        }
        if(mReminderExisted){
            mReminderTxt.visibility = View.VISIBLE
            mReminderTxt.text = mMovieReminder.reminderTimeDisplay
        }else{
            mReminderTxt.visibility = View.GONE
        }
        mOverview.text = mMovie.overview
        mCastAndCrewList = arrayListOf()
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mCastAndCrewAdapter = CastAndCrewAdapter(mCastAndCrewList)
        mCastRecyclerView.layoutManager = layoutManager
        mCastRecyclerView.setHasFixedSize(true)
        mCastRecyclerView.adapter = mCastAndCrewAdapter
        getCastAndCrewFromApi()

        return view
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.btn_favorite_detail -> {
                if(mMovie.isFavorite){
                    if(mDataBaseOpenHelper.deleteMovie(mMovie.id) > -1){
                        mMovie.isFavorite = false
                        Toast.makeText(p0.context,"Remove successfully ${mMovie.id}",Toast.LENGTH_SHORT).show()
                        mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_outline_24)
                        mDetailListener.onUpdateFromDetail(mMovie,false)
                        mBadgeListener.onUpdateBadgeNumber(false)
                    } else{
                    Toast.makeText(p0.context,"Remove failed ${mMovie.id}",Toast.LENGTH_SHORT).show()
                }
            }else{
                if(mDataBaseOpenHelper.addMovie(mMovie) > -1){
                    mMovie.isFavorite = true
                    mFavoriteBtn.setImageResource(R.drawable.ic_baseline_star_rate_24)
                    mDetailListener.onUpdateFromDetail(mMovie,true)
                    mBadgeListener.onUpdateBadgeNumber(true)
                }else
                    Toast.makeText(p0.context,"Add failed ${mMovie.id}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getCastAndCrewFromApi(){
        var retrofit: ApiInterface =
            RetrofitClient().getRetrofitInstance().create(ApiInterface::class.java)
        val retrofiData = retrofit.getCastAndCrew(mMovie.id, APIConstant.API_KEY)
        retrofiData.enqueue(object : Callback<CastCrewList?> {
            override fun onResponse(call: Call<CastCrewList?>?, response: Response<CastCrewList?>?) {
                val responseBody = response!!.body()
                mCastAndCrewList.addAll(responseBody!!.castList)
                mCastAndCrewList.addAll(responseBody.crewList)
//                mCastAndCrewAdapter.updateList(mCastAndCrewList)
                mCastAndCrewAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<CastCrewList?>?, t: Throwable?) {
            }

        })
    }

    override fun onPause() {
        super.onPause()
        mDetailListener.onUpdateTitleFromDetail("Home")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createReminder(){
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(requireContext(),{ _,year,month,day ->
            TimePickerDialog(requireContext(),{_,hour,minute ->
                val pickedDateTime = Calendar.getInstance()
                pickedDateTime.set(year,month,day,hour,minute)
                mSaveYear =year
                mSaveMonth = month
                mSaveDay = day
                mSaveHour = hour
                mSaveMinute = minute
                currentDateTime.set(mSaveYear,mSaveMonth,mSaveDay,mSaveHour,mSaveMinute)
                val reminderTimeInMillis: Long = currentDateTime.timeInMillis
                val reminderTimeDisplay = "$mSaveYear/${mSaveMonth+1}/$mSaveDay  $mSaveHour:$mSaveMinute"
                mReminderTxt.visibility = View.VISIBLE
                mReminderTxt.text = reminderTimeDisplay

                mMovie.reminderTimeDisplay = reminderTimeDisplay
                mMovie.reminderTime = reminderTimeInMillis.toString()

                if(mReminderExisted){
//                    if(mDataBaseOpenHelper.checkReminderExist(mMovie.id) > 0){
//                        mDataBaseOpenHelper.deleteReminderByMovieId(mMovie.id)
                        if(mDataBaseOpenHelper.updateReminder(mMovie)>0){
                            NotificationUtil().cancelNotification(mMovie.id,requireContext())
                            NotificationUtil().createNotification(
                                mMovie,
                                reminderTimeInMillis,
                                requireContext()
                            )
                            mReminderListener.onLoadReminder()
                            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context,"update Reminder Failed",Toast.LENGTH_SHORT).show()
                        }
                }else{
                    if(mDataBaseOpenHelper.addReminder(mMovie)>0){
                        mReminderListener.onLoadReminder()
                        NotificationUtil().createNotification(
                            mMovie,
                            reminderTimeInMillis,
                            requireContext()
                        )
                        Toast.makeText(context,"Create new",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Add Reminder Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            },startHour,startMinute,true).show()
        },startYear,startMonth,startDay).show()
    }
}