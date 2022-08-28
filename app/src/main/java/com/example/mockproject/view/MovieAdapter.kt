package com.example.mockproject.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mockproject.Constant.APIConstant
import com.example.mockproject.Model.Movie
import com.example.mockproject.Model.MovieList
import com.example.mockproject.R
import com.squareup.picasso.Picasso
import java.lang.Exception

class MovieAdapter(
    private var mlistMovie: MutableList<Movie>,
    private var mViewType: Int,
    private var mViewClickListener: View.OnClickListener,
    private var mIsFavoriteList: Boolean
) :RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    companion object{
        const val TYPE_LIST = 0
        const val TYPE_GRID = 1
        const val TYPE_LOADING_LIST = 2
        const val TYPE_LOADING_GRID = 3
    }

    fun updateData(listMovie: MutableList<Movie>){
        this.mlistMovie = listMovie
    }

    fun setViewType(viewType: Int){
        this.mViewType = viewType
    }

    fun removeItemLoading(){
        if(mlistMovie.isNotEmpty()){
            val lastPosition = mlistMovie.size - 1
            mlistMovie.removeAt(lastPosition)
            notifyDataSetChanged()
        }
    }

    fun setupMovieFavorite(listMovieFav: ArrayList<Movie>){
        for(i in 0 until mlistMovie.size){
            for(j in 0 until listMovieFav.size){
                if(mlistMovie[i].id == listMovieFav[j].id){
                    mlistMovie[i].isFavorite = true
                }
            }

        }
    }

    fun setupMovieBySetting(
        listMovie: ArrayList<Movie>,
        rate: Int,
        releaseYear: String,
        sortBy: String
    ){

        listMovie.removeAll{it.voteAverage < rate}

        val convertYear: Int?= if(releaseYear.length >3){
            releaseYear.substring(0,4).trim().toIntOrNull()
        }else{
            null
        }

        if(convertYear != null){
            listMovie.removeAll{
                it.releaseDate.substring(0,4).trim() != releaseYear
            }
        }

        if(sortBy == "Release Date")
            listMovie.sortByDescending { it.releaseDate }
        else if (sortBy == "Rating")
            listMovie.sortByDescending { it.voteAverage }

        updateData(listMovie)
    }

    override fun getItemCount(): Int {
        return  mlistMovie.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(!mIsFavoriteList && mlistMovie.isNotEmpty() && position == mlistMovie.size -1 && mViewType == TYPE_LIST ){
            TYPE_LOADING_LIST
        }else if(!mIsFavoriteList && mlistMovie.isNotEmpty() && position == mlistMovie.size -1 && mViewType == TYPE_GRID ) {
            TYPE_LOADING_GRID
        }else{
            mViewType
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == TYPE_LIST) {
            ListViewHolder(
                mViewClickListener,mlistMovie,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item, parent, false)
            )
        }else if(viewType == TYPE_GRID){
            GridViewHolder(
                mlistMovie,
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item_grid,parent,false)
            )
        }else if(viewType == TYPE_LOADING_LIST) {
            LoadListViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item_load,parent,false)
            )
        }else{
            LoadGridViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.movie_item_load_grid,parent,false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.tag = position
        holder.itemView.setOnClickListener(mViewClickListener)
        if(holder is GridViewHolder){
            holder.bindata(position)
        }else if(holder is ListViewHolder){
            holder.bindData(position)
        }
    }

    class GridViewHolder(
        private var movieList: MutableList<Movie>,
        itemView: View): RecyclerView.ViewHolder(itemView){
        private var imgMovie: ImageView =
            itemView.findViewById(R.id.img_movie_grid)
        private var tvTitle: TextView =
            itemView.findViewById(R.id.tv_title_grid)

        fun bindata(position: Int){
            val movie = movieList[position]
            val url = APIConstant.BASE_IMG_URL + movie.posterPath
            Picasso.get().load(url).into(imgMovie)
            tvTitle.text = movie.title
        }
    }

    class ListViewHolder(private var mViewClickListener: View.OnClickListener,
        private var movieList: MutableList<Movie>,
        itemView: View):
            RecyclerView.ViewHolder(itemView){

        private var itemTitle: TextView =
            itemView.findViewById(R.id.tv_title)
        private var itemImgMovie: ImageView =
            itemView.findViewById(R.id.img_Movie)
        private var itemdate: TextView =
            itemView.findViewById(R.id.tv_date)
        private var itemrate: TextView =
            itemView.findViewById(R.id.tv_rate)
        private var itemAdult: ImageView =
            itemView.findViewById(R.id.img_adult)
        private var itemImgFavorite: ImageButton =
            itemView.findViewById(R.id.img_btn)
        private var itemOverView: TextView =
            itemView.findViewById(R.id.tv_overview)

        fun bindData(position: Int){
            val movie = movieList[position]
            itemTitle.text = movie.title
            val urlImage = APIConstant.BASE_IMG_URL + movie.posterPath
            Picasso.get().load(urlImage).into(itemImgMovie)
            itemdate.text = movie.releaseDate
            "${movie.voteAverage}/10".also{ itemrate.text = it }
            if(movie.adult){
                itemAdult.visibility = View.VISIBLE
            }else{
                itemAdult.visibility = View.GONE
            }
            itemOverView.text = movie.overview
            if(movie.isFavorite){
                itemImgFavorite.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }else{
                itemImgFavorite.setImageResource(R.drawable.ic_baseline_star_outline_24)
            }
            itemImgFavorite.tag = position
            itemImgFavorite.setOnClickListener(mViewClickListener)
        }
    }

    class LoadListViewHolder (itemView: View): RecyclerView.ViewHolder(itemView)
    class LoadGridViewHolder (itemView: View): RecyclerView.ViewHolder(itemView)
}