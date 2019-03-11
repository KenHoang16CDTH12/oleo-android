package com.framgia.oleo.data.source.model

import android.os.Parcel
import android.os.Parcelable

class BoxChat() : Parcelable {

    var id: String? = null
    var messageList: MutableList<Message>? = null
    var userFriendName: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        userFriendName = parcel.readString()
    }

    constructor(roomId: String, messageList: MutableList<Message>, userFriendName: String) : this() {
        id = roomId
        this.messageList = messageList
        this.userFriendName = userFriendName
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(id)
        dest.writeString(userFriendName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<BoxChat> {
        override fun createFromParcel(parcel: Parcel): BoxChat {
            return BoxChat(parcel)
        }

        override fun newArray(size: Int): Array<BoxChat?> {
            return arrayOfNulls(size)
        }
    }
}
