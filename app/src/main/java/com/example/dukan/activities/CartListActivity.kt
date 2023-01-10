package com.example.dukan.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dukan.R
import com.example.dukan.adapters.CartItemsListAdapter
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.CartItem
import com.example.dukan.models.Product
import kotlinx.android.synthetic.main.activity_cart_list.*

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList : ArrayList<Product>
    private lateinit var mCartListItems :ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)

        setUpActionBar()
    }


    private fun setUpActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)
        val toolbar = supportActionBar

        toolbar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        toolbar?.setDisplayHomeAsUpEnabled(true)

        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }

    }

    fun successCartItemsList(cartList : ArrayList<CartItem>){
        hideProgressDialog()



        for(product in mProductsList) {
            for (cartItem in cartList){
                if (product.product_id == cartItem.product_id) {

                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        if(mCartListItems.size > 0){
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            val itemAdapter = CartItemsListAdapter(this@CartListActivity , cartList)
            rv_cart_items_list.layoutManager = LinearLayoutManager(this)
            rv_cart_items_list.setHasFixedSize(true)
            rv_cart_items_list.adapter =itemAdapter

            var subTotal : Double  = 0.0
            for (item in mCartListItems){

                val availableQuantity = item.stock_quantity.toInt()
                if(availableQuantity > 0){
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }

            tv_sub_total.text = "\u20B9${subTotal}"
            tv_shipping_charge.text = "\u20B9100.0" // TODO Change shipping charge logic

            if(subTotal >0 ) {
                ll_checkout.visibility = View.VISIBLE

                val total = subTotal + 100 // TODO Change logic here
                tv_total_amount.text = "\u20B9${total}"
            } else {
                ll_checkout.visibility = View.GONE
            }
        }
        else {
           rv_cart_items_list.visibility = View.GONE
           ll_checkout.visibility = View.GONE
           tv_no_cart_item_found.visibility = View.VISIBLE
        }

    }

    private fun getCartItemsList() {
     //   showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getCartLists(this@CartListActivity)
    }

    override fun onResume() {
        super.onResume()

        getProductLists()
    }


    fun successProductListsFromFireStore(productsLists : ArrayList<Product>){
        hideProgressDialog()
        mProductsList = productsLists
        getCartItemsList()
    }

    private fun getProductLists(){
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getAllProductList(this@CartListActivity)

    }
    
    
    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(this@CartListActivity , resources.getString(R.string.msg_item_removed_successfully), Toast.LENGTH_LONG).show()
        getCartItemsList()
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }


}