package com.framgia.oleo.data.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.room.Room
import com.framgia.oleo.data.source.local.dao.UserDatabase
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.utils.Constant
import com.framgia.oleo.utils.extension.isCheckedInternetConnected
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("MissingPermission")
class LocationService : Service() {

    private var idUser = ""
    private var isInsert = false
    private var isGPSEnable: Boolean = false
    private var isNetWorkEnable: Boolean = false
    private var broadcastResetService = RestartServiceLocation()
    private var geoCoder: Geocoder? = null
    private var fireBaseDatabase = FirebaseDatabase.getInstance()
    private var userDatabase: UserDatabase? = null
    private var locationListeners = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )

    override fun onBind(arg: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return Service.START_STICKY
    }

    override fun onCreate() {
        userDatabase = Room.databaseBuilder(
            applicationContext, UserDatabase::class.java, UserDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()

        idUser = userDatabase!!.userDAO().getUser.id
        registerReceiver(broadcastResetService, IntentFilter(Constant.ACTION_RESTART_SERVICE))
        initializeLocationManager()
        isGPSEnable = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!!
        isNetWorkEnable = locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!!

        if (isCheckLocationPermission()) {
            if (isNetWorkEnable) return locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                locationListeners[1]
            )
            if (isGPSEnable) return locationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                locationListeners[0]
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isCheckLogout && locationManager != null) {
            unregisterReceiver(broadcastResetService)
            for (i in locationListeners.indices) {
                locationManager!!.removeUpdates(locationListeners[i])
            }
            isCheckLogout = false
        } else sendBroadcast(Intent(Constant.ACTION_RESTART_SERVICE))
    }

    private fun initializeLocationManager() {
        if (locationManager == null) {
            locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    inner class LocationListener(provider: String) : android.location.LocationListener {

        var latitude = 0.0
        var longitude = 0.0
        private var lastLocation = Location(provider)

        override fun onLocationChanged(location: Location) {
            if (isCheckedInternetConnected(applicationContext)) {
                if (lastLocation.distanceTo(location) - location.accuracy > 0) {
                    lastLocation.set(location)
                    if (latitude != lastLocation.latitude || longitude != lastLocation.longitude) {
                        latitude = lastLocation.latitude
                        longitude = lastLocation.longitude
                        try {
                            val formatter = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
                            val calendar = Calendar.getInstance()
                            geoCoder = Geocoder(applicationContext, Locale.getDefault())
                            var address: MutableList<Address>? = null
                            address?.clear()
                            address = geoCoder?.getFromLocation(latitude, longitude, 1)!!

                            pushDataLocationUser(
                                Place(
                                    "",
                                    latitude.toString(),
                                    longitude.toString(),
                                    address[0].getAddressLine(0).toString(),
                                    formatter.format(calendar.time)
                                )
                            )
                        } catch (ex: IOException) {
                            ex.printStackTrace()
                        }
                    }
                }
            }
        }

        override fun onProviderDisabled(provider: String) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    }

    fun pushDataLocationUser(place: Place) {
        place.id = fireBaseDatabase.getReference(Constant.PATH_STRING_USER).push().key.toString()
        fireBaseDatabase.getReference(Constant.PATH_STRING_LOCATION).child(idUser)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value == null) isInsert = true
                    else if (snapshot.exists()) {
                        val data = snapshot.children.last().getValue(Place::class.java)
                        isInsert =
                            !(data!!.latitude == place.latitude && data.longitude == place.longitude)
                    }
                    if (isInsert) {
                        fireBaseDatabase.getReference(Constant.PATH_STRING_LOCATION).child(idUser)
                            .child(place.id.toString()).setValue(place)
                    }
                }
            })
    }

    private fun isCheckLocationPermission(): Boolean {
        val permission = "android.permission.ACCESS_FINE_LOCATION"
        val res = this.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        var isCheckLogout = false
        var locationManager: LocationManager? = null
        private const val LOCATION_INTERVAL = 10000 * 6 * 5
        private const val LOCATION_DISTANCE = 0f
        private const val DATE_TIME_FORMAT = "HH:mm-dd/MM/yyyy"
    }
}
