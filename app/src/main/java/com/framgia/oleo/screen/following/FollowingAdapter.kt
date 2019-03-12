package com.framgia.oleo.screen.following

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.databinding.AdapterFollowingBinding
import com.framgia.oleo.utils.extension.lastIndex

class FollowingAdapter : RecyclerView.Adapter<FollowingAdapter.Companion.ViewHolder>() {
    private var followRequests: MutableList<FollowRequest> = mutableListOf()
    private lateinit var onItemViewListener: OnItemViewListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: AdapterFollowingBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_following, parent, false)
        return ViewHolder(binding, onItemViewListener)
    }

    override fun getItemCount(): Int {
        return followRequests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(followRequests.get(position))
    }

    fun addFollowRequest(followRequest: FollowRequest) {
        followRequests.add(followRequest)
        notifyItemInserted(followRequests.lastIndex)
    }

    fun removeFollowRequest(followRequest: FollowRequest) {
        val index = followRequests.indexOf(followRequests.find { it.id == followRequest.id })
        followRequests.removeAt(index)
        notifyItemRemoved(index)
    }

    fun changeFollowRequest(followRequest: FollowRequest) {
        val index = followRequests.indexOf(followRequests.find { it.id == followRequest.id })
        followRequests.set(index, followRequest)
        notifyItemChanged(index)
    }

    fun setOnItemViewListener(onItemViewListener: OnItemViewListener) {
        this.onItemViewListener = onItemViewListener
    }

    interface OnItemViewListener {
        fun onBlockClick(followRequest: FollowRequest)
    }

    companion object {
        class ViewHolder(
            private val binding: AdapterFollowingBinding,
            private val onItemViewListener: OnItemViewListener
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.listener = onItemViewListener
            }

            fun bindData(followRequest: FollowRequest) {
                binding.followRequest = followRequest
            }
        }
    }
}
