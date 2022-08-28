package com.example.androidtesst.view.ui

import android.content.SharedPreferences
import android.icu.text.StringPrepParseException
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.androidtesst.view.interfaceview.HomeListener
import com.example.mockproject.API.ApiData
import com.example.mockproject.API.ApiInterface
import com.example.mockproject.API.RetrofitClient
import com.example.mockproject.Constant.APIConstant
import com.example.mockproject.Constant.Constant
import com.example.mockproject.Model.Movie
import com.example.mockproject.Model.MovieList
import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.view.MovieAdapter
import com.example.mockproject.view.interfaceview.BadgeListener
import com.example.mockproject.view.interfaceview.DetailListener
import com.example.mockproject.view.interfaceview.ReminderListener
import com.example.mockproject.view.ui.DetailFragment
import com.example.mockproject.viewmodel.MovieViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment(
    private var mScreenType: Int,
    private var mDatabaseOpenHelper: DataBaseOpenHelper,
//    private var mMovieDetailFragment: DetailFragment,
    private var mMovieCategory: String,
    ) : Fragment(),View.OnClickListener {
    private var mViewType: Int = MovieAdapter.TYPE_LIST
    private val mMovieViewModel: MovieViewModel by activityViewModels()
    private lateinit var mMovieRecyclerView: RecyclerView
    private lateinit var mMovieAdapter: MovieAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private lateinit var mGridLayoutManager: GridLayoutManager
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mProgressBar: ProgressBar

    private lateinit var mMovieList: ArrayList<Movie>
    private lateinit var mMovieListDB: ArrayList<Movie>
    private lateinit var mHandler: Handler

    private lateinit var mSharedPreferences: SharedPreferences
    private  var mRatePref: Int = 0
    private lateinit var mReleaseYearPref: String
    private lateinit var mSortByPref: String
    private var mPage = 0

    private lateinit var mHomeListener: HomeListener
    private lateinit var mBadgeListener: BadgeListener
    private lateinit var mDetailListener: DetailListener
    private lateinit var mReminderListener: ReminderListener

//    private lateinit var mMovieDetailFragment: FavoriteFragment

    fun setBadgeListener(badgeListener: BadgeListener){
        this.mBadgeListener = badgeListener
    }

    fun setHomeListener(homeListener: HomeListener){
        this.mHomeListener = homeListener
    }

    fun setDetailListener(detailListener: DetailListener) {
        this.mDetailListener = detailListener
    }

    fun updateMovieList(movie: Movie,isFavorite:Boolean){
        mMovieList[findById(movie.id)].isFavorite = isFavorite
        mMovieAdapter.notifyDataSetChanged()
    }

    fun setReminderListener(reminderListener: ReminderListener){
        this.mReminderListener = reminderListener
    }

    fun changeViewHome(){
        if(mMovieRecyclerView.layoutManager == mGridLayoutManager){
            mViewType = MovieAdapter.TYPE_LIST
            mMovieRecyclerView.layoutManager = mLinearLayoutManager

        }else{
            mViewType = MovieAdapter.TYPE_GRID
            mMovieRecyclerView.layoutManager = mGridLayoutManager
        }
        mMovieAdapter.setViewType(mViewType)
        mMovieRecyclerView.adapter = mMovieAdapter
        mMovieAdapter.notifyDataSetChanged()
    }

    fun setListMovieByCondition(){
        loadDataBySetting()
        updateMovieList()
        mHandler.postDelayed({
            getListMovieFromApi(false,false)
        },1000)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        mMovieList = ArrayList()
        mMovieRecyclerView = view.findViewById(R.id.list_recyclerview)
        mLinearLayoutManager = LinearLayoutManager(activity)
        mGridLayoutManager = GridLayoutManager(activity,2)
        mMovieAdapter = MovieAdapter(mMovieList,mViewType,this,false)
        if(mScreenType == 0){
            mMovieRecyclerView.layoutManager = mLinearLayoutManager
        }else{
            mMovieRecyclerView.layoutManager = mGridLayoutManager
        }
        mMovieRecyclerView.setHasFixedSize(true)
        mMovieRecyclerView.adapter = mMovieAdapter

        mSwipeRefreshLayout = view.findViewById(R.id.swipeLayout)
        mProgressBar = view.findViewById(R.id.progress_bar)
        mHandler = Handler(Looper.getMainLooper())
        mMovieListDB = mDatabaseOpenHelper.getListMovie()
        loadDataBySetting()
        updateMovieList()
        mHandler.postDelayed({
            getListMovieFromApi(false,false)
        },1000)

        mSwipeRefreshLayout.setOnRefreshListener {
            mHandler.postDelayed({
                updateMovieList()
                getListMovieFromApi(true,false)
            },1000)
        }
        loadMore()

        return view
    }

    private fun getListMovieFromApi(isRefresh: Boolean,isLoadMore: Boolean){
        if(isLoadMore){
            mPage += 1
        }else{
            if(!isRefresh){
                mProgressBar.visibility = View.VISIBLE
            }
        }
        var retrofit: ApiInterface =
            RetrofitClient().getRetrofitInstance().create(ApiInterface::class.java)
        val retrofiData = retrofit.getMovieList(mMovieCategory, APIConstant.API_KEY,"$mPage")
        retrofiData.enqueue(object : Callback<MovieList?> {
            override fun onResponse(call: Call<MovieList?>?, response: Response<MovieList?>?) {
                mMovieAdapter.removeItemLoading()
                val responseBody = response!!.body()
                val listMovieResult = responseBody?.result as ArrayList<Movie>
//                mMovieViewModel.movielist.value = listMovieResult
                mMovieList.addAll(listMovieResult)
                mMovieAdapter.setupMovieFavorite(mMovieListDB)

                mMovieAdapter.setupMovieBySetting(
                    mMovieList,
                    mRatePref,
                    mReleaseYearPref,
                    mSortByPref
                )
                if(mPage < responseBody.totalPages){
                    val loadMoreItem =
                        Movie(0,"0","0",0.0,"0","0",false,false,"0","0",false)
                    mMovieList.add(loadMoreItem)
                }
                mMovieAdapter.notifyDataSetChanged()
                if(!isLoadMore && !isRefresh){
                    mProgressBar.visibility = View.GONE
                }
                if(isRefresh){
                    mSwipeRefreshLayout.isRefreshing = false
                }

            }

            override fun onFailure(call: Call<MovieList?>?, t: Throwable?) {
                if(!isLoadMore){
                    mPage -= 1
                }else{
                    if(isRefresh){
                        mSwipeRefreshLayout.isRefreshing = false
                    }else{
                        mProgressBar.visibility = View.GONE
                    }
                }
            }

        })
    }

    private fun updateMovieList(){
        mPage = 1
        mMovieList = ArrayList()
        mLinearLayoutManager = LinearLayoutManager(activity)
        mGridLayoutManager = GridLayoutManager(activity,2)
        mMovieAdapter = MovieAdapter(mMovieList,mViewType,this,false)
        if(mViewType == MovieAdapter.TYPE_GRID){
            mMovieRecyclerView.layoutManager = mGridLayoutManager
        }else{
            mMovieRecyclerView.layoutManager = mLinearLayoutManager
        }
        mMovieRecyclerView.setHasFixedSize(true)
        mMovieRecyclerView.adapter = mMovieAdapter
    }

    private fun loadMore(){
        mMovieRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(isLastItemDisplaying(recyclerView)){
                    mHandler.postDelayed({
                        getListMovieFromApi(false,true)
                    },1000)
                }
            }
        })
    }

    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean{
        if(recyclerView.adapter!!.itemCount !=0){
            val lastVisibleItemPosition =
                (recyclerView.layoutManager as LinearLayoutManager)!!.findLastCompletelyVisibleItemPosition()
            if(lastVisibleItemPosition!=RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.adapter!!
                .itemCount -1
            )return true
        }
        return false
    }

    private fun findById(id:Int):Int{
        var position = -1
        val size = mMovieList.size
        for(i in 0 until size){
            if(mMovieList[i].id == id){
                position = i
                break
            }
        }
        return position
    }

    override fun onClick(p0: View) {

        when(p0.id){
            R.id.img_btn -> {
                val position = p0.tag as Int
                val movie: Movie =  mMovieList[position]
                Log.d("TAGTAG","onCLick: "+ movie.id)
                if(movie.isFavorite){
                    if(mDatabaseOpenHelper.deleteMovie(movie.id)> -1){
                        movie.isFavorite = false
                        mMovieAdapter.notifyItemChanged(position)
                        mBadgeListener.onUpdateBadgeNumber(false)
                        mHomeListener.onUpdateFromMovie(movie,false)
                        Toast.makeText(p0.context,"Remove successfully ${movie.id}",Toast.LENGTH_SHORT).show()
                    }else {
                        Toast.makeText(p0.context, "Remove failed ${movie.id}", Toast.LENGTH_SHORT).show()
                    }
                    mHomeListener.onUpdateFromMovie(movie,false)
                }
                else{
                    if(mDatabaseOpenHelper.addMovie(movie) > -1){
                        movie.isFavorite = true
                        mMovieAdapter.notifyItemChanged(position)
                        mHomeListener.onUpdateFromMovie(movie,true)
                        mBadgeListener.onUpdateBadgeNumber(true)
                    }else{
                        Toast.makeText(p0.context, "Add failed ${movie.id}", Toast.LENGTH_SHORT).show()
                    }
                }

            }
            R.id.movie_item -> {
                val position = p0.tag as Int
                val movieDetail: Movie = mMovieList[position]
                val bundle= Bundle()
                bundle.putSerializable("movieDetail",movieDetail)
                val detailFragment = DetailFragment(mDatabaseOpenHelper)
                detailFragment.setDetailListener(mDetailListener)
                detailFragment.setBadgeListener(mBadgeListener)
                detailFragment.setRemindListener(mReminderListener)
                detailFragment.arguments = bundle
//                mMovieDetailFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_home,detailFragment,Constant.FRAGMENT_DETAIL_TAG)
                    addToBackStack(null)
                    commit()
                    mHomeListener.onUpdateTitleMovie(movieDetail.title,true)
                }
            }
        }
    }

    private fun loadDataBySetting(){
        mMovieCategory = mSharedPreferences.getString(Constant.PREF_CATEGORY_KEY,"popular").toString()
        mRatePref = mSharedPreferences.getInt(Constant.PREF_RATE_KEY,0)
        mReleaseYearPref = mSharedPreferences.getString(Constant.PREF_RELEASE_KEY,"").toString()
        mSortByPref = mSharedPreferences.getString(Constant.PREF_SORT_KEY,"").toString()
    }

}