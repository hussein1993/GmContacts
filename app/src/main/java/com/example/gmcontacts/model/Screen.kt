package com.example.gmcontacts.model

sealed class Screen(val route :String){
    object Contacts : Screen(route = "contacts_screen")
    object Profile : Screen(route = "profile_screen")

}
