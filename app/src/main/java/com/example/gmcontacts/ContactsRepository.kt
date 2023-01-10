package com.example.gmcontacts

import android.R
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.provider.ContactsContract
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap

class ContactsRepository ( val _context: Context){


    @SuppressLint("Range")
    fun getContactData(contact: Contact):Contact{
        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.PHOTO_URI
        )
        val phoneNumbers = getNumbersTypes(contentResolver = contentResolver,contact.id)
        val emailAddresses = getEmailsTypes(contentResolver = contentResolver, id = contact.id)

        contact.phoneNumbers = phoneNumbers
        contact.emails = emailAddresses
        return contact
    }

    @SuppressLint("Range")
    fun importContacts(filterName:String):  List<Contact>  {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.PHOTO_URI
        )
        val cursor = contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
            if (filterName.isNullOrEmpty()) null else "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
            , if (filterName.isNullOrEmpty()) null else arrayOf("%$filterName%")
            , null
        )
        cursor?.let {
            if (cursor.count > 0) {
                val _id =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID)
                val _name = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val _photo = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                val _phone_num = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val _phone_type = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                val _emailAddress = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                val _emailType = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)
                while (cursor.moveToNext()) {
                    var phoneNumbers : List<Pair<String, String>>
                    var emailAddresses : List<Pair<String, String>>

                    val id =
                        cursor.getString(_id)
                    val name =
                        cursor.getString(_name)


                    val photoUri = cursor.getString(_photo)


                    val _cont = Contact(id= id ,name = name, phoneNumbers = listOf(), emails = listOf(), img = photoUri)

                    contacts.add(_cont)
                }
            }
        }
        cursor?.close()
        contacts.sortBy { it.name }
        val resContacts =  contacts.distinctBy{ it.id }
        val _imgContacts = resContacts.filter { it.img!= null}

        _imgContacts.forEach { contact->
            val inputStream = contentResolver?.openInputStream(Uri.parse(contact.img))
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inScaled = false
            val photo = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            contact.updateBitmapImage(photo.asImageBitmap())
        }

        return resContacts
    }

    @SuppressLint("Range")
    private fun getNumbersTypes(contentResolver: ContentResolver?, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val phoneNumbers = mutableListOf<Pair<String, String>>()
        val phoneCursor =
            contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID} = ?",
            arrayOf(id),
            null
        )

        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            do {
                val phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val phoneType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                var  phoneTypeLabel : String =""
                if (!phoneType.isNullOrEmpty()){
                     phoneTypeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(_context.resources, phoneType.toInt(), null).toString()
                }

                phoneNumbers.add(Pair(phoneNumber, phoneTypeLabel))
            } while (phoneCursor.moveToNext())
        }

        phoneCursor?.close()
        return  phoneNumbers
    }


    @SuppressLint("Range")
    private fun getEmailsTypes(contentResolver: ContentResolver?, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val emailAddresses = mutableListOf<Pair<String, String>>()
        val emailCursor = contentResolver?.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(id),
            null
        )

        if (emailCursor != null && emailCursor.moveToFirst()) {
            do {
               // val email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.Data.DATA1))
                val emailAddress = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                val emailType = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))
                var emailTypeLabel = ""
                if (!emailType.isNullOrEmpty()){
                    emailTypeLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(_context.resources, emailType.toInt(), null).toString()
                }
                emailAddresses.add(Pair(emailAddress, emailTypeLabel))
            } while (emailCursor.moveToNext())
        }

        emailCursor?.close()
        return  emailAddresses
    }

}