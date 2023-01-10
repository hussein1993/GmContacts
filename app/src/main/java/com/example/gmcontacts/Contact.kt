package com.example.gmcontacts

import android.app.Application
import android.content.ContentResolver
import android.graphics.*
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class Contact(val id : String,val name: String, var phoneNumbers: List<Pair<String,String>>, var emails: List<Pair<String,String>>,val img : String?) {

    lateinit var photo : ImageBitmap
    fun updateBitmapImage(_photo : ImageBitmap){
        photo = _photo
    }


}

