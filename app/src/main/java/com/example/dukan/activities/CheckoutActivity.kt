package com.example.dukan.activities

import android.content.Intent
import android.mtp.MtpConstants
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dukan.R
import com.example.dukan.adapters.CartItemsListAdapter
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Address
import com.example.dukan.models.CartItem
import com.example.dukan.models.Order
import com.example.dukan.models.Product
import com.example.dukan.utils.Constants
import com.google.api.Distribution.BucketOptions.Linear
import kotlinx.android.synthetic.main.activity_checkout.*

class CheckoutActivity : BaseActivity() {

    private var mAddressDetails : Address?= null
    private lateinit var mProductsList : ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var  mSubTotal : Double = 0.0
    private var mTotalAmount : Double = 0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setUpActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }

        if(mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text ="${mAddressDetails!!.address} , ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if(mAddressDetails?.otherDetails!!.isNotEmpty()){
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }

            tv_checkout_mobile_number?.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener{
            placeAnOrder()
        }
    }


    private fun setUpActionBar() {
       setSupportActionBar(toolbar_checkout_activity)
        val toolbar = supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getAllProductList(this@CheckoutActivity)
    }

    fun successProductListFromFireStore(productsList : ArrayList<Product>){
            mProductsList = productsList
            getCartItemsList()
    }

    private fun getCartItemsList() {
        fireStoreClass().getCartLists(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList  : ArrayList<CartItem>) {
        hideProgressDialog()

        for(product in mProductsList) {
            for(cartItem in cartList) {
                if (product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity , mCartItemsList , false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList)
        {
            val availableQuantity = item.stock_quantity.toInt()
            if(availableQuantity > 0) {
            val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "\u20B9$mSubTotal"
        tv_checkout_shipping_charge.text = "\u20B9100"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 100.0
            tv_checkout_total_amount.text = "\u20B9$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }


    private fun placeAnOrder() {
       showProgressDialog(resources.getString(R.string.please_wait))
        if (mAddressDetails != null) {
            val order = Order(
                fireStoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My Order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "100.0",
                mTotalAmount.toString(),
            )

            fireStoreClass().placeOrder(this@CheckoutActivity , order = order)
        }
    }

        fun orderPlacedSuccess() {
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity , "Your order was placed successfully." , Toast.LENGTH_LONG).show()

        val intent = Intent(this@CheckoutActivity , DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}