package com.arafat.trackiekotlinapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.BuildConfig
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.SharedPrefManager
import kotlinx.android.synthetic.main.activity_trackie_splash.*
import org.json.JSONObject

 class TrackieSplashActivity : AppCompatActivity() {

    private val TAG = TrackieSplashActivity::class.java.simpleName
    lateinit var UserPref: SharedPreferences
    private var handler: Handler? = null
    private var handler1: Handler? = null
    var UserAccessToken = ""
    var userRole = ""
    lateinit var sharedPrefManager : SharedPrefManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trackie_splash)
        initView()

        if (Build.VERSION.SDK_INT >= 21){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        //making notification bar transparent
        changeStatusBarColor()

        getUserPref()

        ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_in))


        handler = Handler()

        val r = Runnable{
            ic_logo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.splash_out))

            handler1 = Handler()
            val r2 = Runnable {
                ic_logo.visibility = View.GONE
            }
            handler1!!.postDelayed(r2,500)
        }
        handler!!.postDelayed(r,1000)

        appBoot()
    }

    private fun initView() {
        UserPref = getSharedPreferences(AppConstant().USER_SHARED_PREF, Context.MODE_PRIVATE)
        sharedPrefManager = SharedPrefManager(this)

    }

    private fun changeStatusBarColor() {

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.LOLLIPOP){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun getUserPref(){

        UserAccessToken = UserPref.getString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN,"").toString()
        userRole = sharedPrefManager.getUserRole()
        Log.d("$TAG UserAccessToken - ", "$UserAccessToken  userRole - $userRole")

    }

    private fun appBoot(){

        Log.d("res:", "response")

        val params = HashMap<String,String>()
        params["current_version"] = BuildConfig.VERSION_CODE.toString();
        val jsonObject = JSONObject(params as Map<*, *>)

        /*val request = JsonObjectRequest(Request.Method.POST,ApiConstants().APP_BOOT,jsonObject,
            Response.Listener { response ->
                // Process the json


            }, Response.ErrorListener{
                // Error in request

            })*/

        val request = JsonObjectRequest(Request.Method.POST,ApiConstants().APP_BOOT,jsonObject,
                        Response.Listener { response ->
                            Log.d(TAG + "appBoot res:", response.toString())

                            val code = response.getInt(AppConstant().CODE)

                            if(code == 200){

                                val jData:JSONObject = JSONObject(response.getString(AppConstant().DATA))
                                val jApp:JSONObject = JSONObject(jData.getString(AppConstant().APP))

                                val updateAvailable : Boolean = jApp.getBoolean(AppConstant().UPDATE_AVAILABLE)

                                if(updateAvailable){

                                    val link : String = jApp.getString(AppConstant().UPDATE_LINK)
                                    getUpdateAlert(link)
                                }else{
                                    loadingHandler()
                                }

                            }

                        }, Response.ErrorListener {

            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        var queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
        //Trackie().getInstance().addToRequestQueue(request)

    }

    private fun getUpdateAlert(link: String) {

        val builder  =  AlertDialog.Builder(this)
            .setTitle("New Update")
            .setCancelable(false)
            .setMessage("New update is available. Please update your app.")
            .setPositiveButton(android.R.string.yes){dialogInterface, i ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                finish()
            }
            .setIcon(R.mipmap.ic_launcher)

        builder.show()
    }

    private fun loadingHandler() {
        Log.d(TAG + "check:","Handler" )
        Log.d(TAG + "check:",UserAccessToken )

        if(TextUtils.isEmpty(UserAccessToken)){
            goToLogin()
        }else{
            goToHome()
        }
    }

    private fun goToLogin() {

        Log.d(TAG + "check:","Handler login" )

        if(sharedPrefManager.isFirstTimeLaunch()){
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun goToHome(){

        Log.d(TAG + "check:","Home" )
        Log.d(TAG + "check:",userRole )

        if(!userRole.equals("")){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            val intent = Intent(this, RoleManagementActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
