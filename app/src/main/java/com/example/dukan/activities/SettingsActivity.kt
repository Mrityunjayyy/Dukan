package com.example.dukan.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.User
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity() , View.OnClickListener {

    private lateinit var mUserDetails : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        btn_logout.setOnClickListener(this@SettingsActivity)
        tv_edit.setOnClickListener(this@SettingsActivity)
        ll_address.setOnClickListener(this@SettingsActivity)

        setUpToolBar()
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar_settings_activity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {

        mUserDetails = user

        hideProgressDialog()
        GlideLoader(this@SettingsActivity).loadUserPicture(user.image, iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"

    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when(view.id)  {

                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity , UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS , mUserDetails)
                    startActivity(intent)
                }

                R.id.btn_logout -> {
                   FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity , LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                }

                R.id.ll_address -> {
                    val intent = Intent (this@SettingsActivity , AddressListActivity::class.java)
                    startActivity(intent)
//                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                    finish()
                }



            }
        }
    }
}