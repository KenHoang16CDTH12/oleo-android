package com.framgia.oleo.screen.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.framgia.oleo.R
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.databinding.ItemLayoutContactsBinding
import com.framgia.oleo.utils.OnItemRecyclerViewClick

class ContactsAdapter(private var onItemRecyclerViewClick: OnItemRecyclerViewClick<Friend>? = null
) : RecyclerView.Adapter<ContactsAdapter.Companion.ItemViewHolder>() {

    private var contacts = mutableListOf<Friend>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DataBindingUtil.inflate<ItemLayoutContactsBinding>(
            LayoutInflater.from(parent.context), R.layout.item_layout_contacts, parent, false
        )
        return ItemViewHolder(binding, onItemRecyclerViewClick!!)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindViewData(contacts[position], position)
    }

    fun updateData(data : MutableList<Friend>){
        contacts.clear()
        contacts.addAll(data)
        notifyDataSetChanged()
    }

    companion object {

        class ItemViewHolder(
            private val binding: ItemLayoutContactsBinding,
            private val listener: OnItemRecyclerViewClick<Friend>,
            private val viewModel: ItemContactsViewModel = ItemContactsViewModel(listener)
        ) : RecyclerView.ViewHolder(binding.root) {

            init {
                binding.viewModel = viewModel
            }

            fun bindViewData(friend: Friend, position: Int) {
                viewModel.position = position
                viewModel.setData(friend)
                binding.executePendingBindings()
            }
        }
    }
}
