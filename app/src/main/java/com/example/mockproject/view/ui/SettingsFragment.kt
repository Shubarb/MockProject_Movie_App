package com.example.mockproject.view.ui


import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import androidx.annotation.DrawableRes
import androidx.preference.*
import com.example.mockproject.Constant.Constant
import com.example.mockproject.R
import com.example.mockproject.view.interfaceview.SettingListener

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mCategoryListPref: ListPreference
    private lateinit var mRateSeekBar: SeekBarPreference
    private lateinit var mReleaseYearEditTextPref: EditTextPreference
    private lateinit var mSortListPref: ListPreference

    private lateinit var mSettingListener: SettingListener

    fun setSettingListener(settingListener: SettingListener) {
        this.mSettingListener = settingListener
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences!!
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences!!
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, p1: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, p1)
        setHasOptionsMenu(true)
        mCategoryListPref = findPreference(Constant.PREF_CATEGORY_KEY)!!
        mRateSeekBar = findPreference(Constant.PREF_RATE_KEY)!!
        mReleaseYearEditTextPref = findPreference(Constant.PREF_RELEASE_KEY)!!
        mSortListPref = findPreference(Constant.PREF_SORT_KEY)!!

        val p0 = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val category = p0.getString(Constant.PREF_CATEGORY_KEY, "popular")
        val rate = p0.getInt(Constant.PREF_RATE_KEY, 0)
        val releaseYear = p0.getString(Constant.PREF_RELEASE_KEY, "")
        val sort = p0.getString(Constant.PREF_SORT_KEY, "")


        mCategoryListPref.summary = when (category) {
            "popular" -> "Popular movies"
            "top_rated" -> "Top rated movies"
            "upcoming" -> "Up coming movies"
            "now_playing" -> "Now playing movies"
            else -> "Popular movies"

        }
        if (rate == 0) {
            mRateSeekBar.summary = ""
        } else {
            mRateSeekBar.summary = rate.toString()
        }

        if (releaseYear.isNullOrEmpty()) {
            mReleaseYearEditTextPref.summary = ""
        } else {
            mReleaseYearEditTextPref.summary = releaseYear
        }

        if (releaseYear.isNullOrEmpty()) {
            mSortListPref.summary = ""
        } else {
            mSortListPref.summary = sort
        }

//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
//        mCategoryListPref.summary =
//            sharedPreferences.getString(Constant.PREF_CATEGORY_KEY,"Popular movies")
//        mRateSeekBar.summary =
//            sharedPreferences.getInt(Constant.PREF_RATE_KEY,0).toString()
//        mReleaseYearEditTextPref.summary =
//            sharedPreferences.getString(Constant.PREF_RELEASE_KEY,"")
//        mSortListPref.summary =
//            sharedPreferences.getString(Constant.PREF_SORT_KEY,"")
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences, p1: String?) {
        val category = p0.getString(Constant.PREF_CATEGORY_KEY,"popular")
        val rate = p0.getInt(Constant.PREF_RATE_KEY,0)
        val releaseYear = p0.getString(Constant.PREF_RELEASE_KEY,"")
        val sort = p0.getString(Constant.PREF_SORT_KEY,"")

        when{
            p1.equals(Constant.PREF_CATEGORY_KEY) -> {
                mCategoryListPref.summary = when(category){
                    "popular" -> "Popular movies"
                    "top_rated" -> "Top rated movies"
                    "upcoming" -> "Up coming movies"
                    "now_playing" -> "Now playing movies"
                    else -> "Popular movies"
                }
            }
            p1.equals(Constant.PREF_RATE_KEY)->{
                if(rate == 0){
                    mRateSeekBar.summary = ""
                }else{
                    mRateSeekBar.summary = rate.toString()
                }
            }
            p1.equals(Constant.PREF_RELEASE_KEY)->{
                if(releaseYear.isNullOrEmpty()) {
                    mReleaseYearEditTextPref.summary = ""
                }else{
                    mReleaseYearEditTextPref.summary = releaseYear
                }
            }
            p1.equals(Constant.PREF_SORT_KEY)->{
                if(releaseYear.isNullOrEmpty()) {
                    mSortListPref.summary = ""
                }else{
                    mSortListPref.summary = sort
                }
            }
        }
        mSettingListener.onUpdateFromSetting()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.change_layout)
        item.isVisible = false
    }
}






