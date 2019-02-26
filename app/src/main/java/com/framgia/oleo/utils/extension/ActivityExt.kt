package com.framgia.oleo.utils.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.framgia.oleo.utils.AnimateType

fun AppCompatActivity.replaceFragmentInActivity(@IdRes containerId: Int, fragment: Fragment,
                                                addToBackStack: Boolean = false, tag: String = fragment::class.java.simpleName,
                                                animateType: AnimateType = AnimateType.FADE) {
    supportFragmentManager.transact({
        if (addToBackStack) {
            addToBackStack(tag)
        }
        replace(containerId, fragment, tag)
    }, animateType = animateType)
}

fun AppCompatActivity.addFragmentToActivity(@IdRes containerId: Int, fragment: Fragment,
                                            addToBackStack: Boolean = true, tag: String = fragment::class.java.simpleName,
                                            animateType: AnimateType = AnimateType.FADE) {
    supportFragmentManager.transact({
        if (addToBackStack) {
            addToBackStack(tag)
        }
        add(containerId, fragment, tag)
    }, animateType = animateType)
}

fun AppCompatActivity.goBackFragment(): Boolean {
    val isShowPreviousPage = supportFragmentManager.backStackEntryCount > 0
    if (isShowPreviousPage) {
        supportFragmentManager.popBackStackImmediate()
    }
    return isShowPreviousPage
}

fun AppCompatActivity.startActivity(@NonNull intent: Intent,
                                    flags: Int? = null) {
    flags.notNull {
        intent.flags = it
    }
    startActivity(intent)
}

fun AppCompatActivity.startActivityAtRoot(context: Context,
                                          @NonNull clazz: Class<out Activity>, args: Bundle? = null) {
    val intent = Intent(context, clazz)
    args.notNull {
        intent.putExtras(it)
    }
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}


fun AppCompatActivity.startActivityForResult(@NonNull intent: Intent,
                                             requestCode: Int, flags: Int? = null) {
    flags.notNull {
        intent.flags = it
    }
    startActivityForResult(intent, requestCode)
}

fun AppCompatActivity.isExistFragment(fragment: Fragment): Boolean {
    return supportFragmentManager.findFragmentByTag(fragment::class.java.simpleName) != null
}

fun AppCompatActivity.switchFragment(@IdRes containerId: Int, currentFragment: Fragment,
                                     newFragment: Fragment, addToBackStack: Boolean = true,
                                     tag: String = newFragment::class.java.simpleName,
                                     animateType: AnimateType = AnimateType.FADE) {
    supportFragmentManager.transact({
        if (isExistFragment(newFragment)) {
            hide(currentFragment).show(newFragment)
        } else {
            hide(currentFragment)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            replace(containerId, newFragment, tag)
        }
    }, animateType = animateType)
}

fun AppCompatActivity.clearAllFragment() {
    for (i in 0 until supportFragmentManager!!.backStackEntryCount) {
        supportFragmentManager!!.popBackStack()
    }
}

