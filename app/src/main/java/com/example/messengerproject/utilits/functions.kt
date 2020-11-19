package com.example.messengerproject.utilits

import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.messengerproject.R
import com.example.messengerproject.RegisterActivity
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.ui.fragments.ChatsFragment
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_settings.*
import java.text.SimpleDateFormat
import java.util.*

fun ImageView.downloadAndSetImage(url: String) {
    Picasso.get()
        .load(url)
        .placeholder(R.drawable.user)
        .into(this)
}

fun ImageView.downloadAndSetImageForLeftMenu(url: String) {
    Picasso.get()
        .load(url)
        .fit()
        .placeholder(R.drawable.user)
        .into(this)
}

fun Fragment.showToast(message:String) {
    Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.replaceActivity(activity: AppCompatActivity) {
    val intent = Intent(this, activity::class.java)
    startActivity(intent)
    this.finish()
}

fun AppCompatActivity.replaceFragmentInSettings(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.settingsContainer,
                fragment
            ).commit()
    }
    else {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.settingsContainer,
                fragment
            ).commit()
    }

}

fun replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.chatsContainer,
                fragment
            ).commit()
    }
    else {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.chatsContainer,
                fragment
            ).commit()
    }

}

fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.chatsContainer,
                fragment
            ).commit()
    }
    else {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.chatsContainer,
                fragment
            ).commit()
    }

}

fun AppCompatActivity.replaceFragmentInRegister(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.registerContainer,
                fragment
            ).commit()
    }
    else {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.registerContainer,
                fragment
            ).commit()
    }

}


fun Fragment.replaceFragment(fragment: Fragment) {
    this.fragmentManager?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(
            R.id.registerContainer,
            fragment
        )?.commit()
}

fun Fragment.replaceFragmentInSettings(fragment: Fragment, addStack: Boolean = true) {
    if (addStack) {
        this.fragmentManager?.beginTransaction()
            ?.addToBackStack(null)
            ?.replace(
                R.id.settingsContainer,
                fragment
            )?.commit()
    }
    else {
        this.fragmentManager?.beginTransaction()
            ?.replace(
                R.id.settingsContainer,
                fragment
            )?.commit()
    }

}

fun String.asTime(): String {
    val time = Date(this.toLong())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    return timeFormat.format(time)
}

fun DataSnapshot.getCommonModel(): CommonModel =
    this.getValue(CommonModel::class.java) ?: CommonModel()