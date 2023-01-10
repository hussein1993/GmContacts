package com.example.gmcontacts

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen(contactsViewModel: ContactsViewModel = viewModel(),navController: NavController){

    val searchWidgetState by remember {
        contactsViewModel.searchWidgetState
    }
    val searchTextState by remember {
        contactsViewModel.searchTextState
    }
    

    Scaffold(
        topBar = {
            MainAppBar(
                searchWidgetState = searchWidgetState,
                searchTextState = searchTextState,
                onTextChange = {
                               contactsViewModel.updateSearchTextState(newValue = it)
                },
                onCloseClicked = {
                                 contactsViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                },
                onSearchClicked = {
                    contactsViewModel.updateSearchWidgetState(newValue = SearchWidgetState.CLOSED)
                },
                onSearchTriggered = {
                    contactsViewModel.updateSearchWidgetState(newValue = SearchWidgetState.OPENED)
                }
            ) }

    ) {
        ContactsScreen(contactsViewModel = contactsViewModel,navController)
    }
}



@Composable
fun MainAppBar(
    searchWidgetState: SearchWidgetState,
    searchTextState: String,
    onTextChange: (String) -> Unit,
    onCloseClicked: () -> Unit,
    onSearchClicked: (String) -> Unit,
    onSearchTriggered: () -> Unit
) {
    when (searchWidgetState) {
        SearchWidgetState.CLOSED -> {
            DefaultAppBar(
                onSearchClicked = onSearchTriggered
            )
        }
        SearchWidgetState.OPENED -> {
            SearchAppBar(
                text = searchTextState,
                onTextChange = onTextChange,
                onCloseClicked = onCloseClicked,
                onSearchClicked = onSearchClicked
            )
        }
    }
}

@Composable
fun DefaultAppBar(onSearchClicked: () -> Unit) {
    TopAppBar(

        title = {
            Text(
                text = "Home",
                color = Color.White
            )
        },
        actions = {
            IconButton(
                onClick = { onSearchClicked() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Icon",
                    tint = Color.White
                )
            }
        },
        backgroundColor = MaterialTheme.colors.secondaryVariant
    )
}

@Composable
fun SearchAppBar(
    text : String,
    onTextChange:(String)->Unit,
    onCloseClicked:()->Unit,
    onSearchClicked:(String)->Unit
){
    Surface(modifier =
    Modifier
        .fillMaxWidth()
        .height(56.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = MaterialTheme.colors.secondaryVariant) {
        TextField( value = text, onValueChange = {
            onTextChange(it)
        },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = "Search here...",
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(modifier = Modifier.alpha(ContentAlpha.medium),
                    onClick = { })
                {
                    Icon(imageVector = Icons.Default.Search,
                        contentDescription = "searchIcon",
                        tint =   Color.White)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = {
                        if(text.isNotEmpty()){
                            onTextChange("")
                        }else{
                            onCloseClicked()
                        }
                    })
                {
                    Icon(imageVector = Icons.Default.Close,
                        contentDescription = "CloseIcon",
                        tint =   Color.White)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClicked(text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            )
        )
    }
}


@Composable
fun ContactsScreen(contactsViewModel: ContactsViewModel = viewModel(), navController: NavController){

    val res : List<Contact> by contactsViewModel.contacts.observeAsState(ArrayList<Contact>())
    val isDataAvaialable =  contactsViewModel.isDataReady.observeAsState(false)
    if(res.isEmpty()){

        if(isDataAvaialable.value) {
            Text(
                text = "No Contacts Found!",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),

                fontSize = 20.sp
            )
        }else{
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth().padding(15.dp)
            ) {
                CircularProgressIndicator()
            }

        }
    }else {
        ContactsContent(contacts = res,navController,contactsViewModel)
    }
}


@Composable
fun ContactsContent(
    contacts: List<Contact>,
    navController: NavController,
    contactsViewModel: ContactsViewModel
) {
    Box {
            LazyColumn(Modifier.fillMaxWidth()) {
                items(contacts) { contact ->
                    ContactCard(contact = contact, navController, contactsViewModel)
                    Spacer(
                        modifier = Modifier
                            .height(10.dp)
                            .background(color = MaterialTheme.colors.secondaryVariant)
                    )
                }
            }



    }
}


@Composable
fun ContactCard(
    contact: Contact,
    navController: NavController,
    contactsViewModel: ContactsViewModel
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp)
        .clickable {
            contactsViewModel.updateSelectedContact(contact = contact)
            navController.navigate(route = Screen.Profile.route)
        }) {
        if(contact.img == null){
            CircleImageWithInitials(contact.name[0]+"", Color(0xff44B08C))
        }else {
            Image(
                bitmap = contact.photo,
                contentDescription = "pImg",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = contact.name,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterVertically)
        )


    }
}

@Composable
fun CircleImageWithInitials(initials: String, color: Color) {
    Box(modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .background(color = color)
        .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape) ){
        Text(text = initials,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp)
    }
}