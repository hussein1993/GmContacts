package com.example.gmcontacts.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import androidx.compose.ui.graphics.asImageBitmap
import com.example.gmcontacts.model.Contact

class ContactsRepository ( val _context: Context){



    @SuppressLint("Range")
    fun getContactData(contact: Contact): Contact {


        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
                ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.PHOTO_URI
        )
        val cursor = contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
            "${ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID} = ?",
            arrayOf(contact.id)
            ,null)



        val emailAddresses = getEmailsTypes(cursor, id = contact.id)
        val phoneNumbers = getNumbersTypes(cursor,contact.id)


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

                    val id = cursor.getString(_id)
                    val name = cursor.getString(_name)
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
    private fun getNumbersTypes(cursor: Cursor?, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.PHOTO_URI
        )

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
            val _phone_num = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val _phone_type = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)

            do {
                val phoneNumber = phoneCursor.getString(_phone_num)
                val phoneType = phoneCursor.getString(_phone_type)
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
    private fun getEmailsTypes(cursor: Cursor?, id: String): List<Pair<String, String>> {
// Get the phone numbers for the contact
        val contentResolver: ContentResolver? = _context.getContentResolver()
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.Contacts.PHOTO_URI
        )

        val emailAddresses = mutableListOf<Pair<String, String>>()
        val emailCursor = contentResolver?.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            null,
            "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
            arrayOf(id),
            null
        )

        if (emailCursor != null && emailCursor.moveToFirst()) {
            val _emailAddress = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            val _emailType = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)
            do {
               // val email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.Contacts.Data.DATA1))
                val emailAddress = emailCursor.getString(_emailAddress)
                val emailType = emailCursor.getString(_emailType)
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