package com.arafat.trackiekotlinapp.helper

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.activity.LoginActivity
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.google.android.material.snackbar.Snackbar

open class BaseActivity() : AppCompatActivity(){

    private val TAG = BaseActivity::class.java.simpleName
    private lateinit var mProgressDialog: ProgressDialog
    internal var context: Context = this
    open lateinit var sharedPreferences: SharedPreferences
    lateinit var sharedPrefManager: SharedPrefManager

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(AppConstant.USER_SHARED_PREF, Context.MODE_PRIVATE)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setIndeterminate(true)
        mProgressDialog.setMessage(resources.getString(R.string.loading))

        sharedPrefManager = SharedPrefManager(this)

    }

    fun showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing)
            mProgressDialog.show()
    }

    fun showProgressDialog(title: String, message: String) {
        if (mProgressDialog != null && !mProgressDialog.isShowing) {
            mProgressDialog.setTitle(title)
            mProgressDialog.setMessage(message)
            mProgressDialog.show()
        }
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing)
            mProgressDialog.dismiss()
    }

    fun showToastMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun logOut() {
        sharedPreferences.edit().clear().apply()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()

        sharedPrefManager.deleteSharedPrefManager()
    }

    fun showSnackBar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction(resources.getString(R.string.close)) { view1 ->

            }
            .setActionTextColor(resources.getColor(R.color.colorBlue))
            .show()
    }

    fun convert12hrsWithMeridienFormat(str: String): String {
        Log.d(TAG, " convert12hrsWithMeridienFormat called $str")
        // Get Hours
        val h1 = str[0].toInt() - '0'.toInt()
        val h2 = str[1].toInt() - '0'.toInt()


        var hh = h1 * 10 + h2
        Log.d(TAG, " converted time hour: $h1 - $h2 - $hh")

        val m1 = str[3].toString()
        val m2 = str[4].toString()
        val minute = m1 + m2
        Log.d(TAG, " converted time minute: $m1 - $m2 = $minute")


        // Finding out the Meridien of time
        // ie. AM or PM
        val Meridien: String
        if (hh < 12) {
            Meridien = "AM"
        } else
            Meridien = "PM"

        hh %= 12

        // Handle 00 and 12 case separately
        if (hh == 0) {
            print("12")

            // Printing minutes and seconds
            for (i in 2..7) {
                print(str[i])
            }
        } else {
            print(hh)
            // Printing minutes and seconds
            for (i in 2..7) {
                print(str[i])
            }
        }

        // After time is printed
        // cout Meridien
        println(" $Meridien")


        return "$hh:$minute $Meridien"
    }


    fun convert12hrsFormat(str: String): String {
        Log.d(TAG, " convert12hrsFormat called $str")
        // Get Hours
        val h1 = str[0].toInt() - '0'.toInt()
        val h2 = str[1].toInt() - '0'.toInt()


        val hh = h1 * 10 + h2
        Log.d(TAG, " converted time hour: $h1 - $h2 - $hh")

        val m1 = str[3].toString()
        val m2 = str[4].toString()
        val minute = m1 + m2
        Log.d(TAG, " converted time minute: $m1 - $m2 = $minute")

        return "$hh:$minute"
    }

}