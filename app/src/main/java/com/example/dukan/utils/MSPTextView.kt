package com.example.dukan.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextView(context : Context, attrs : AttributeSet) :AppCompatTextView(context , attrs ) {

    init {
            changeFont()
    }

    private fun changeFont(){
        var typeFace : Typeface = Typeface.createFromAsset(context.assets , "Montserrat-Regular.ttf")
        setTypeface(typeFace)
    }
}