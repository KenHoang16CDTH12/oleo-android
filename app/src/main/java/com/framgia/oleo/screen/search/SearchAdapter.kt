package com.framgia.oleo.screen.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.AdapterSearchBindingImpl
import com.framgia.oleo.utils.extension.hide
import com.framgia.oleo.utils.extension.show

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.Companion.ViewHolder>() {
    private var users: MutableList<User> = arrayListOf()
    private var friends: MutableList<Friend> = arrayListOf()
    private lateinit var onItemViewListener: OnItemViewListener
    private var userId: String? = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AdapterSearchBindingImpl =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_search, parent, false)
        return ViewHolder(binding, onItemViewListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (users[position].id == userId) {
            holder.bindData(users[position], true)
            return
        }
        for (i in friends) {
            if (users[position].id == i.id) {
                holder.bindData(users[position], true)
                return
            }
        }
        holder.bindData(users[position], false)
    }

    fun setUserFriend(friend: MutableList<Friend>) {
        this.friends.clear()
        this.friends = friend
    }

    fun updateData(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun setOnItemViewListener(onItemViewListener: OnItemViewListener) {
        this.onItemViewListener = onItemViewListener
    }

    fun setUserId(userId: String) {
        this.userId = userId
    }

    interface OnItemViewListener {
        fun onImageSendMessageClicked(user: User)

        fun onImageAddFriendClicked(user: User)

        fun onImageUserProfileClicked(user: User)
    }

    companion object {
        class ViewHolder(
            private val binding: AdapterSearchBindingImpl,
            onItemViewListener: OnItemViewListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.listener = onItemViewListener
            }

            fun bindData(user: User, hideRequest: Boolean) {
                if (hideRequest) binding.imageAddFriend.hide()
                else binding.imageAddFriend.show()
                binding.user = user
            }
        }
    }
}
