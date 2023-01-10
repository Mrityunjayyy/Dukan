package com.example.dukan.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class MSPTextViewBold(context  : Context , attrs : AttributeSet) : AppCompatTextView(context , attrs){

    init{
        changeFont()
    }

    private fun changeFont(){
        var typeface  :Typeface = Typeface.createFromAsset(context.assets , "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}