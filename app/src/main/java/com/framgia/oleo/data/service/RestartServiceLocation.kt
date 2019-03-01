package com.framgia.oleo.data.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.framgia.oleo.utils.Constant.ACTION_RESTART_SERVICE

class RestartServiceLocation : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION_RESTART_SERVICE)
            context?.startService(Intent(context.applicationContext, LocationService::class.java)
        )
    }
}
