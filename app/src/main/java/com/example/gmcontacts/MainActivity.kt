package com.example.gmcontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.gmcontacts.ui.theme.GmContactsTheme

class MainActivity : ComponentActivity() {
    lateinit var _repo : ContactsRepository



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestReadContactsPermission(this)
        Log.i("huss","ONCreate")



        setContent {
            val _viewModel : ContactsViewModel by viewModels()
            GmContactsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Log.i("huss","SUrface")
                    _viewModel.loadContacts()
                    MainScreen(contactsViewModel = _viewModel)

                }
            }
        }
    }


    fun requestReadContactsPermission(mainActivity: MainActivity) {
        val PERMISSION_ALL = 1
        // Check if the app has read contacts permission
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // Request read contacts permission
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.READ_CONTACTS),PERMISSION_ALL)
        } else {
            _repo = ContactsRepository(applicationContext)
            // If the app already has read contacts permission, you can do what you need to do
            // with the contacts here
        }
    }
}





