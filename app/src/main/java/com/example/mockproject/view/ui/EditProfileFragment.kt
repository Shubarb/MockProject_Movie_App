package com.example.mockproject.view.ui

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.mockproject.R
import com.example.mockproject.util.BitmapConverter
import com.example.mockproject.view.interfaceview.ProfileListener
import java.lang.Exception
import java.util.*

class EditProfileFragment : Fragment() {
    private lateinit var mProfileImg: ImageView
    private lateinit var mRadioGroup: RadioGroup
    private lateinit var mRadioMale: RadioButton
    private lateinit var mRadioFemale: RadioButton
    private lateinit var mNameEdt: EditText
    private lateinit var mEmailEdt: EditText
    private lateinit var mDobtxt: TextView
    private lateinit var mSaveBtn: ImageView

    private var mBitmapProfile: Bitmap? = null
    private val mConverterImg: BitmapConverter = BitmapConverter()
    private var mGender: String = ""

    private var mSaveDay = 0
    private var mSaveMonth = 0
    private var mSaveYear = 0

    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var mProfileListener: ProfileListener

    fun setProfileListener(profileListener: ProfileListener){
        this.mProfileListener = profileListener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        mRadioGroup = view.findViewById(R.id.radio_gender_gr)
        mRadioMale = view.findViewById(R.id.radio_male)
        mRadioFemale = view.findViewById(R.id.radio_female)
        onClickChanged()

        mProfileImg = view.findViewById(R.id.img_avatar_edit_profile)
        mNameEdt = view.findViewById(R.id.edt_name_profile)
        mEmailEdt = view.findViewById(R.id.edt_email_profile)
        mDobtxt = view.findViewById(R.id.txt_dob_profile)
        mSaveBtn = view.findViewById(R.id.save_profile)

        val bundle = arguments
        if(bundle != null){
            var nameBundle = bundle.getString("name")
            var emailBundle = bundle.getString("email")
            var dobBundle = bundle.getString("dob")
            if(nameBundle == "No data") nameBundle = ""
            if(emailBundle == "No data") emailBundle = ""
            if(dobBundle == "No data") dobBundle = ""

            mNameEdt.setText(nameBundle,TextView.BufferType.EDITABLE)
            mEmailEdt.setText(emailBundle,TextView.BufferType.EDITABLE)
            mDobtxt.setText(dobBundle,TextView.BufferType.EDITABLE)

            try {
                mProfileImg.setImageBitmap(mConverterImg.decodeBase64(bundle.getString("imgBitMapString")))

            }catch (e:Exception){
                mProfileImg.setImageResource(R.drawable.ic_baseline_person_24)
            }
            if(bundle.getString("gender").toString().contains("Female")){
                mRadioGroup.check(R.id.radio_female)
            }else{
                mRadioGroup.check(R.id.radio_male)

            }

        }

        mDobtxt.setOnClickListener{
            setDate()
        }

        mSaveBtn.setOnClickListener {
            val name = mNameEdt.text.toString()
            val email = mEmailEdt.text.toString()
            val dob = mDobtxt.text.toString()
            if(name == "" || email == "" || dob == "" || mGender == ""){
                Toast.makeText(context,"Fill all information!",Toast.LENGTH_SHORT).show()
            }else{
                mProfileListener.onSaveProfile(name,email,dob,mGender,mBitmapProfile)
            }

        }

        mProfileImg.setOnClickListener {
            dispatchTakePictureIntent()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            val imageBitmap = data!!.extras!!.get("data") as Bitmap
            this.mBitmapProfile = imageBitmap
            mProfileImg.setImageBitmap(imageBitmap)
        }

    }

    private fun onClickChanged(){
        mRadioGroup.setOnCheckedChangeListener { _, i ->
            if(i == R.id.radio_male){
                mGender = "Male"
            }else if(i == R.id.radio_female){
                mGender = "Female"
            }
        }
    }

    private fun setDate(){
        val currentDataTime = Calendar.getInstance()
        val startYear = currentDataTime.get(Calendar.YEAR)
        val startMonth = currentDataTime.get(Calendar.MONTH)
        val startDay = currentDataTime.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(),{ _,year,month,day ->
            val pickedDateTime = Calendar.getInstance()
            pickedDateTime.set(year,month,day)
            mSaveYear = year
            mSaveMonth = month
            mSaveDay = day
            currentDataTime.set(mSaveYear,mSaveMonth,mSaveDay)
            val timehour = "$mSaveYear/${mSaveMonth+1}/$mSaveDay"
            mDobtxt.setText(timehour,TextView.BufferType.EDITABLE)
        },startYear,startMonth,startDay).show()
    }

    private fun dispatchTakePictureIntent(){
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE)
        }catch (e:ActivityNotFoundException){

        }
    }

}