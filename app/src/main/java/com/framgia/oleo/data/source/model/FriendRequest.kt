package com.framgia.oleo.data.source.model

class FriendRequest() {
    var id: String? = null
    var message: String? = null
    var status = 0
    var time: Long? = null

    constructor(id: String, message: String, status: Int, time: Long) : this() {
        this.id = id
        this.message = message
        this.status = status
        this.time = time
    }
}
