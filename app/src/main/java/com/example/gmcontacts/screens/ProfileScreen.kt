package com.example.gmcontacts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gmcontacts.ViewModels.ContactsViewModel
import com.example.gmcontacts.model.Contact


@Composable
fun ProfileScreen(profileViewModel: ContactsViewModel = viewModel()){
   // var res = rememberSaveable {
     //   mutableStateOf(profileViewModel.selectedContact)
  //  }
    val res : Contact by profileViewModel.selectedContact.observeAsState(
        Contact("","", listOf(),
        listOf(),null)
    )
    if(res.name.isNullOrEmpty()){
        Text(text = "NoPermission",
            textAlign = TextAlign.Center
        )
    }else {
        ProfileContent(res)
    }
}

@Composable
fun ProfileContent(contact: Contact) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.verticalScroll(rememberScrollState())) {
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxSize()
            ) {
            if(contact.img == null){
                ProfileImageWithInitials(contact.name[0]+"",Color(0xff44B08C))
            }else {
                Box(modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()) {
                    Image(
                        bitmap = contact.photo,
                        contentDescription = "pImg",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = contact.name,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                fontSize = MaterialTheme.typography.h4.fontSize,
            fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.height(20.dp))
            InfoLayouts(contact.phoneNumbers)
            InfoLayouts(contact.emails)

        }
    }
}

@Composable
fun InfoLayouts(infoListPairs: List<Pair<String, String>>) {

       Column(
           Modifier
               .fillMaxWidth()
               .padding(5.dp)){
           infoListPairs.forEach { item ->
                   InfoRow(item)

               Spacer(modifier = Modifier
                   .height(10.dp)
                   .background(color = MaterialTheme.colors.secondaryVariant))
           }
       }

}

@Composable
fun InfoRow(infoPair: Pair<String,String>){
    Card(elevation = 10.dp,
    shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(all = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,) {
            Text(
                text = infoPair.first,
                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 24.sp
            )
            Text(
                text = infoPair.second,

                color = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 24.sp
            )


        }
    }

}


@Composable
fun ProfileImageWithInitials(initials: String, color: Color) {
    Box(modifier = Modifier
        .height(200.dp)
        .fillMaxWidth()
        .background(color = color)){
        Text(text = initials,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
            fontSize = 48.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun profile() {
    ProfileContent(contact = Contact("huss", "huss", listOf(Pair("21424412","mobile"),Pair("0292992","office")),
    listOf(Pair("hasfsa@fasf.Com","work")),null)
    )
}
