package com.example.dukan.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dukan.R
import kotlinx.android.synthetic.main.activity_add_edit_address.*

class AddEditAddressActivity : AppCompatActivity()  , View.OnClickListener{
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

    override fun onClick(view : View?) {
        if(view != null) {
            when(view.id) {
                R.id.btn_submit_address -> {

                }
            }
        }
    }

  I am doing this to learn the revert git command
}