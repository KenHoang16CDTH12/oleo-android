package com.framgia.oleo.screen.followed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.databinding.AdapterFollowedBinding
import com.framgia.oleo.utils.extension.lastIndex

class FollowedAdapter : RecyclerView.Adapter<FollowedAdapter.Companion.ViewHolder>() {
    private var followeds: MutableList<Followed> = mutableListOf()
    private lateinit var onItemViewListener: OnItemViewListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AdapterFollowedBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_followed, parent, false)
        return ViewHolder(binding, onItemViewListener)
    }

    override fun getItemCount(): Int {
        return followeds.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(followeds.get(position))
    }

    fun addFollowRequest(followed: Followed) {
        followeds.add(followed)
        notifyItemInserted(followeds.lastIndex)
    }

    fun removeFollowRequest(followed: Followed) {
        val index = followeds.indexOf(followeds.find { it.id == followed.id })
        followeds.removeAt(index)
        notifyItemRemoved(index)
    }

    fun changeFollowRequest(followed: Followed) {
        val index = followeds.indexOf(followeds.find { it.id == followed.id })
        followeds.set(index, followed)
        notifyItemChanged(index)
    }

    fun setOnItemViewListener(onItemViewListener: OnItemViewListener) {
        this.onItemViewListener = onItemViewListener
    }

    interface OnItemViewListener {
        fun onUnfollowClick(followed: Followed)
    }

    companion object {
        class ViewHolder(
            private val binding: AdapterFollowedBinding,
            private val onItemViewListener: OnItemViewListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.listener = onItemViewListener
            }

            fun bindData(followed: Followed) {
                binding.followed = followed
            }
        }
    }
}
