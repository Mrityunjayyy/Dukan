package com.example.dukan.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager

import com.example.dukan.R
import com.example.dukan.activities.CartListActivity
import com.example.dukan.activities.SettingsActivity
import com.example.dukan.activities.ui.dashboard.DashboardViewModel
import com.example.dukan.adapters.DashboardItemsListAdapter
import com.example.dukan.databinding.FragmentDashboardBinding
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Product
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : BaseFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private var _binding: FragmentDashboardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu , menu )
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      val id = item.itemId
      when(id) {
          R.id.action_settings -> {
              startActivity(Intent(activity , SettingsActivity::class.java))
              return true
          }

          R.id.action_cart -> {
              startActivity(Intent(activity , CartListActivity::class.java))
              return true
          }
      }

        return super.onOptionsItemSelected(item)
    }


    fun successDashBoardItemsList(dashboardItemsList : ArrayList<Product>) {
        hideProgressDialog()

        if(dashboardItemsList.size > 0) {
            rv_dashboard_items.visibility = View.VISIBLE
            tv_no_dashboard_items_found.visibility = View.GONE


             rv_dashboard_items.layoutManager = GridLayoutManager(activity , 2)
             rv_dashboard_items.setHasFixedSize(true)

            val adapter  = DashboardItemsListAdapter(requireActivity() , dashboardItemsList)
            rv_dashboard_items.adapter = adapter

        } else {
            rv_dashboard_items.visibility = View.GONE
            tv_no_dashboard_items_found.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))

        fireStoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

}