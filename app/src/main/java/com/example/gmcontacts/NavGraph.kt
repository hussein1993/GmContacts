package com.example.gmcontacts

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gmcontacts.ViewModels.ContactsViewModel
import com.example.gmcontacts.model.Screen

@Composable
fun SetupNavGraph(navController: NavHostController, _viewModel: ContactsViewModel){
    NavHost(navController = navController,
        startDestination = Screen.Contacts.route )
    {
        composable(
            route = Screen.Contacts.route
        ){
            MainScreen(_viewModel, navController = navController)
        }
        composable(
            route = Screen.Profile.route
        ){
            ProfileScreen(_viewModel)
        }

    }
}