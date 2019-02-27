package com.framgia.oleo.screen.friendrequest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.AdapterFriendRequestsBinding

class FriendRequestAdapter : RecyclerView.Adapter<FriendRequestAdapter.Companion.ViewHolder>() {
    private var users: MutableList<User> = mutableListOf()
    private var friendRequests: MutableList<FriendRequest> = mutableListOf()
    private lateinit var onItemViewListener: OnItemViewListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AdapterFriendRequestsBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.adapter_friend_requests,
                parent,
                false
            )
        return ViewHolder(binding, onItemViewListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(users.get(position), friendRequests)
    }

    fun updateData(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun setOnItemViewListener(onItemViewListener: OnItemViewListener) {
        this.onItemViewListener = onItemViewListener
    }

    fun setUsers(friendRequests: List<FriendRequest>) {
        this.friendRequests.clear()
        this.friendRequests.addAll(friendRequests)
    }

    interface OnItemViewListener {
        fun onConFirmClick(friendRequest: FriendRequest)
        fun onDeleteClick(friendRequest: FriendRequest)
    }

    companion object {
        class ViewHolder(
            private val binding: AdapterFriendRequestsBinding,
            private val onItemViewListener: OnItemViewListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.onItemViewListener = onItemViewListener
            }

            fun bindData(user: User, friendRequests: List<FriendRequest>) {
                binding.user = user
                for (friendRequest in friendRequests) {
                    if (friendRequest.id == user.id) {
                        binding.friendRequest = friendRequest
                        return
                    }
                }
            }
        }
    }
}
