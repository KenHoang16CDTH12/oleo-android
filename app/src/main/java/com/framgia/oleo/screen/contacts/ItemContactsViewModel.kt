package com.framgia.oleo.screen.contacts

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.framgia.oleo.BR
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.utils.OnItemRecyclerViewClick
import com.framgia.oleo.utils.extension.notNull

class ItemContactsViewModel(
    private val listener: OnItemRecyclerViewClick<Friend>? = null) : BaseObservable() {

    @Bindable
    var contacts: Friend? = null
    var position = 0

    fun setData(data: Friend?) {
        data.notNull {
            contacts = it
            notifyPropertyChanged(BR.contacts)
        }
    }

    fun onItemClick(){
        contacts.notNull { listener?.onItemClickListener(it) }
    }
}
