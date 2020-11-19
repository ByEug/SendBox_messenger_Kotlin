package com.example.messengerproject.utilits

import android.net.Uri
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.provider.ContactsContract
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ServerValue

lateinit var AUTH: FirebaseAuth
lateinit var UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: User

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_USERNAMES = "usernames"
const val NODE_PHONES = "phone"
const val NODE_PHONES_CONTACTS = "phones_contacts"

const val FOLDER_PROFILE_IMAGE = "profile_image"

const val CHILD_ID = "id"
const val CHILD_PHONE = "phone"
const val CHILD_USERNAME = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_PHOTO_URL = "photoUrl"

const val CHILD_TEXT = "text"
const val CHILD_TYPE = "type"
const val CHILD_FROM = "from"
const val CHILD_TIMESTAMP = "timeStamp"

const val NODE_MAIN_LIST = "main_list"


fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = User()
    UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            //showToast("Fail in putUrlToDatabase")
        }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener {
            function(it.toString())
        }
        .addOnFailureListener {
            //showToast("Fail in getUrlFromStorage")
        }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener {
            function()
        }
        .addOnFailureListener {
            //showToast("Fail in putImageToStorage")
        }
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(UID)
        .addListenerForSingleValueEvent(AppValueEventListener{
            USER = it.getValue(User::class.java) ?: User()
            if (USER.username.isEmpty()) {
                USER.username = UID
            }
            function()
        })
}

fun initContacts() {
    if (checkPermission(READ_CONTACTS)) {
        var arrayContacts = arrayListOf<CommonModel>()

        val cursor = APP_ACTIVITY.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()) {
                val fullname = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val phone = it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newModel = CommonModel()
                newModel.fullname = fullname
                newModel.phone = phone.replace(Regex("[\\s, -]"), "")
                arrayContacts.add(newModel)
            }
        }

        cursor?.close()
        
        updatePhonesToDatabase(arrayContacts)

    }
}

fun updatePhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if (AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener{
            it.children.forEach {snapShot ->
                arrayContacts.forEach { contact ->
                    if (snapShot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)
                            .child(snapShot.value.toString()).child(CHILD_ID)
                            .setValue(snapShot.value.toString())
                            .addOnFailureListener {
                                println("Mistake in adding to the phone_contacts")
                            }
                    }
                }
            }
        })
    }

}


fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {

    val refDialogUser = "$NODE_MESSAGES/$UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$UID"
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { println("Troubles") }
}

fun DataSnapshot.getUser(): User =
    this.getValue(User::class.java) ?: User()


fun saveToMainList(id: String, typeChat: String) {
    val refUser = "$NODE_MAIN_LIST/$UID/$id"
    val refReceived = "$NODE_MAIN_LIST/$id/$UID"

    val mapUser = hashMapOf<String, Any>()
    val mapReceived = hashMapOf<String, Any>()

    mapUser[CHILD_ID] = id
    mapUser[CHILD_TYPE] = typeChat

    mapReceived[CHILD_ID] = UID
    mapReceived[CHILD_TYPE] = typeChat

    val commonMap = hashMapOf<String, Any>()
    commonMap[refUser] = mapUser
    commonMap[refReceived] = mapReceived

    REF_DATABASE_ROOT.updateChildren(commonMap)
        .addOnFailureListener {
            println("Trouble in main_list")
        }
}

fun clearChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID).child(id)
        .removeValue()
        .addOnFailureListener { println("check clear") }
        .addOnSuccessListener {
            REF_DATABASE_ROOT.child(NODE_MESSAGES).child(id).child(UID)
                .removeValue()
                .addOnSuccessListener { function() }
                .addOnFailureListener { println("check 2nd clear") }
        }
}

fun deleteChat(id: String, function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID).child(id).removeValue()
        .addOnFailureListener { println("check delete") }
        .addOnSuccessListener { function() }
}