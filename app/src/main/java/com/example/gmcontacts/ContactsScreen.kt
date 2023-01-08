package com.example.gmcontacts

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MainScreen(contactsViewModel: ContactsViewModel){

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
        ContactsScreen(contactsViewModel = contactsViewModel)
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
                text = "Home"
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
        }
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
        color = MaterialTheme.colors.primary) {
        TextField(value = text, onValueChange = {
            onTextChange(it)
        },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = "Search ...",
                    color = Color.White
                )
            },
            textStyle = TextStyle(
                fontSize = MaterialTheme.typography.subtitle1.fontSize
            ),
            singleLine = true,
            leadingIcon = {
                IconButton(modifier = Modifier.alpha(ContentAlpha.medium),
                    onClick = {})
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
fun ContactsScreen(contactsViewModel : ContactsViewModel = viewModel()){

    val res : List<Contact> by contactsViewModel.contacts.observeAsState(ArrayList<Contact>())
    if(res == null || res.size == 0){
        Text(text = "No Contacts Found!",
            textAlign = TextAlign.Center
        )
    }else {
        ContactsContent(contacts = res)
    }
}

@Composable
fun ContactsContent(contacts: List<Contact>) {
    Box {
        LazyColumn(Modifier.fillMaxWidth()) {
            items(contacts) { contact ->
                ContactCard(contact = contact)
                Spacer(modifier = Modifier
                    .height(10.dp)
                    .background(color = MaterialTheme.colors.secondaryVariant))
            }
        }

    }
}


@Composable
fun ContactCard(contact: Contact){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp)) {
        if(contact.img == null){
            CircleImageWithInitials(contact.name[0]+"", Color.Cyan)
        }else {
            Image(
                bitmap = contact.img.asImageBitmap(),
                contentDescription = "pImg",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${contact.id}-${contact.name}-${contact.phoneNumbers}-- ${contact.emails}",
            color = MaterialTheme.colors.secondaryVariant,
            modifier = Modifier.align(Alignment.CenterVertically)
        )


    }
}

@Composable
fun CircleImageWithInitials(initials: String, color: Color) {
    Box(modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape) ){
        Text(text = initials,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp)
    }
}