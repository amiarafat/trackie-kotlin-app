package com.arafat.trackiekotlinapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.fragment.HomeFragment
import com.arafat.trackiekotlinapp.fragment.MenuFragment
import com.arafat.trackiekotlinapp.fragment.NotificationFragment
import com.arafat.trackiekotlinapp.fragment.TeamOwnerHomeFragment
import com.arafat.trackiekotlinapp.helper.BaseActivity
import com.arafat.trackiekotlinapp.helper.SharedPrefManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import org.w3c.dom.Text
import java.util.HashMap

class HomeActivity : BaseActivity() {

    private val TAG = HomeActivity::class.java.simpleName

    lateinit var tvToolbarTitle : TextView
    lateinit var toolbarPerson : ImageView
    var DeviceFCMToken = ""
    private lateinit var spDeviceTokPref: SharedPreferences

    override lateinit var sharedPreferences: SharedPreferences
    private var accessToken = ""
    private var userRole = ""
    lateinit var sfManager: SharedPrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initView()

        startGPStartDialog()

        getUserInfo()

        getUserStat()

        deviceInfoUpdate()

    }

    private fun initView() {
        sfManager = SharedPrefManager(this)
        sharedPreferences = getSharedPreferences(AppConstant().USER_SHARED_PREF, Context.MODE_PRIVATE)

        val myToolbar : Toolbar = findViewById(R.id.toolbarHome)
        setSupportActionBar(myToolbar)

        tvToolbarTitle = findViewById(R.id.tvToolbarTitle)
        toolbarPerson = findViewById(R.id.tbPerson)

        //TODO for firebase implementation
        //spDeviceTokPref = getSharedPreferences(FireBaseConfig.SHARED_PREF, Context.MODE_PRIVATE)

        val navView : BottomNavigationView = findViewById(R.id.navView)
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun getUserInfo() {
        accessToken = sharedPreferences.getString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN, "").toString()
        //TODO for firebase implementation
        //DeviceFCMToken = spDeviceTokPref.getString(FireBaseConfig.FCM_TOKEN, "")
        userRole = sharedPrefManager.getUserRole()
        Log.d(TAG, "getUserInfo accessToken :: " + accessToken + "--" + DeviceFCMToken + "userRole --" + userRole)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (userRole == AppConstant().USER_ROLE_TEAM_OWNER) {
                        val homeFragmentTeamOwner = TeamOwnerHomeFragment()
                        openFragment(homeFragmentTeamOwner)
                        tvToolbarTitle.setText(R.string.title_home)
                    } else {
                        val homeFragment = HomeFragment()
                        openFragment(homeFragment)
                        tvToolbarTitle.setText(R.string.title_home)
                    }
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_notifications -> {
                    val notificationFragment = NotificationFragment()
                    openFragment(notificationFragment)
                    tvToolbarTitle.setText(R.string.title_notifications)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_menu -> {
                    val menuFragment = MenuFragment()
                    openFragment(menuFragment)
                    tvToolbarTitle.setText(R.string.title_menu)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun startGPStartDialog() {
        val builder = LocationSettingsRequest.Builder()
            //                .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
            .addLocationRequest(LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        //        builder.setNeedBle(false);
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())

        task.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse> {
            override fun onComplete(task: Task<LocationSettingsResponse>) {
                try {
                    val response = task.getResult(ApiException::class.java)
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (exception: ApiException) {
                    when (exception.getStatusCode()) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                    this@HomeActivity,
                                    AppConstant().REQUEST_GPS_CHECK_SETTINGS
                                )
                            } catch (e: IntentSender.SendIntentException) {
                                // Ignore the error.
                            } catch (e: ClassCastException) {
                                // Ignore, should be an impossible error.
                            }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        }
                    }// Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                }

            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("onActivityResult()", Integer.toString(resultCode))

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        when (requestCode) {
            AppConstant().REQUEST_GPS_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                }// All required changes were successfully made
                //Toast.makeText(HomeActivity.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                    //Toast.makeText(HomeActivity.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                    startGPStartDialog()
                }
                else -> {
                }
            }
        }
    }


    fun getUserStat(){

        val request = object : StringRequest(Method.POST,ApiConstants().APP_STATE, Response.Listener {
            response ->

            val jObj = JSONObject(response)

            if (jObj.getString("success") == "true") {

                val Data = jObj.getString("data")
                val jData = JSONObject(Data)
                val typeSelected = jData.getString("type_selected")
                val onlineState = jData.getString("online")
                val trackId = jData.getInt("track_id")

                sfManager.setUserOnlineStatus(onlineState)
                sfManager.setUserRole(typeSelected)
            }

        }, Response.ErrorListener {
            error -> error.printStackTrace()

        }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("authorization",AppConstant().BEARER+accessToken)
                params.put("Content-Type","application/x-www-form-urlencoded")
                return params
            }
        }

        val queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun deviceInfoUpdate(){

        val request = object : StringRequest(Method.POST,ApiConstants().APP_DEVICE_INFO, Response.Listener {
                response ->



        }, Response.ErrorListener {
                error -> error.printStackTrace()

        }){
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params.put("authorization",AppConstant().BEARER+accessToken)
                params.put("Content-Type","application/x-www-form-urlencoded")
                return params
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()

                Log.d("os::", android.os.Build.VERSION.RELEASE)

                params.put("device_token",DeviceFCMToken)
                params.put("model", Build.MODEL)
                params.put("brand", Build.BRAND)
                params.put("os",Build.VERSION.RELEASE)
                params.put("uuid",getDeviceUID())
                return params
            }
        }

        val queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)

    }

    private fun getDeviceUID(): String {

        val android_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("uid::", android_id)
        return android_id
    }
}
