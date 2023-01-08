package com.example.gmcontacts

import android.graphics.*

data class Contact(val id : String,val name: String, val phoneNumbers: List<Pair<String,String>>, var emails: List<Pair<String,String>>,val img : Bitmap?) {

}

