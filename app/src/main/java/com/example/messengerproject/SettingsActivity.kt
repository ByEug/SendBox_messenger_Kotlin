package com.example.messengerproject

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.messengerproject.databinding.ActivitySettingsBinding
import com.example.messengerproject.ui.fragments.SettingsFragment
import com.example.messengerproject.utilits.APP_ACTIVITY
import com.example.messengerproject.utilits.replaceFragmentInSettings

class SettingsActivity : AppCompatActivity() {

    private lateinit var mToolbar: Toolbar
    private lateinit var mBinding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    @SuppressLint("RestrictedApi")
    override fun onStart() {
        super.onStart()
        mToolbar = mBinding.settingsToolBar
        setSupportActionBar(mToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        mToolbar.setNavigationIcon(R.drawable.ic_settings_back)
        mToolbar.setNavigationOnClickListener {
            val fragments: List<Fragment> = this.supportFragmentManager.fragments
            for (fragment: Fragment in fragments) {
                if (fragment::class == SettingsFragment()::class) {
                    APP_ACTIVITY.getAppDrawer().enableDrawer()
                    this.finish()
                }
                if (fragment.isVisible) {
                    this.supportFragmentManager.popBackStack()
                }
            }
        }
        replaceFragmentInSettings(SettingsFragment(), false)
    }

}

