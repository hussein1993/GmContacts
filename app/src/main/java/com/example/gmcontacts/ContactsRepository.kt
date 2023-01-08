package com.example.gmcontacts

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import android.provider.ContactsContract

class ContactsRepository( val _context: Context){

    // Method that retrieves all the contacts data
    @SuppressLint("Range")
     fun importContacts(filterName:String):  List<Contact>  {
        val contacts = mutableListOf<Contact>()
        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
        )
        val cursor = contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
            if (filterName.isNullOrEmpty()) null else "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
            , if (filterName.isNullOrEmpty()) null else arrayOf("%$filterName%")
            , null
        )
        cursor?.let {
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    var phoneNumbers : List<Pair<String, String>>
                    var emailAddresses : List<Pair<String, String>>

                    val id =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))

                    phoneNumbers = getNumbersTypes(contentResolver,id)
                    emailAddresses = getEmailsTypes(contentResolver,id)

                    val photoUri =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI))

                    var  photo : Bitmap? = null
                    if (photoUri != null){
                        val inputStream = contentResolver.openInputStream(Uri.parse(photoUri))
                         photo = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()
                    }
                    val _cont = Contact(id= id ,name = name, phoneNumbers = phoneNumbers, emails = emailAddresses, img = photo)
                    contacts.add(_cont)
                }
            }
        }
        cursor?.close()
        contacts.sortBy { it.name }
        return contacts.distinctBy { it.name }
    }

    @SuppressLint("Range")
    private fun getNumbersTypes(contentResolver: ContentResolver, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val phoneNumbers = mutableListOf<Pair<String, String>>()
        val phoneCursor = contentResolver.query(
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
                val phoneTypeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(_context.resources, phoneType.toInt(), null).toString()
                phoneNumbers.add(Pair(phoneNumber, phoneTypeLabel))
            } while (phoneCursor.moveToNext())
        }

        phoneCursor?.close()
        return  phoneNumbers
    }


    @SuppressLint("Range")
    private fun getEmailsTypes(contentResolver: ContentResolver, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val emailAddresses = mutableListOf<Pair<String, String>>()
        val emailCursor = contentResolver.query(
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
                val emailTypeLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(_context.resources, emailType.toInt(), null).toString()
                emailAddresses.add(Pair(emailAddress, emailTypeLabel))
            } while (emailCursor.moveToNext())
        }

        emailCursor?.close()
        return  emailAddresses
    }

}