package com.example.dukan.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.User
import com.example.dukan.utils.Constants
import com.example.dukan.utils.Constants.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
import com.example.dukan.utils.Constants.PICK_IMAGE_REQUEST_CODE
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri : Uri ?= null
    private var mUserProfileImageURl : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)


        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
         mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }




        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_save.setOnClickListener(this@UserProfileActivity)

        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        if(mUserDetails.profileCompleted == 0) {
            tv_title.text = resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled = false

            et_last_name.isEnabled = false


        } else {
            setUpActionBar()
            tv_title.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image , iv_user_photo)



            if (mUserDetails.mobile != 0L) {
                et_mobile_number.setText(mUserDetails.mobile.toString())
            }
            if (mUserDetails.gender == Constants.MALE){
                rb_male.isChecked = true
            } else  {
                rb_female.isChecked = true
            }


        }



    }


    override fun onClick(view : View?) {
      when(view?.id) {
          R.id.iv_user_photo -> {
              if(ActivityCompat.checkSelfPermission(this@UserProfileActivity , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
              //  showErrorSnackBar("You already have the permission", false)
                  Constants.showImageChooser(this@UserProfileActivity)
               } else  {
                   ActivityCompat.requestPermissions(this@UserProfileActivity , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) ,
                       EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
                    }
               }

          R.id.btn_save -> {

             if(validateUserProfileDetails()){
                 showProgressDialog(resources.getString(R.string.please_wait))

                 if(mSelectedImageFileUri != null) {
                     fireStoreClass().uploadImageToCloudStorage(this@UserProfileActivity , mSelectedImageFileUri , Constants.USER_PROFILE_IMAGE)
                 } else {
                     updateUserProfileDetails()
                 }


              }



          }
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode ==  EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)  {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this@UserProfileActivity)
          //      showErrorSnackBar("The storage permission is granted" , false )
            } else {
                Toast.makeText(this@UserProfileActivity , resources.getString(R.string.read_storage_permission_denied) , Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == PICK_IMAGE_REQUEST_CODE)  {
                if (data != null)  {
                    try {
                       mSelectedImageFileUri = data.data!!

                        GlideLoader(this@UserProfileActivity).
                        loadUserPicture(mSelectedImageFileUri!! , iv_user_photo)

                       // iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    } catch (e : IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@UserProfileActivity , resources.getString(R.string.image_selection_failed) , Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        val userHashMap = HashMap<String , Any>()

        val firstName = et_first_name.text.toString().trim { it <= ' '}
        if (firstName != mUserDetails.firstName)  {
            userHashMap[Constants.FIRST_NAME] = firstName
        }

        val lastName = et_last_name.text.toString().trim { it <= ' '}
        if (lastName != mUserDetails.lastName)  {
            userHashMap[Constants.LAST_NAME] = lastName
        }

        val gender = if(rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURl.isNotEmpty()){
           userHashMap[Constants.IMAGE] = mUserProfileImageURl
        }

        val mobileNumber = et_mobile_number.text.toString().trim{ it <= ' '}

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString() ) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mUserDetails.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE] = 1

       // showProgressDialog(resources.getString(R.string.please_wait))

        fireStoreClass().updateUserProfileData(this@UserProfileActivity , userHashMap)

    }


    private fun validateUserProfileDetails() : Boolean {
        return when {

            TextUtils.isEmpty(et_mobile_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number) , true)
                false
            }
            else -> {
                true
            }
        }
    }



        fun userProfileUpdateSuccess() {
          hideProgressDialog()
          Toast.makeText(this@UserProfileActivity , resources.getString(R.string.msg_profile_update_success) , Toast.LENGTH_LONG).show()
           val intent = Intent(this@UserProfileActivity , DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }

    private fun setUpActionBar(){
        setSupportActionBar(toolbar_user_profile_activity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)

            toolbar_user_profile_activity.setNavigationOnClickListener {  onBackPressed() }
        }

    }

    fun imageUploadSuccess(imageURL : String) {
       // hideProgressDialog()


        mUserProfileImageURl = imageURL
        updateUserProfileDetails()

    }




}