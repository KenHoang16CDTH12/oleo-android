package com.framgia.oleo.utils.extension

import android.content.Context
import android.net.ConnectivityManager
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageButton

private const val VALUE_MAX_TIME_CLICK = 2000
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

fun isCheckedInternetConnected(context: Context?): Boolean {
    val connectManager =
        context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectManager.activeNetworkInfo != null
}
