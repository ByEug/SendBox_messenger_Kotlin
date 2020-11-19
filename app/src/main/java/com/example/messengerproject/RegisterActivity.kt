package com.example.messengerproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.messengerproject.databinding.ActivityRegisterBinding
import com.example.messengerproject.ui.fragments.EnterPhoneNumberFragment
import com.example.messengerproject.utilits.initFirebase
import com.example.messengerproject.utilits.replaceFragment
import com.example.messengerproject.utilits.replaceFragmentInRegister

class RegisterActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityRegisterBinding
    private lateinit var mToolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        mToolBar = mBinding.registerToolBar
        setSupportActionBar(mToolBar)
        title = "Your phone:"
        replaceFragmentInRegister(EnterPhoneNumberFragment(), false)
    }
}