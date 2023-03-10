package com.example.dukan.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.User
import com.example.dukan.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : BaseActivity()  , View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//
//        @Suppress("DEPRECATION")
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN ,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }

    override fun onClick(view : View?) {
        if (view != null) {

            when(view.id) {
                R.id.tv_forgot_password -> {

                    val intent  = Intent(this@LoginActivity , ForgotPasswordActivity::class.java)
                    startActivity(intent)

                }

                R.id.btn_login -> {
                    logInRegisteredUser()
                }

                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }


    private fun validateLoginDetails() : Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email) , true)
                false
            }

            TextUtils.isEmpty(et_password.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password) , true)
                false
            }

            else -> {
               // showErrorSnackBar("Your Details are valid" , false)
                true
            }
        }
    }


    private fun logInRegisteredUser() {
        if(validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val email : String = et_email.text.toString().trim { it <= ' '}
            val password : String = et_password.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email , password).addOnCompleteListener(
              OnCompleteListener<AuthResult>  {
                    task ->

                    if(task.isSuccessful) {

                        fireStoreClass().getUserDetails(this@LoginActivity)


                    } else  {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString() , true ) }
                })

        }
     }


    fun userLoggedInSuccess(user : User) {
        hideProgressDialog()
        if(user.profileCompleted == 0 ) {
            val intent = Intent(this@LoginActivity , UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS  , user)
            startActivity(intent)
        } else {
            val intent = Intent(this@LoginActivity , DashboardActivity::class.java)
            startActivity(intent)
        }
        finish()

    }

}