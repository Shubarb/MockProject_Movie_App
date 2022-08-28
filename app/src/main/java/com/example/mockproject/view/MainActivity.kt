package com.example.androidtesst.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.androidtesst.view.ui.HomeFragment
import com.example.androidtesst.view.interfaceview.HomeListener
import com.example.androidtesst.view.ui.FavoriteFragment
import com.example.androidtesst.view.ui.InforFragment
import com.example.mockproject.BroadcastReceiver.channelID
import com.example.mockproject.Constant.APIConstant.Companion.PREFS_NAME
import com.example.mockproject.Constant.Constant
import com.example.mockproject.Model.Movie
import com.example.mockproject.R
import com.example.mockproject.database.DataBaseOpenHelper
import com.example.mockproject.util.BitmapConverter
import com.example.mockproject.view.ReminderAdapter
import com.example.mockproject.view.interfaceview.*
import com.example.mockproject.view.ui.DetailFragment
import com.example.mockproject.view.ui.EditProfileFragment
import com.example.mockproject.view.ui.ReminderFragment
import com.example.mockproject.view.ui.SettingsFragment
import com.google.android.material.tabs.TabLayout
import java.lang.Exception

class MainActivity : AppCompatActivity(),HomeListener,BadgeListener,ReminderListener,
    InformationListener,SettingListener,FavoriteListener ,DetailListener,ProfileListener,ToolbarTitleListener{

    private lateinit var mViewPager: ViewPager
    private lateinit var mTabLayout: TabLayout
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mNavigationView: NavigationView
    private lateinit var mToolbar: Toolbar
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mIconList: MutableList<Int>
    private lateinit var mMovieFavoriteList: ArrayList<Movie>
    private lateinit var mMovieReminderList: ArrayList<Movie>
    private lateinit var mViewPageAdapter: ViewPageAdapter

    private lateinit var mHomeFragment: HomeFragment
    private lateinit var mFavoriteFragment: FavoriteFragment
    private lateinit var mSettingsFragment: SettingsFragment
    private lateinit var mInforFragment: InforFragment
    private lateinit var mReminderFragment: ReminderFragment
    private lateinit var mEditProfileFragment: EditProfileFragment

    private lateinit var mDatabaseOpenHelper: DataBaseOpenHelper

    private lateinit var mMovieListType: String

    // Navigation view
    private lateinit var mHeaderLayout: View
    private lateinit var mAvatarImg: ImageView
    private lateinit var mNameText: TextView
    private lateinit var mEmailText: TextView
    private lateinit var mEditbtn: ImageView
    private lateinit var mShowAllReminder: ImageView
    private lateinit var mGenderText: TextView
    private lateinit var mDateOfBirthText: TextView
    private lateinit var mSharedPreferences: SharedPreferences

    private var mIsGridview : Boolean  = false
    private var mFavoriteCount: Int = 0

    private var imgBitmap: Bitmap? = null
    private val imgConverter : BitmapConverter = BitmapConverter()

    private lateinit var mReminderAdapter: ReminderAdapter
    private lateinit var mReminderRecyclerView: RecyclerView
    private lateinit var mReminderLayout: LinearLayout

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mIconList = mutableListOf(
            R.drawable.ic_gray_home,
            R.drawable.ic_gray_favorite,
            R.drawable.ic_gray_settings,
            R.drawable.ic_gray_info
        )
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val sharedPreferencesDefault = PreferenceManager.getDefaultSharedPreferences(this)
        mMovieListType = sharedPreferencesDefault.getString(Constant.PREF_CATEGORY_KEY,"upcoming").toString()
        val convertType:String = when(mMovieListType){
            "Top rated movies" -> "top_rate"
            "Up coming movies" -> "upcoming"
            else -> "now_playing"
        }
        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
//        mMovieCategory = PreferenceManager.getDefaultSharedPreferences(this)
//            .getString(Constant.PREF_CATEGORY_KEY,"popular").toString()
        mDatabaseOpenHelper = DataBaseOpenHelper(this,"movie_database",null,1)
        mMovieReminderList = mDatabaseOpenHelper.getListReminder()
        mMovieFavoriteList = mDatabaseOpenHelper.getListMovie()
        mFavoriteCount = mMovieFavoriteList.size
        //INIT
        //NavigationView
        mNavigationView = findViewById(R.id.navigation_view)
        mHeaderLayout = mNavigationView.getHeaderView(0)
        mNavigationView.bringToFront()

        //fragmentView
        mHomeFragment = HomeFragment(1, mDatabaseOpenHelper,convertType)
        mFavoriteFragment = FavoriteFragment(mDatabaseOpenHelper, mMovieFavoriteList)
        mSettingsFragment = SettingsFragment()
        mInforFragment = InforFragment()

        mReminderFragment = ReminderFragment(mDatabaseOpenHelper)
        mEditProfileFragment = EditProfileFragment()

        //callback fragment
        mHomeFragment.setBadgeListener(this)
        mHomeFragment.setHomeListener(this)
        mHomeFragment.setDetailListener(this)
        mHomeFragment.setReminderListener(this)


        mFavoriteFragment.setBadgeListener(this)
        mFavoriteFragment.setFavoriteListener(this)

        mReminderFragment.setRemindListener(this)
        mReminderFragment.setToolbarTitleListener(this)

        mEditProfileFragment.setProfileListener(this)

        mSettingsFragment.setSettingListener(this)
        //setup
        setUpTabs()
        setDrawerLayout()

        //Navigation view items
        mEditbtn = mHeaderLayout.findViewById(R.id.edit_profile_btn)
        mShowAllReminder = mHeaderLayout.findViewById(R.id.btn_show_reminder_all)
        mAvatarImg = mHeaderLayout.findViewById(R.id.img_avatar)
        mNameText = mHeaderLayout.findViewById(R.id.tv_name)
        mDateOfBirthText = mHeaderLayout.findViewById(R.id.txt_dob_header_profile)
        mEmailText = mHeaderLayout.findViewById(R.id.txt_mail_header_profile)
        mGenderText = mHeaderLayout.findViewById(R.id.txt_gender_header_profile)

        loadDataProfile()
        mEditbtn.setOnClickListener{
            if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                this.mDrawerLayout.closeDrawer(GravityCompat.START)
            }
            val bundle = Bundle()
            bundle.putString("name",mNameText.text.toString())
            bundle.putString("email",mEmailText.text.toString())
            bundle.putString("dob",mDateOfBirthText.text.toString())
            bundle.putString("gender",mGenderText.text.toString())
            try {
                bundle.putString(
                    "imgBitMapString",
                    mSharedPreferences.getString("profileImg","No data")
                )
            }catch (e:Exception){

            }
            mEditProfileFragment.arguments = bundle
            Toast.makeText(this,"EditProfile",Toast.LENGTH_SHORT).show()
            if(!mEditProfileFragment.isAdded){
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.frg_home,mEditProfileFragment,"frg_reminder")
                    addToBackStack(null)
                    commit()
                }
            }
        }

        mShowAllReminder.setOnClickListener{
//            supportActionBar!!.title = "Reminder"
            if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                this.mDrawerLayout.closeDrawer(GravityCompat.START)
            }
            mReminderFragment = ReminderFragment(mDatabaseOpenHelper)
            mReminderFragment.setToolbarTitleListener(this)
            mReminderFragment.setRemindListener(this)
            supportFragmentManager.beginTransaction().apply {
                add(
                    R.id.relative_layout,
                    mReminderFragment,
                    Constant.FRAGMENT_REMINDER_TAG
                )
                addToBackStack(null)
                commit()
            }
        }

        // Reminder

        onLoadingReminder()
        createNotificationChannel()


    }

    private fun onLoadingReminder(){
        mMovieReminderList = mDatabaseOpenHelper.getListReminder()
        mReminderLayout = mHeaderLayout.findViewById(R.id.reminder_layout)
        mReminderRecyclerView = mHeaderLayout.findViewById(R.id.rcv_view_reminder)
        if(mMovieReminderList.isEmpty()){
            mReminderLayout.visibility = View.GONE
        }else {
            mReminderLayout.visibility = View.VISIBLE
            val linearLayoutManager = LinearLayoutManager(this)
            mReminderAdapter = ReminderAdapter(
                mMovieReminderList,
                ReminderAdapter.REMINDER_PROFILE,
                mDatabaseOpenHelper
            )
            mReminderAdapter.setReminderListener(this)
            mReminderRecyclerView.layoutManager = linearLayoutManager
            mReminderRecyclerView.setHasFixedSize(true)
            mReminderRecyclerView.adapter = mReminderAdapter
            mReminderAdapter.updateData(mMovieReminderList)
//            setHas
        }
    }

    override fun onBackPressed() {
        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            this.mDrawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onUpdateFromMovie(movie: Movie, isFavorites: Boolean) {
//        mHomeFragment.updateForFavorite(movie)
//        movie.isFavorite = isFavorites
        mFavoriteFragment.updateFavoriteList(movie,isFavorites)
    }

    override fun onUpdateFromFavorite(movie: Movie){
        mHomeFragment.updateMovieList(movie,false)
        val detailFragment = supportFragmentManager.findFragmentByTag(Constant.FRAGMENT_DETAIL_TAG)
        if(detailFragment != null){
            detailFragment as DetailFragment
            detailFragment.updateMovie(movie.id)
        }
    }

    override fun onUpdateFromDetail(movie: Movie, isFavorite: Boolean) {
        mHomeFragment.updateMovieList(movie,isFavorite)
        mFavoriteFragment.updateFavoriteList(movie,isFavorite)
    }

    override fun onAddReminder() {
        mMovieReminderList = mDatabaseOpenHelper.getListReminder()
        mReminderAdapter.updateData(mMovieReminderList)
    }

    override fun onUpdateTitleFromDetail(movieTitle: String) {
        supportActionBar!!.title = movieTitle
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.change_layout -> {
                mIsGridview = !mIsGridview
                if(mIsGridview){
                    item.setIcon(R.drawable.ic_gray_list)
                }else{
                    item.setIcon(R.drawable.grid_gray)
                }
                mHomeFragment.changeViewHome() }
            R.id.action_homes -> {
                mViewPager.currentItem = 0

            }
            R.id.action_favorites -> { mViewPager.currentItem = 1 }
            R.id.action_settings -> { mViewPager.currentItem = 2 }
            R.id.action_informations -> { mViewPager.currentItem = 3 }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUpdateTitleMovie(movieTitle: String,isTitle: Boolean) {
            supportActionBar!!.title = movieTitle
    }

    private fun setUpTabs(){
        mViewPager = findViewById(R.id.viewPager)
        mTabLayout = findViewById(R.id.tabs)

        mViewPageAdapter = ViewPageAdapter(supportFragmentManager)
        mViewPageAdapter.addFragment(mHomeFragment,"Home")
        mViewPageAdapter.addFragment(mFavoriteFragment,"Favorite")
        mViewPageAdapter.addFragment(mSettingsFragment,"Setting")
        mViewPageAdapter.addFragment(mInforFragment,"About")
        mViewPager.offscreenPageLimit = 4

        mViewPager.adapter = mViewPageAdapter
        mTabLayout.setupWithViewPager(mViewPager)
        mTabLayout.background = getDrawable(R.drawable.nenn)

        val countFragment = mViewPageAdapter.count
        for(i in 0 until countFragment){
//            mTabLayout.getTabAt(i)!!.setIcon(mIconList[i])
            mTabLayout.getTabAt(i)!!.setCustomView(R.layout.notification_badge)
            val tabView = mTabLayout.getTabAt(i)!!.customView
            val titleTab = tabView!!.findViewById<TextView>(R.id.tab_title)
            titleTab.text = listTitle[i]
            val iconTab = tabView.findViewById<ImageView>(R.id.tab_icon)
            iconTab.setImageResource(mIconList[i])
            if(i == 1){
                val badgeText = tabView.findViewById<TextView>(R.id.tab_badge)
                badgeText.visibility = View.VISIBLE
                badgeText.setBackground(ContextCompat.getDrawable(baseContext, R.drawable.round))
                badgeText.setText("$mFavoriteCount",TextView.BufferType.EDITABLE
                )
            }
        }
        setTitleFragment()
    }

    private fun setDrawerLayout(){
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Home"
        mDrawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            mDrawerLayout,
            mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        mDrawerLayout.addDrawerListener(toggle)

        toggle.syncState()
        toggle.toolbarNavigationClickListener = View.OnClickListener {

            mDrawerLayout.openDrawer(GravityCompat.START)
        }
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.shubarb_logo)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setTitleFragment(){
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                mTabLayout.nextFocusRightId = position
                supportActionBar!!.title = (listTitle[position])
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    private var listTitle = mutableListOf("Home","Favorite","Setting","About")

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(channelID,"Movie!!!",NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Time to watch a movie"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun loadDataProfile(){
        mNameText.text = mSharedPreferences.getString("profileName","No data")
        mEmailText.text = mSharedPreferences.getString("profileEmail","No data")
        mDateOfBirthText.text = mSharedPreferences.getString("profileDob","No data")
        mGenderText.text = mSharedPreferences.getString("profileGender","No data")
        try {
            mAvatarImg.setImageBitmap(
                imgConverter.decodeBase64(
                    mSharedPreferences.getString(
                        "profileImg",
                        "No data"
                    )
                )
            )
        }catch (e: Exception){
            mAvatarImg.setImageResource(R.drawable.ic_baseline_person_24)
        }
    }

    override fun onUpdateBadgeNumber(isFavorite: Boolean) {
        val tabView = mTabLayout.getTabAt(1)!!.customView
        val badgeText = tabView!!.findViewById<TextView>(R.id.tab_badge)
        if(isFavorite)
            badgeText.setText("${++mFavoriteCount}",TextView.BufferType.EDITABLE)
        else
            badgeText.setText("${--mFavoriteCount}",TextView.BufferType.EDITABLE)
    }

     override fun onCLickItemReminder(movie: Movie) {
        mViewPager.currentItem = 0
        supportActionBar!!.title = movie.title

        if(this.mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            this.mDrawerLayout.closeDrawer(GravityCompat.START)
        }

        val reminderCurrentFragment =
            supportFragmentManager.findFragmentByTag(Constant.FRAGMENT_REMINDER_TAG)
        if(reminderCurrentFragment != null){
            supportFragmentManager.beginTransaction().apply {
                remove(reminderCurrentFragment)
                commit()
            }
        }
        val detailCurrentFragment =
            supportFragmentManager.findFragmentByTag(Constant.FRAGMENT_DETAIL_TAG)
        if(detailCurrentFragment != null){
            supportFragmentManager.beginTransaction().apply {
                remove(detailCurrentFragment)
                commit()
            }
        }

        val bundle = Bundle()
        bundle.putSerializable("movieDetail", movie)
        val detailFragment = DetailFragment(mDatabaseOpenHelper)
        detailFragment.setToolbarTitleListener(this)
        detailFragment.setBadgeListener(this)
        detailFragment.setDetailListener(this)
        detailFragment.setRemindListener(this)
        detailFragment.arguments = bundle
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frg_home,detailFragment,Constant.FRAGMENT_DETAIL_TAG)
            addToBackStack(null)
            commit()
        }
    }

    override fun onLoadReminder() {
        onLoadingReminder()
    }

    override fun onLongCLickItemReminder(position: Int): Boolean {
        return true
    }

    override fun onSaveProfile(name: String, email: String, dob: String, gender: String, imgBitmap: Bitmap?) {
        val edit = mSharedPreferences.edit()
        edit.putString("profileName",name)
        edit.putString("profileEmail",email)
        edit.putString("profileDob",dob)
        edit.putString("profileGender",gender)
        if(imgBitmap != null)
            edit.putString("profileImg",imgConverter.encodeBase64(imgBitmap))
        this.imgBitmap = imgBitmap
        mAvatarImg.setImageBitmap(imgBitmap)
        edit.apply()
        loadDataProfile()
        Toast.makeText(this,"Save profile successfully",Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateToolbarTitle(toolbarTitle: String) {
        supportActionBar!!.title = toolbarTitle
    }

    override fun onUpdateFromSetting() {
        mHomeFragment.setListMovieByCondition()
    }


}