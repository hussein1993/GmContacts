package com.example.gmcontacts

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.*


class ContactsViewModel(application: Application) : AndroidViewModel(application){

    private val repository = ContactsRepository(application.applicationContext)

    val _permissionGranted = MutableLiveData(false)
    val permissionGranted : LiveData<Boolean> = _permissionGranted

    private val _selectedContact = MutableLiveData<Contact>()
    val selectedContact : LiveData<Contact> = _selectedContact

    // LiveData object that will hold the list of Contact objects
    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts : LiveData<List<Contact>> = _contacts

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)

    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _isDataReady: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

    val isDataReady: LiveData<Boolean> = _isDataReady


    private val _searchTextState: MutableState<String> =
        mutableStateOf(value = "")
    val searchTextState: State<String> = _searchTextState


    init {
        loadContacts(permissionGranted.value)
    }

    fun updateSelectedContact(contact: Contact) {

       _selectedContact.value = repository.getContactData(contact = contact)
    }

    fun setPermissionGranted(granted: Boolean) {
        _permissionGranted.value = granted
        loadContacts(granted)
    }


    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }
    fun updateIsDataAvailable(newValue: Boolean) {
        _isDataReady.value = newValue
    }


    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
        loadContacts(permissionGranted.value)
        Log.i("huss", "loadContacts-SEAARCHHHH")
    }


    // Method that retrieves the contacts data from the repository and updates the LiveData object
    fun loadContacts(granted: Boolean?) {
        Log.i("huss", "loadContacts-${granted}")
        if (granted == true) {
            viewModelScope.launch {
                val contactList = repository.importContacts(filterName = searchTextState.value)
                _contacts.value = contactList
                updateIsDataAvailable(true)
                Log.i("huss", "loadContacts-${contactList.size}")
            }
        }
    }


    fun onRepoSuccess(data: List<Contact>) {
        _contacts.value = data
    }
}
