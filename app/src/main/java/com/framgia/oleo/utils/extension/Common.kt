package com.framgia.oleo.utils.extension

import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageButton

private const val VALUE_MAX_TIME_CLICK = 1000
private var lastClickTime: Long = 0

fun isCheckMultiClick(): Boolean {
    if (SystemClock.elapsedRealtime() - lastClickTime < VALUE_MAX_TIME_CLICK)
        return false
    lastClickTime = SystemClock.elapsedRealtime()
    return true
}

fun isCheckClickableButtonClick(button: Button) {
    button.isClickable = false
    Handler().postDelayed({
        button.isClickable = true
    }, VALUE_MAX_TIME_CLICK.toLong())
}

fun isCheckClickableImageButtonClick(imageButton: ImageButton) {
    imageButton.isClickable = false
    Handler().postDelayed({
        imageButton.isClickable = true
    }, VALUE_MAX_TIME_CLICK.toLong())
}
