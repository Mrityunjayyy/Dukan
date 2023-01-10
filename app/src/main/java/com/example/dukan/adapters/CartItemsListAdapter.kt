package com.example.dukan.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dukan.R
import com.example.dukan.activities.CartListActivity
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.CartItem
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.item_cart_layout.view.*

open class CartItemsListAdapter(private val context : Context , private var items : ArrayList<CartItem>) :RecyclerView.Adapter<CartItemsListAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart_layout , parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]


            if (holder is ViewHolder) {
                GlideLoader(context).loadCartItemListPicture(
                    model.image,
                    holder.itemView.iv_cart_item_image
                )
                holder.itemView.tv_cart_item_title.text = model.title
                holder.itemView.tv_cart_quantity.text = model.cart_quantity
                holder.itemView.tv_cart_item_price.text = "\u20B9" + model.price

                if (model.cart_quantity == "0") {
                    holder.itemView.ib_remove_cart_item.visibility = View.GONE
                    holder.itemView.ib_add_cart_item.visibility = View.GONE

                    holder.itemView.tv_cart_quantity.text =
                        context.resources.getString(R.string.lbl_out_of_stock)

                    holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSnackBarError
                        )
                    )
                } else {
                    holder.itemView.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.itemView.ib_add_cart_item.visibility = View.VISIBLE

                    holder.itemView.tv_cart_quantity.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorSecondaryText
                        )
                    )
                }

            holder.itemView.ib_delete_cart_item.setOnClickListener{
                when(context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                fireStoreClass().removeItemFromCart(context , model.id)
            }

                holder.itemView.ib_remove_cart_item.setOnClickListener{
                    if(model.cart_quantity == "1"){
                        fireStoreClass().removeItemFromCart(context , model.id)
                    } else {
                        val cartQuantity : Int = model.cart_quantity.toInt()

                        val itemHashMap =HashMap<String , Any>()

                        itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                        if(context is CartListActivity){
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }

                        fireStoreClass().updateMyCart(context , model.id , itemHashMap)
                    }
                }



                
                holder.itemView.ib_add_cart_item.setOnClickListener{
                    val cartQuantity : Int = model.cart_quantity.toInt()
                    if (cartQuantity < model.stock_quantity.toInt()){
                        val itemHashMap = HashMap<String , Any>()

                        itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                        if(context is CartListActivity){
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))
                        }
                        fireStoreClass().updateMyCart(context , model.id , itemHashMap)
                    } else {
                        if(context is CartListActivity) {
                            context.showErrorSnackBar(
                                context.resources.getString(R.string.msg_for_available_stock , model.stock_quantity) ,
                                errorMessage = true
                            )
                        }
                    }
                }

            }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}