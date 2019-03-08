package com.framgia.oleo.data.source.model

class FollowRequest() {
    var id: String? = null
    var status: String? = null
    var time: Long? = null

    constructor(id: String?, status: String, time: Long?) : this() {
        this.id = id
        this.status = status
        this.time = time
    }
}
