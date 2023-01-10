package com.example.dukan.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Product
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_add_product.*
import java.io.IOException

class AddProductActivity: BaseActivity() , View.OnClickListener {

    private var mSelectedImageFileUri : Uri?= null
    private var mProductImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        iv_add_update_product.setOnClickListener(this@AddProductActivity)
        btn_submit_add_product.setOnClickListener(this@AddProductActivity)


        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_add_product_activity)
        val actionBar = supportActionBar

        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        }

        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }


    override fun onClick(view : View?) {
        if (view != null) {
            when(view.id) {
                R.id.iv_add_update_product -> {
                    if(ActivityCompat.checkSelfPermission(this@AddProductActivity , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        ActivityCompat.requestPermissions(this@AddProductActivity , arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE) ,
                            Constants.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
                        )
                    }
                }

                R.id.btn_submit_add_product -> {
                            if(validateProductDetails()){
                                uploadProductImage()
                               // showErrorSnackBar("Your details are valid" , false )
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
        if(requestCode == Constants.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
               Constants.showImageChooser(this@AddProductActivity)
            } else {
                Toast.makeText(this@AddProductActivity , resources.getString(R.string.read_storage_permission_denied) , Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == Constants.PICK_IMAGE_REQUEST_CODE)  {
                if (data != null)  {
                    try {
                        iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this@AddProductActivity , R.drawable.ic_vector_edit))
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this@AddProductActivity).
                        loadUserPicture(mSelectedImageFileUri!! , iv_product_image )

                        // iv_user_photo.setImageURI(Uri.parse(selectedImageFileUri.toString()))
                    } catch (e : IOException) {
                        e.printStackTrace()
                        Toast.makeText(this@AddProductActivity , resources.getString(R.string.image_selection_failed) , Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun validateProductDetails(): Boolean {
        return when {

            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }

            TextUtils.isEmpty(et_product_title.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }

            TextUtils.isEmpty(et_product_price.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }

            TextUtils.isEmpty(et_product_description.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }

            TextUtils.isEmpty(et_product_quantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }


    fun imageUploadSuccess(imageURL : String) {
//        hideProgressDialog()
//        showErrorSnackBar("Product image is uploaded successfully. Image URL :$imageURL" , false)

        mProductImageURL = imageURL

        uploadProductDetails()

    }


    private fun uploadProductDetails() {
        val username = this.getSharedPreferences(
            Constants.MYSHOPPAL_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME , "")!!

        val product = Product(
            fireStoreClass().getCurrentUserID() ,
            username,
            et_product_title.text.toString().trim { it <= ' '} ,
            et_product_price.text.toString().trim { it <= ' '} ,
            et_product_description.text.toString().trim { it <= ' '} ,
            et_product_quantity.text.toString().trim { it <= ' '} ,
            mProductImageURL
        )

        fireStoreClass().uploadProductDetails(this@AddProductActivity , product)
    }

    fun productUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(this@AddProductActivity , resources.getString(R.string.product_uploaded_success_message) , Toast.LENGTH_LONG).show()
        finish()
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().uploadImageToCloudStorage(this@AddProductActivity, mSelectedImageFileUri , Constants.PRODUCT_IMAGE)

    }
}