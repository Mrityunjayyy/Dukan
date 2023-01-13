package com.example.dukan.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dukan.R
import com.example.dukan.adapters.AddressListAdapter
import com.example.dukan.firestore.fireStoreClass
import com.example.dukan.models.Address
import com.example.dukan.utils.Constants
import com.example.dukan.utils.SwipeToDeleteCallback
import com.example.dukan.utils.SwipeToEditCallback
import kotlinx.android.synthetic.main.activity_address_list.*

class  AddressListActivity : BaseActivity() {

    private var mSelectAddress : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setUpActionBar()

        tv_add_address.setOnClickListener{
            val intent = Intent(this@AddressListActivity , AddEditAddressActivity::class.java )
            startActivityForResult(intent , Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS , false)
        }

        if(mSelectAddress) {
            tv_title_address_list.text = resources.getString(R.string.title_select_address)
        }

        getAddressItemsList()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun getAddressItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        fireStoreClass().getAllAddressList(this@AddressListActivity)
    }


    fun successAddressListsFromFireStore(addressList : ArrayList<Address>) {
        hideProgressDialog()
        if ( addressList.size > 0 ) {

            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE

            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity , addressList , mSelectAddress)
            rv_address_list.adapter = addressAdapter

            if(!mSelectAddress){
                val editSwipeHandler = object:SwipeToEditCallback(this){
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }
                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)


                val deleteSwipeHandler = object: SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        fireStoreClass().deleteAddress(this@AddressListActivity , addressList[viewHolder.adapterPosition].id)
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)

            } else {
                tv_add_address.visibility = View.GONE
            }




        } else {
            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }
    }


    fun deleteAddressSuccess()  {
        hideProgressDialog()
        Toast.makeText(this@AddressListActivity , resources.getString(R.string.err_your_address_deleted_successfully) , Toast.LENGTH_LONG).show()

        getAddressItemsList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            getAddressItemsList()
        }
    }
}