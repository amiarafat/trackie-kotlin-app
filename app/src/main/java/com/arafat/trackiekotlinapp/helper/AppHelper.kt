package com.arafat.trackiekotlinapp.helper

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.view.View
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.activity.LoginActivity
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class AppHelper{

    val LOG_IN_PREFERENCES = "LoginPrefs"
    val INFO_ACCESS_TOKEN = "info_access_token"

    //For Company
    val COMPANY_NAME = "com_name"
    val COMPANY_LOGO = "com_logo"
    val COMPANY_HELPLINE = "com_helpline"
    val COMPANY_VISITIN_RANGE = "com_visiting_range"

    //USER
    val USER_NAME = "user_name"
    val USER_PROFILE_PHOTO = "user_photo"
    val USER_PHONE = "user_number"
    val USER_IN_TIME = "user_in_time"
    val USER_OUT_TIME = "user_out_time"

    //APP STATE
    val USER_STATE = "user_state"
    val USER_TRACK_ID = "user_track_id"

    //USERLOC
    val USER_LATEST_LAT = "user_lat"
    val USER_LATEST_LON = "user_lon"


    //Local BroadCasting
    val PUSH_DIGNOSIS_NOTIFICATION = "PUSH_NOTIFICATION"


    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    fun getFileDataFromDrawable(context: Context, drawable: Drawable): ByteArray {
        val bitmap = (drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun showSnackBar(context: Context, view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(context.resources.getString(R.string.close)) { view1 ->

            }
            .setActionTextColor(context.resources.getColor(R.color.colorBlue))
            .show()
    }

    fun logOut(context: Context) {
        val sharedPreferences: SharedPreferences
        sharedPreferences =
            context.getSharedPreferences(AppConstant().USER_SHARED_PREF, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }
}