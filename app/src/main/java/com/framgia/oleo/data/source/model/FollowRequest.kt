package com.framgia.oleo.data.source.model

class FollowRequest() {
    var id: String? = null
    var status: String? = null
    var time: Long? = null
    var name: String? = null
    var image: String? = null
    var phoneNumber: String? = null

    constructor(
        id: String?,
        status: String?,
        time: Long?,
        name: String?,
        image: String?,
        phoneNumber: String?
    ) : this() {
        this.id = id
        this.status = status
        this.time = time
        this.name = name
        this.image = image
        this.phoneNumber = phoneNumber
    }
}
