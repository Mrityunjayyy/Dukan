package com.example.dukan.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dukan.R
import com.example.dukan.activities.AddEditAddressActivity
import com.example.dukan.models.Address
import com.example.dukan.utils.Constants
import kotlinx.android.synthetic.main.item_address_layout.view.*

class AddressListAdapter(private val context  : Context , private val items : ArrayList<Address> , private val selectAddress : Boolean)
    : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) :RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
     return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address_layout , parent ,false) )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]

        if (holder is ViewHolder) {
            holder.itemView.tv_address_full_name.text = model.name
            holder.itemView.tv_address_type.text = model.type
            holder.itemView.tv_address_details.text = "${model.address} , ${model.zipCode}"
            holder.itemView.tv_address_mobile_number.text = model.mobileNumber


            if(selectAddress) {
                holder.itemView.setOnClickListener{
                    Toast.makeText(context , "Selected address : ${model.address} , ${model.zipCode}" , Toast.LENGTH_LONG).show()
                }
            }
        }



    }

    override fun getItemCount(): Int {
       return items.size
    }

    fun notifyEditItem(activity : Activity, position : Int) {
        val intent = Intent(context , AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS , items[position])
        activity.startActivityForResult(intent , Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }
}