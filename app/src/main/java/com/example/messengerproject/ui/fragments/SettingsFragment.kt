package com.example.messengerproject.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.example.messengerproject.MainActivity
import com.example.messengerproject.R
import com.example.messengerproject.RegisterActivity
import com.example.messengerproject.SettingsActivity
import com.example.messengerproject.utilits.*
import com.google.firebase.storage.StorageReference
import com.mikepenz.materialdrawer.util.ifNotNull
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private fun initFields() {
        username_value.text = USER.username
        phone_value.text = USER.phone
        fullname_value.text = USER.fullname
        if (USER.photoUrl.isNotEmpty()) {
            settings_user_photo.downloadAndSetImage(USER.photoUrl)
        }
        change_photo_button.setOnClickListener { changeUserPhoto() }
    }

    private fun changeUserPhoto() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)

        when(requestCode) {
            GALLERY_REQUEST -> {
                if (resultCode == RESULT_OK && imageReturnedIntent != null) {
                    val uri = imageReturnedIntent.data
                    val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(UID)
                    if (uri != null) {
                        putImageToStorage(uri, path) {
                            getUrlFromStorage(path) {
                                putUrlToDatabase(it) {

                                    if (settings_user_photo != null)
                                    {
                                        settings_user_photo.downloadAndSetImage(it)
                                    }
                                    USER.photoUrl = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as SettingsActivity).menuInflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_exit -> {
                AUTH.signOut()
                (activity as SettingsActivity).replaceActivity(RegisterActivity())
            }
            R.id.settings_menu_edit -> replaceFragmentInSettings(EditFragment())
        }
        return true
    }

}