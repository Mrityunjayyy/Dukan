package com.example.dukan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dukan.R
import kotlinx.android.synthetic.main.activity_add_edit_address.*
import kotlinx.android.synthetic.main.activity_address_list.*

class AddEditAddressActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setUpActionBar()
    }

    private fun setUpActionBar() {
        setSupportActionBar(toolbar_add_edit_address_activity)
        val actionBar = supportActionBar
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }

    }
}