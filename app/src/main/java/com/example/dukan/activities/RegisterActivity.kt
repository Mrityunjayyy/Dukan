package com.example.dukan.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_login.setOnClickListener {
         onBackPressed()
        }

        setupActionBar()

        btn_register.setOnClickListener {
           registerUser()
        }

    }

    private fun setupActionBar(){
  setSupportActionBar(toolbar_register_activity)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(et_emaill.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(et_passwordd.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            et_passwordd.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cb_terms_and_condition.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
               // showErrorSnackBar(resources.getString(R.string.Registered), false)
                true
            }
        }
    }


    private fun registerUser(){
        if(validateRegisterDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))


            val email : String = et_emaill.text.toString().trim{ it <= ' '}
            val password : String = et_confirm_password.text.toString().trim { it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
               OnCompleteListener<AuthResult> {
                    task ->

                   //   hideProgressDialog()


                    if(task.isSuccessful) {
                        val fireBaseUser :FirebaseUser = task.result!!.user!!

                        val user = User( fireBaseUser.uid ,
                            et_first_name.text.toString().trim { it <= ' '},
                            et_last_name.text.toString().trim{ it <= ' '} ,
                            et_emaill.text.toString().trim{ it  <= ' '} ,
                        )


                        fireStoreClass().registerUser(this@RegisterActivity , user )


                      //  showErrorSnackBar("You are Registered Successfully. Your User id is ${fireBaseUser.uid}" , false )
                      //  FirebaseAuth.getInstance().signOut()
                     //   finish()

//                        val intent  = Intent(this@RegisterActivity , LoginActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                        intent.putExtra("user_id" , fireBaseUser.uid)
//                        intent.putExtra("email" , email)
//                        startActivity(intent)
//                        finish()
                    } else {

                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString() , true)
                    }
                })

        }
    }

    fun userRegistrationSuccess() {

        hideProgressDialog()
        Toast.makeText(this@RegisterActivity , resources.getString(R.string.register_success) , Toast.LENGTH_LONG).show()
        finish()

    }



}
