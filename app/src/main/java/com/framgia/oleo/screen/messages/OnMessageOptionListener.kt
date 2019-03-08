package com.framgia.oleo.screen.messages

import com.framgia.oleo.data.source.model.User

interface OnMessageOptionListener {
    fun onFollowClick(userFriend: User)
}
