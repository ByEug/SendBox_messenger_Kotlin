package com.example.messengerproject.ui.fragments.single_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.messengerproject.R
import com.example.messengerproject.models.CommonModel
import com.example.messengerproject.utilits.UID
import com.example.messengerproject.utilits.asTime
import kotlinx.android.synthetic.main.message_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChtHolder>() {

    var mlistMessagesCache = emptyList<CommonModel>()

    class SingleChtHolder(view: View): RecyclerView.ViewHolder(view) {
        val blockUserMessage:ConstraintLayout = view.block_user_message
        val chatUserMessage: TextView = view.chat_user_message
        val chatUserMessageTime: TextView = view.chat_user_message_time

        val blockReceivedMessage:ConstraintLayout = view.block_received_message
        val chatReceivedMessage: TextView = view.chat_received_message
        val chatReceivedMessageTime: TextView = view.chat_received_message_time


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChtHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)



        return SingleChtHolder(view)
    }

    override fun getItemCount(): Int = mlistMessagesCache.size

    override fun onBindViewHolder(holder: SingleChtHolder, position: Int) {
        if (mlistMessagesCache[position].from == UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceivedMessage.visibility = View.GONE
            holder.chatUserMessage.text = mlistMessagesCache[position].text
            holder.chatUserMessageTime.text =
                mlistMessagesCache[position].timeStamp.toString().asTime()
        }
        else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceivedMessage.visibility = View.VISIBLE
            holder.chatReceivedMessage.text = mlistMessagesCache[position].text
            holder.chatReceivedMessageTime.text =
                mlistMessagesCache[position].timeStamp.toString().asTime()
        }
    }

    fun setList(list: List<CommonModel>) {
        mlistMessagesCache = list
        notifyDataSetChanged()
    }
}
