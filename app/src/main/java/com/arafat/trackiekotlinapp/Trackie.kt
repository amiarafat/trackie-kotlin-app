package com.arafat.trackiekotlinapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.text.TextUtils
import androidx.multidex.MultiDexApplication
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Trackie() : MultiDexApplication(){

    private val TAG = Trackie::class.java.simpleName
    private val CHANNEL_ID = "Trackie_Channel_Id"
    private val CHANNEL_NAME = "Trackie Channel"
    private lateinit var  mRequestQueue : RequestQueue
    private lateinit var mInstance : Trackie

    override fun onCreate() {
        super.onCreate()
        mInstance = this

        createNotiChannel()
    }

    private fun createNotiChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager : NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    @Synchronized
    fun getInstance(): Trackie {
        return mInstance
    }

    fun getRequestQueue():RequestQueue{

        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }

        return mRequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue().add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue().add(req)
    }
}