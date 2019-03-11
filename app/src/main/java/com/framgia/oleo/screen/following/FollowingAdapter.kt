package com.framgia.oleo.screen.following

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.databinding.AdapterFollowingBinding

class FollowingAdapter : RecyclerView.Adapter<FollowingAdapter.Companion.ViewHolder>() {
    private var users: MutableList<User> = mutableListOf()
    private lateinit var onItemViewListener: OnItemViewListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AdapterFollowingBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_following, parent, false)
        return ViewHolder(binding, onItemViewListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(users.get(position))
    }

    fun updateData(users: List<User>) {
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun setOnItemViewListener(onItemViewListener: OnItemViewListener) {
        this.onItemViewListener = onItemViewListener
    }

    interface OnItemViewListener {
        fun onBlockClick(user: User)
    }

    companion object {
        class ViewHolder(
            private val binding: AdapterFollowingBinding,
            private val onItemViewListener: OnItemViewListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.listener = onItemViewListener
            }

            fun bindData(user: User) {
                binding.user = user
            }
        }
    }
}
