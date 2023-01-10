package com.example.dukan.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dukan.R
import com.example.dukan.activities.AddProductActivity
import com.example.dukan.activities.ui.home.HomeViewModel
import com.example.dukan.adapters.MyProductsListAdapter
import com.example.dukan.databinding.FragmentProductsBinding
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Product
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : BaseFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_prouct_menu , menu )
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when(id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity , AddProductActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    fun successProductListsFromFireStore( productsLists : ArrayList<Product>) {
        hideProgressDialog()

        if(productsLists.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)

            val adapterProducts = MyProductsListAdapter(requireActivity() , productsLists , this@ProductsFragment)

            rv_my_product_items.adapter = adapterProducts
        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }


    private fun getProductListsFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getProductsList(this@ProductsFragment)
    }

    override fun onResume() {
        super.onResume()
        getProductListsFromFireStore()
    }

    fun deleteProduct(productID : String) {
        //Toast.makeText(requireActivity() , "You can now delete the product with ID $productID" , Toast.LENGTH_LONG).show()

        showAlertDialogToDeleteProduct(productID)
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        Toast.makeText(requireActivity() ,
            resources.getString(R.string.product_delete_success_message) ,
            Toast.LENGTH_LONG)
            .show()

        getProductListsFromFireStore()
    }

    private fun showAlertDialogToDeleteProduct(productID: String) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->

            showProgressDialog(resources.getString(R.string.please_wait))
            fireStoreClass().deleteProduct(this@ProductsFragment, productID)
            dialogInterface.dismiss()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

}