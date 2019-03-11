package com.framgia.oleo.data.source.model

class Friend() {
    var id: String? = null
    var time: Long? = null
    var user: User? = null

    constructor(id: String, time: Long, user: User) : this() {
        this.id = id
        this.time = time
        this.user = user
    }
}
