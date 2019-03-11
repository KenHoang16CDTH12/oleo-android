package com.framgia.oleo.data.source.model

class Followed() {
    var id: String? = null
    var time: Long? = null

    constructor(id: String, time: Long) : this() {
        this.id = id
        this.time = time
    }
}
