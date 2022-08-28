package com.example.androidtesst.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.QuickContactBadge
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.API.ApiData
import com.example.mockproject.Model.Movie
import com.example.mockproject.Model.MovieList

import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.view.MovieAdapter
import com.example.mockproject.view.interfaceview.BadgeListener
import com.example.mockproject.view.interfaceview.FavoriteListener
import com.example.mockproject.viewmodel.MovieViewModel


class FavoriteFragment(
    private var mDataBaseOpenHelper: DataBaseOpenHelper,
//    private var mMovieDetailFragment: DetailFragment
    private var mMovieFavoriteList: ArrayList<Movie>
) : Fragment(), View.OnClickListener{
    private val mMovieViewModel: MovieViewModel by activityViewModels()
    private lateinit var mMovieRecyclerView: RecyclerView
    private lateinit var mMovieAdapter: MovieAdapter

    private lateinit var mBadgeListener: BadgeListener
    private lateinit var mHomeFavoriteListener: FavoriteListener

    fun setBadgeListener(badgeListener: BadgeListener){
        this.mBadgeListener = badgeListener
    }

    fun setFavoriteListener(favoriteListener: FavoriteListener){
        this.mHomeFavoriteListener = favoriteListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       var view = inflater.inflate(R.layout.fragment_favorite, container, false)
        mMovieRecyclerView = view.findViewById(R.id.list_recyclerview)
        loadFavoriteList()
//        mMovieViewModel.movielistDB.value = mMovieFavoriteList
//        mMovieViewModel.movielistDB.observe(requireActivity(),{
//            mMovieFavoriteList = it
//            loadFavoriteList()
//        })
        setHasOptionsMenu(true)
        return view
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.img_btn -> {
                val position = p0.tag as Int
                val movie: Movie =  mMovieFavoriteList[position]
                if(movie.isFavorite){
                    if(mDataBaseOpenHelper.deleteMovie(movie.id)> -1){
                        movie.isFavorite = false
                        mMovieFavoriteList.remove(movie)
                        mMovieAdapter.notifyDataSetChanged()
                        mBadgeListener.onUpdateBadgeNumber(false)
                        mHomeFavoriteListener.onUpdateFromFavorite(movie)
                        Toast.makeText(p0.context,"Remove successfully ${movie.id}", Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(p0.context, "Remove failed ${movie.id}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }

    private fun loadFavoriteList(){
        mMovieAdapter = MovieAdapter(mMovieFavoriteList,MovieAdapter.TYPE_LIST,this,true)
        mMovieRecyclerView.layoutManager = LinearLayoutManager(activity)
        mMovieRecyclerView.setHasFixedSize(true)
        mMovieRecyclerView.adapter = mMovieAdapter
    }

    fun updateFavoriteList(movie: Movie,isFavorite: Boolean){
        var position = -1
        val size = mMovieFavoriteList.size
        for(i in 0 until size){
            if(mMovieFavoriteList[i].id == movie.id){
                position = i
                break
            }
        }
        if(isFavorite){
            mMovieFavoriteList.add(movie)
        }else{
            if(position != -1){
                mMovieFavoriteList.removeAt(position)
            }
        }
        loadFavoriteList()
    }
}