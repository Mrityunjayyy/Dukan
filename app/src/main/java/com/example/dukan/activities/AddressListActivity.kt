package com.example.dukan.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dukan.R
import kotlinx.android.synthetic.main.activity_address_list.*

class AddressListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setUpActionBar()

        tv_add_address.setOnClickListener{
            startActivity(Intent(this@AddressListActivity , AddEditAddressActivity::class.java ))
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_address_list_activity)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }

    }
}