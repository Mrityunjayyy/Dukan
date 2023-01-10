package com.example.dukan.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSPRadioButton(context : Context, attrs : AttributeSet) : AppCompatRadioButton(context , attrs) {
    init {
        changeFont()
    }

    private fun changeFont(){
        val typeFace : Typeface = Typeface.createFromAsset(context.assets , "Montserrat-Bold.ttf")
        setTypeface(typeFace)
    }
}
