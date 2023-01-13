package com.example.dukan.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dukan.R
import com.example.dukan.activities.ui.notifications.NotificationsViewModel
import com.example.dukan.adapters.MyOrdersListAdapter
import com.example.dukan.databinding.FragmentOrdersBinding
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Order
import kotlinx.android.synthetic.main.fragment_orders.*


class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun populationOrdersListInUI(ordersList : ArrayList<Order>) {
       // hideProgressDialog()

        if(ordersList.size > 0) {

            tv_no_orders_found.visibility = View.GONE
            rv_my_order_items.visibility = View.VISIBLE


            rv_my_order_items.layoutManager = LinearLayoutManager(requireActivity())
            rv_my_order_items.setHasFixedSize(true)

            val ordersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rv_my_order_items.adapter = ordersAdapter

        } else {
            tv_no_orders_found.visibility = View.VISIBLE
            rv_my_order_items.visibility = View.GONE
        }

    }

    private fun getMYOrdersList() { 
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMYOrdersList()
    }
}