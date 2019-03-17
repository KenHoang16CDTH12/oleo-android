package com.framgia.oleo.screen.messages

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.MessagesRepository
import com.framgia.oleo.data.source.model.BoxChat
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.AdapterMessageBinding
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.Index
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import kotlinx.android.synthetic.main.adapter_message.view.*

class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.Companion.MessagesHolder>() {

    private var messages: MutableList<BoxChat> = arrayListOf()
    private lateinit var listener: OnItemRecyclerViewClick<BoxChat>
    private lateinit var user: User
    private lateinit var messagesRepository: MessagesRepository

    fun setListener(itemClickListener: OnItemRecyclerViewClick<BoxChat>) {
        listener = itemClickListener
    }

    fun addData(boxChat: BoxChat) {
        val index = messages.indexOfFirst { it.id == boxChat.id }
        if (index < Index.POSITION_ZERO) {
            messages.add(boxChat)
            notifyItemInserted(messages.lastIndex)
        }
    }

    fun removeData(boxChat: BoxChat) {
        val index = messages.indexOfFirst { it.id == boxChat.id }
        if (index >= Index.POSITION_ZERO) {
            messages.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun getUser(user: User) {
        this.user = user
    }

    fun setMessageRepository(messagesRepository: MessagesRepository) {
        this.messagesRepository = messagesRepository
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesHolder {
        val binding: AdapterMessageBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_message, parent, false)
        return MessagesHolder(binding, listener, user, messagesRepository)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessagesHolder, position: Int) {
        holder.bindData(messages[position])
    }

    override fun onViewAttachedToWindow(holder: MessagesHolder) {
        super.onViewAttachedToWindow(holder)
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: MessagesHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetach()
    }

    companion object {
        class MessagesHolder(
            private val binding: AdapterMessageBinding,
            private val listener: OnItemRecyclerViewClick<BoxChat>,
            private val user: User,
            private val messagesRepository: MessagesRepository
        ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, LifecycleOwner {

            private val lifecycleRegistry = LifecycleRegistry(this)

            override fun getLifecycle(): Lifecycle {
                return lifecycleRegistry
            }

            fun onAttach() {
                lifecycleRegistry.markState(Lifecycle.State.STARTED)
            }

            fun onDetach() {
                lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
            }

            init {
                lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
                binding.viewModel = MessagesAdapterViewModel(messagesRepository)
                binding.root.setOnClickListener(this)
            }

            private lateinit var boxChat: BoxChat

            fun bindData(boxChat: BoxChat) {
                this.boxChat = boxChat
                lifecycleRegistry.markState(Lifecycle.State.STARTED)
                binding.textMessage.text = ""
                binding.textTime.text = ""
                binding.viewModel!!.setMessage(user.id, boxChat.id!!)
                binding.viewModel!!.setImageProfile(boxChat.id!!)
                binding.viewModel!!.setBoxChatName(user.id, boxChat.id!!)
                getLiveData()
            }

            private fun getLiveData() {
                binding.viewModel!!.message.observe(this, Observer { message ->
                    if(message==null) return@Observer
                    binding.textMessage.text = message.message.toString()
                    binding.textTime.text = DateUtils.getRelativeTimeSpanString(
                        message.time!!, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS
                    )
                })
                binding.viewModel!!.boxChatName.observe(this, Observer {
                    itemView.textFriendName.text = it
                })
            }

            override fun onClick(v: View?) {
                listener.onItemClickListener(boxChat)
            }
        }
    }
}
