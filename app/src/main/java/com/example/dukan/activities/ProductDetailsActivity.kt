package com.example.dukan.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.dukan.R
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.CartItem
import com.example.dukan.models.Product
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_product_details.*

class ProductDetailsActivity : BaseActivity() , View.OnClickListener {

    private var mProductID : String = ""
    private lateinit var mProductDetails : Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        setUpActionBar()

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_ID)){
            mProductID = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!

        }

        var productOwnerId :String = ""

        if(intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if(fireStoreClass().getCurrentUserID() == productOwnerId){
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        }else {
            btn_add_to_cart.visibility = View.VISIBLE
           // btn_go_to_cart.visibility = View.VISIBLE
        }

        getProductDetails()
        btn_add_to_cart.setOnClickListener(this@ProductDetailsActivity)
        btn_go_to_cart.setOnClickListener(this@ProductDetailsActivity)
    }


   private fun setUpActionBar() {
        setSupportActionBar(toolbar_product_details_activity)
        val toolbar = supportActionBar!!
        toolbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        toolbar.setDisplayHomeAsUpEnabled(true)

        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun productDetailsSuccess(product : Product) {
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image , iv_product_detail_image
        )

        tv_product_details_title.text = product.title
        tv_product_details_price.text =   product.price
        tv_product_details_description.text = product.description
        tv_product_details_stock_quantity.text = product.stock_quantity

        if(product.stock_quantity.toInt() == 0){
            hideProgressDialog()

            btn_add_to_cart.visibility = View.GONE
            tv_product_details_stock_quantity.text = resources.getString(R.string.lbl_out_of_stock)

            tv_product_details_stock_quantity.setTextColor(ContextCompat.getColor(this@ProductDetailsActivity , R.color.colorSnackBarError))
        } else {
            if (fireStoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                fireStoreClass().checkIfItemExistsInCart(this@ProductDetailsActivity, mProductID)
            }
        }
    }

    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getProductDetails(this@ProductDetailsActivity , productId = mProductID )
    }

    override fun onClick(view: View?) {
        if(view != null) {
            when(view.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity , CartListActivity::class.java))

                }

            }
        }
    }


    private fun addToCart() {
       val addToCart  = CartItem(
           fireStoreClass().getCurrentUserID() ,
           mProductID ,
           mProductDetails.title ,
           mProductDetails.price ,
           mProductDetails.image ,
           Constants.DEFAULT_CART_QUANTITY,
           mProductDetails.stock_quantity
       )

        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().addCartItems(this , addToCart)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(this@ProductDetailsActivity , resources.getString(R.string.success_message_item_added_to_cart) , Toast.LENGTH_LONG).show()

        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE

    }

    fun productExistsInCart() {
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

}