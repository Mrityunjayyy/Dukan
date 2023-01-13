package com.example.dukan.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dukan.R
import com.example.dukan.activities.SoldProductDetailsActivity
import com.example.dukan.models.SoldProduct
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.item_list_layout.view.*

class MySoldProductsAdapter(private val context : Context, private val items : ArrayList<SoldProduct>) : RecyclerView.Adapter<MySoldProductsAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_layout , parent , false ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]

        if (holder is ViewHolder) {
            GlideLoader(context).loadProductPicture(model.image , holder.itemView.iv_item_image)

            holder.itemView.tv_item_name.text = model.title
            holder.itemView.tv_item_price.text = "\u20B9${model.price}"

            holder.itemView.ib_delete_product.visibility = View.GONE

            holder.itemView.setOnClickListener{
                val intent = Intent(context , SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}