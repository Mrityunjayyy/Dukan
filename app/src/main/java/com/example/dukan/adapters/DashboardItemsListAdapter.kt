package com.example.dukan.adapters
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dukan.R
import com.example.dukan.activities.ProductDetailsActivity
import com.example.dukan.models.Product
import com.example.dukan.utils.Constants
import com.example.dukan.utils.GlideLoader
import kotlinx.android.synthetic.main.item_dashboard_layout.view.*

open class DashboardItemsListAdapter(private val context : Context , private var items : ArrayList<Product>) : RecyclerView.Adapter<DashboardItemsListAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout , parent , false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = items[position]

        if(holder is ViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.image , holder.itemView.iv_dashboard_item_image
            )

            holder.itemView.tv_dashboard_item_title.text = model.title
            holder.itemView.tv_dashboard_item_price.text = "\u20B9${model.price}"

        }


        holder.itemView.setOnClickListener{
            val intent = Intent(context , ProductDetailsActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_ID , model.product_id)
            intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID , model.user_id)
            context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int {
        return items.size
    }
}