package com.example.messengerproject.ui.fragments

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerproject.MainActivity
import com.example.messengerproject.R
import com.example.messengerproject.RegisterActivity
import com.example.messengerproject.SettingsActivity
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.models.User
import com.example.messengerproject.ui.fragments.single_chat.SingleChatAdapter
import com.example.messengerproject.utilits.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_single_chat.*
import kotlinx.android.synthetic.main.toolbar_info.view.*

class SingleChatFragment(private val contact: CommonModel) : BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: User
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppValueEventListener
    private var mListMessages = emptyList<CommonModel>()

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.getAppDrawer().disableDrawer()
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        initToolbar()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = chat_recycler_view
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mMessagesListener = AppValueEventListener { dataSnapshot ->
            mListMessages = dataSnapshot.children.map {
                it.getCommonModel()
            }
            mAdapter.setList(mListMessages)
            mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
        }
        mRefMessages.addValueEventListener(mMessagesListener)
    }

    private fun initInfoToolbar() {
        mToolbarInfo.toolbar_image.downloadAndSetImage(mReceivingUser.photoUrl)
        mToolbarInfo.toolbar_fullname.text = mReceivingUser.fullname
        mToolbarInfo.toolbar_phone.text = mReceivingUser.phone
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = (activity as MainActivity).mainToolBar.toolbar_info
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUser()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        chat_btn_send_message.setOnClickListener {
            val message = chat_input_message.text.toString()
            if (message.isEmpty()) {
                showToast("Enter message")
            }
            else {
                sendMessage(message, contact.id, TYPE_TEXT) {
                    saveToMainList(contact.id, TYPE_CHAT)
                    chat_input_message.setText("")
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.getAppDrawer().enableDrawer()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        (activity as MainActivity).menuInflater.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_clear_chat -> clearChat(contact.id) {
                showToast("Chat has been cleared")
                replaceFragment(ChatsFragment(), false)
            }
            R.id.menu_delete_chat -> deleteChat(contact.id) {
                showToast("Chat has been deleted")
                replaceFragment(ChatsFragment(), false)
            }
        }
        return true
    }

}