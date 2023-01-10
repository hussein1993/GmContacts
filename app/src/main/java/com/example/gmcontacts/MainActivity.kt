package com.example.gmcontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.gmcontacts.ui.theme.GmContactsTheme


class MainActivity : ComponentActivity() {


    val PERMISSION_ALL = 1
    lateinit var _viewModel :ContactsViewModel
    lateinit var navController : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("huss","ONCreate")

        _viewModel=  ViewModelProvider(this).get(ContactsViewModel::class.java)

        setContent {

            GmContactsTheme {
                navController = rememberNavController()
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    Log.i("huss","SUrface")
//                    _viewModel.permissionGranted.observe(this, Observer {
//
//                    })
                    _viewModel.contacts.observe(this,{

                    })
                   SetupNavGraph(navController = navController, _viewModel = _viewModel)
                   // MainScreen(contactsViewModel = _viewModel)

                }
            }
        }

        if(_viewModel.permissionGranted.value == false) {
            requestReadContactsPermission(this)
        }
    }


    fun requestReadContactsPermission(mainActivity: MainActivity) {
        // Check if the app has read contacts permission
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            // Request read contacts permission
        ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.READ_CONTACTS),PERMISSION_ALL)
        } else {
            _viewModel.setPermissionGranted(true)
            Log.i("huss","XXXXXXXXXXXXXXXXXXXx")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission was granted, set the flag in the viewModel
                _viewModel.setPermissionGranted(true)
                Log.i("huss","result Permission arrive Goood")
            } else {
                // Permission was denied, set the flag in the viewModel
                _viewModel.setPermissionGranted(false)
            }
        }
    }

}





