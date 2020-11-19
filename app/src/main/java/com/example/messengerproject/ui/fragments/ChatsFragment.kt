package com.example.messengerproject.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerproject.MainActivity
import com.example.messengerproject.R
import com.example.messengerproject.databinding.FragmentMainListBinding
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.ui.fragments.main_list.MainListAdapter
import com.example.messengerproject.utilits.*
import kotlinx.android.synthetic.main.fragment_main_list.*
import kotlin.collections.forEach as forEach1

class ChatsFragment : Fragment(R.layout.fragment_main_list) {

    private lateinit var recyclerView:RecyclerView
    private lateinit var mAdapter: MainListAdapter
    private val mRefMainList = REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(UID)
    private val mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS)
    private val mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(UID)
    private var mListItems = listOf<CommonModel>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        (activity as MainActivity).title = "Sendbox"
        initRecyclerView()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initRecyclerView() {
        recyclerView = main_list_recycler_view
        mAdapter = MainListAdapter()
        mRefMainList.addListenerForSingleValueEvent(AppValueEventListener{
            mListItems = it.children.map { it.getCommonModel() }
            mListItems.forEach { model ->


                mRefUsers.child(model.id).addListenerForSingleValueEvent(AppValueEventListener{
                    val newModel = it.getCommonModel()


                    mRefMessages.child(model.id).limitToLast(1).addListenerForSingleValueEvent(AppValueEventListener{
                        val tempList = it.children.map {it.getCommonModel()}

                        if (tempList.isEmpty()) {
                            newModel.lastMessage = "Chat has been cleared"
                        } else {
                            newModel.lastMessage = tempList[0].text
                        }


                        if (newModel.fullname.isEmpty()) {
                            newModel.fullname = newModel.phone
                        }

                        mAdapter.updateListItems(newModel)

                    })
                })
            }
        })

        recyclerView.adapter = mAdapter
    }
}