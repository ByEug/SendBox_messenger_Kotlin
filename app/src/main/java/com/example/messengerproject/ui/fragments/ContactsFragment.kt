package com.example.messengerproject.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerproject.R
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.utilits.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.contact_item.view.*
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()


    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = "Contacts"
        APP_ACTIVITY.getAppDrawer().disableDrawer()
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Contacts"
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = contacts_recycler_view
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(UID)


        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()

        mAdapter = object: FirebaseRecyclerAdapter<CommonModel,ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getValue(CommonModel::class.java)

                    holder.name.text = contact?.fullname
                    holder.phone.text = contact?.phone
                    if (contact != null) {
                        holder.photo.downloadAndSetImage(contact.photoUrl)
                        holder.itemView.setOnClickListener {
                            fragmentManager?.beginTransaction()
                                ?.addToBackStack(null)
                                ?.replace(
                                    R.id.chatsContainer,
                                    SingleChatFragment(
                                        contact
                                    )
                                )?.commit()
                        }
                    }

                }

                mRefUsers.addValueEventListener(mRefUsersListener)

                mapListeners[mRefUsers] = mRefUsersListener

            }

        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.getAppDrawer().enableDrawer()
    }

    class ContactsHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.contact_fullname
        val photo: CircleImageView = view.contact_photo
        val phone: TextView = view.contact_phone
    }

}