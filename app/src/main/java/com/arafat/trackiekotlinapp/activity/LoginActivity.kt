package com.arafat.trackiekotlinapp.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.BuildConfig
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.BaseActivity

import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest as JsonObjectRequest

class LoginActivity : BaseActivity() {

    private val TAG = LoginActivity::class.java.simpleName

    private lateinit var llPhone : LinearLayout
    private lateinit var llPin : LinearLayout

    private lateinit var etPhone : EditText
    private lateinit var etPin : EditText

    private lateinit var btnLoginNext : Button
    private lateinit var btnLoginSubmit : Button

    var pinId : String = ""
    override lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()

        btnLoginNext.setOnClickListener {

            showProgressDialog("Loading","Wating for Pin...")
            getLoginInfo()
        }

        btnLoginSubmit.setOnClickListener {

            showProgressDialog("Loading","Submitting Pin...")
            getPin()
        }

    }

    private fun initView() {

        sharedPreferences = getSharedPreferences(AppConstant.USER_SHARED_PREF, Context.MODE_PRIVATE)

        llPhone = findViewById(R.id.llPhone) as LinearLayout
        llPin = findViewById(R.id.llPin) as LinearLayout

        etPhone = findViewById(R.id.etPhone)
        etPin = findViewById(R.id.etPin)

        btnLoginNext = findViewById(R.id.btnLoginNext)
        btnLoginSubmit = findViewById(R.id.btnLoginSubmit)

    }


    private fun getLoginInfo() {

        val phone :String = etPhone.text.toString().trim()

        if(!TextUtils.isEmpty(phone)){
            sendPinToPhone(phone)
        }
    }

    private fun sendPinToPhone(phone: String) {

        var jsonBody: JSONObject

        val obj : String =
            "{\"applicationId\":" + resources.getString(R.string.info_bip_app_id) + ",\"messageId\":" + resources.getString(
                R.string.info_bip_message_id_en
            ) + ",\"from\":MUV,\"to\":" + phone + "}"
        jsonBody = JSONObject(obj)

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, ApiConstants().INFO_BIP_PIN, jsonBody,
            Response.Listener {response ->

                hideProgressDialog()
                Log.d("res::", response.toString())

                if(response.has("pinId")){
                    pinId = response.getString("pinId")
                }

                llPhone.visibility = View.GONE
                llPin.visibility = View.VISIBLE

            }, Response.ErrorListener {


            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put("Authorization","App "+getResources().getString(R.string.info_bip_apiKey))
                headers.put("Content-Type", "application/json");
                return headers
            }
        }

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        var queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun getPin() {

        val pin = etPin.text.toString()

        if (!TextUtils.isEmpty(pin)) {
            submitPin(pin)
        }
    }

    private fun submitPin(pin: String) {

        var jsonBody: JSONObject
        val jObj = "{\"pin\":$pin}"
        Log.d("s::", jObj)
        jsonBody = JSONObject(jObj)

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, ApiConstants().INFO_BIP_PIN+"/"+pinId+"/verify", jsonBody,
            Response.Listener {response ->

                hideProgressDialog()
                Log.d("resVerify::", response.toString())

                if(response.has("verified")){

                    if(response.getString("verified") == "true"){

                        var msisdn : String = response.getString("msisdn")
                        Log.d("msi:", msisdn)
                        loginCheck(msisdn)
                    }
                }


            }, Response.ErrorListener {


            }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers.put("Authorization","App "+getResources().getString(R.string.info_bip_apiKey))
                    headers.put("Content-Type", "application/json");
                    return headers
                }
        }

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        var queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun loginCheck(msisdn: String) {

        val params = HashMap<String,String>()
        params["phone"] = msisdn;
        val jsonObject = JSONObject(params as Map<*, *>)

        val request = JsonObjectRequest(Request.Method.POST,ApiConstants().APP_LOGIN,jsonObject,
            Response.Listener { response ->

                Log.d("res::",response.toString())

                if(response.has("errors")){

                    val jError:JSONObject = JSONObject(response.getString(AppConstant.ERRORS))

                    if (jError.getString("registered") == "false") {

                        Log.d(TAG, "Un Registerd")

                        val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
                        intent.putExtra("-phone", msisdn)
                        startActivity(intent)

                    }
                }else{

                    val code = response.getInt(AppConstant.CODE)
                    if(code == 200){

                        val data = response.getString(AppConstant.DATA)
                        val jData = JSONObject(data)
                        val Token = jData.getString(AppConstant.TOKEN)

                        val spEdit = sharedPreferences.edit()
                        spEdit.putString(AppConstant.USER_SHARED_PREF_PHONE, msisdn)
                        spEdit.putString(AppConstant.USER_SHARED_PREF_ACCESS_TOKEN, Token)
                        spEdit.apply()
                        spEdit.commit()

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
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
    }

    override fun onBackPressed() {

        if (llPin.visibility == View.VISIBLE) {

            llPin.visibility = View.GONE
            llPhone.visibility = View.VISIBLE

        } else {
            closeApp()
        }
    }

    private fun closeApp(){

        AlertDialog.Builder(this@LoginActivity)
            .setCancelable(false)
            .setTitle("Are you sure, want to exit the app?")
            .setPositiveButton(android.R.string.yes)
                { dialog, which ->
                    finish()
                    System.exit(0)
                }
            .setNegativeButton(android.R.string.no)
                { dialog, which ->

                }
            .setIcon(R.mipmap.ic_launcher)
            .show()
        }
}
