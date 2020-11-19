package com.example.messengerproject.ui.fragments

import android.view.*
import androidx.fragment.app.Fragment
import com.example.messengerproject.MainActivity
import com.example.messengerproject.R
import com.example.messengerproject.SettingsActivity
import com.example.messengerproject.utilits.*
import kotlinx.android.synthetic.main.fragment_edit.*


class EditFragment : Fragment(R.layout.fragment_edit) {

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        if (USER.fullname.isNotEmpty()) {
            val nameAndSurname = USER.fullname.split(" ")
            edit_name.setText(nameAndSurname[0])
            edit_surname.setText(nameAndSurname[1])
        }
        edit_phone.setText(USER.phone)
        edit_username.setText(USER.username)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as SettingsActivity).menuInflater.inflate(R.menu.settings_menu_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings_confirm_change -> changeUserData()
        }
        return true
    }

    private fun changeUserData() {
        val firstName = edit_name.text.toString()
        val secondName = edit_surname.text.toString()
        val phone = edit_phone.text.toString()
        val username = edit_username.text.toString()
        if (firstName.isEmpty() || secondName.isEmpty() || phone.isEmpty() || username.isEmpty())
        {
            showToast("Empty field!")
        }
        else {
            val fullname = "$firstName $secondName"
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_PHONE).setValue(phone)
            USER.phone = phone
            if (USER.id == "") {
                USER.id = UID
                REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_ID).setValue(UID)
            }
            changeUsername(username)
            REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_FULLNAME)
                .setValue(fullname).addOnCompleteListener{
                    if (it.isSuccessful) {
                        showToast("Data reloaded")
                        USER.fullname = fullname
                        fragmentManager?.popBackStack()
                    }
                }
        }
    }

    private fun changeUsername(username: String) {
        REF_DATABASE_ROOT.child(NODE_USERNAMES)
            .addListenerForSingleValueEvent(AppValueEventListener{
                if (it.hasChild(username)) {
                    showToast("This username has already exists")
                }
                else {
                    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
                    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(username).setValue(UID)
                    REF_DATABASE_ROOT.child(NODE_USERS).child(UID).child(CHILD_USERNAME).setValue(username)
                    USER.username = username
                }
            })
    }

}