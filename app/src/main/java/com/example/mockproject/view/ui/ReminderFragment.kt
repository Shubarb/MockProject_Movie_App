package com.example.mockproject.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.Model.Movie
import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.util.NotificationUtil
import com.example.mockproject.view.ReminderAdapter
import com.example.mockproject.view.interfaceview.ReminderListener
import com.example.mockproject.view.interfaceview.ToolbarTitleListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ReminderFragment(private var mDataBaseOpenHelper: DataBaseOpenHelper) : Fragment(),ReminderListener  {

    private lateinit var mReminderAdapter: ReminderAdapter
    private lateinit var mReminderRecyclerView: RecyclerView
    private lateinit var mMovieRemindList: ArrayList<Movie>
    private lateinit var mReminderListener: ReminderListener
    private lateinit var mToolbarTitleListener: ToolbarTitleListener

    fun setToolbarTitleListener(toolbarTitleListener: ToolbarTitleListener){
        this.mToolbarTitleListener = toolbarTitleListener
    }

    fun setRemindListener (reminderListener: ReminderListener){
        this.mReminderListener = reminderListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reminder, container, false)
        mMovieRemindList = mDataBaseOpenHelper.getListReminder()

        val linearLayoutManager = LinearLayoutManager(context)
        mReminderAdapter = ReminderAdapter(mMovieRemindList,ReminderAdapter.REMINDER_ALL,mDataBaseOpenHelper)
        mReminderAdapter.setReminderListener(this)
        mReminderRecyclerView = view.findViewById(R.id.rcv_view_reminder_all)
        mReminderRecyclerView.layoutManager = linearLayoutManager
        mReminderRecyclerView.setHasFixedSize(true)
        mReminderRecyclerView.adapter = mReminderAdapter
        mReminderAdapter.updateData(mMovieRemindList)
        setHasOptionsMenu(true)
        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }

    override fun onDetach() {
        mToolbarTitleListener.onUpdateToolbarTitle("Movie")
        super.onDetach()
    }

    private fun showAlertDialog(position: Int){
        val movie = mMovieRemindList[position]
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Delete Reminder")
            .setMessage("Do you want to cancel/delete this reminder?")
            .setNegativeButton("Cancel"){_,_ ->
//                NotificationUtil().cancelNotification(movie.id,requireContext())
//                mReminderAdapter.deleteItem(position)
////                mReminderAdapter.notifyDataSetChanged()
//                mReminderListener.onLoadReminder()
//                if(mMovieRemindList.size <= 0){
//                    requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
//                }
            }
            .setPositiveButton("Delete"){_,_ ->
                if(mDataBaseOpenHelper.deleteReminderByMovieId(movie.id)>-1){
                    NotificationUtil().cancelNotification(movie.id,requireContext())
                    mMovieRemindList.removeAt(position)
//                    mReminderAdapter.deleteItem(position)
                    mReminderAdapter.notifyDataSetChanged()
                    mReminderListener.onLoadReminder()
                    if(mMovieRemindList.size <= 0){
                        requireActivity().supportFragmentManager.beginTransaction().remove(this).commit()
                    }
                }

            }
            .show()
    }

    override fun onCLickItemReminder(movie: Movie) {
        mToolbarTitleListener.onUpdateToolbarTitle(movie.title)
        mReminderListener.onCLickItemReminder(movie)

    }

    override fun onLoadReminder() {
    }

    override fun onLongCLickItemReminder(position: Int): Boolean {
        showAlertDialog(position)
        return false
    }


}