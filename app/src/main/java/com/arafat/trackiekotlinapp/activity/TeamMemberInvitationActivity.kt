package com.arafat.trackiekotlinapp.activity

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.BaseActivity
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class TeamMemberInvitationActivity : BaseActivity() {

    private val TAG = TeamMemberInvitationActivity::class.java.simpleName

    lateinit var sharedPref: SharedPreferences
    lateinit var llInviteStatus: LinearLayout
    lateinit var llUserInvitation: LinearLayout
    lateinit var rootLayoutTeamMemberInvitation: RelativeLayout
    var userAccessToken = ""
    lateinit var tvUserName: TextView
    lateinit var tvInvitationMessage: TextView
    lateinit var tvLabelSkip: TextView
    lateinit var btnAcceptInvitation: Button
    var invitationId = 0
    lateinit var progressBarTeamMemberInvitation: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_member_invitation)

        initView()
        getUserInfo()

        if (isNetworkAvailable(this)) {
            getUserInvitation()
        } else {
            showSnackBar(
                rootLayoutTeamMemberInvitation,
                resources.getString(R.string.internet_missing)
            )
        }

        tvLabelSkip.setOnClickListener {
            gotoHome();
        }

        btnAcceptInvitation.setOnClickListener {

            if(isNetworkAvailable(this)){
                acceptInvitation(invitationId)
            }else{
                showSnackBar(
                    rootLayoutTeamMemberInvitation,
                    resources.getString(R.string.internet_missing)
                )
            }
        }
    }

    private fun acceptInvitation(invitationId: Int) {

        Log.d(TAG, "acceptInvitation :: $invitationId")

        val request = object : StringRequest(Method.POST,ApiConstants().API_INVITE_ACCEPT, Response.Listener {
            response ->

            Log.d(TAG, "acceptInvitation :: $response")

            val jsonObject = JSONObject(response)
            val code = jsonObject.getInt(AppConstant().CODE)

            when(code){
                200 -> {
                    Log.d(TAG, "success")
                    gotoHome()
                }
                401 -> {
                    logOut()
                }
                406 ->{

                    val errors = jsonObject.getString("errors")
                    val jsonObjectErrors = JSONObject(errors)
                    val showMessage = jsonObjectErrors.getString("show_message")
                    showSnackBar(rootLayoutTeamMemberInvitation, showMessage)
                }
            }

        },Response.ErrorListener {
            error ->  error.printStackTrace()
        }){
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AppConstant().AUTHORIZATION,AppConstant().BEARER + userAccessToken)
                return headers
            }

            public override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["invitation_id"] = invitationId.toString()
                return params
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

    private fun gotoHome() {

        val intent = Intent(this@TeamMemberInvitationActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun initView() {
        sharedPreferences = getSharedPreferences(AppConstant().USER_SHARED_PREF, MODE_PRIVATE)
        rootLayoutTeamMemberInvitation = findViewById(R.id.rootLayoutTeamMemberInvitation)
        llInviteStatus = findViewById(R.id.llInviteStatus)
        llUserInvitation = findViewById(R.id.llUserInvitation)
        tvUserName = findViewById(R.id.tvUserName)
        tvInvitationMessage = findViewById(R.id.tvInvitationMessage)
        tvLabelSkip = findViewById(R.id.tvLabelSkip)
        btnAcceptInvitation = findViewById(R.id.btnAcceptInvitation)
        progressBarTeamMemberInvitation = findViewById(R.id.progressBarTeamMemberInvitation)
    }

    private fun getUserInfo() {

        userAccessToken =
            sharedPreferences.getString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN, "").toString()
        Log.d(TAG, " userAccessToken :: $userAccessToken")
    }


    private fun getUserInvitation() {

        val request = object :
            StringRequest(Request.Method.GET, ApiConstants().API_INVITE_REQUESTS, Response.Listener
            { response ->

                progressBarTeamMemberInvitation.visibility = View.GONE
                Log.d(TAG, " getRequestInvitationUsers response $response")

                val jsonObject = JSONObject(response)
                val code = jsonObject.getInt(AppConstant().CODE)

                when (code) {
                    200 -> {
                        //show success message
                        Log.d(TAG, "success")
                        val data = jsonObject.getString(AppConstant().DATA)
                        val jsonObjectData = JSONObject(data)

                        val jsonArrayUsers =
                            jsonObjectData.getJSONArray("invitation_requests")
                        Log.d(TAG, "jsonArrayUsers length --" + jsonArrayUsers.length())
                        if (jsonArrayUsers.length() == 0) {
                            llInviteStatus.visibility = View.VISIBLE
                        } else {
                            llUserInvitation.visibility = View.VISIBLE
                            parseArray(jsonArrayUsers)
                        }
                    }
                    401 -> {
                        logOut()
                    }
                }


            }, Response.ErrorListener { error ->
                Log.d(TAG, " volley error $error")
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AppConstant().AUTHORIZATION, AppConstant().BEARER + userAccessToken)
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

    private fun parseArray(jsonArrayUsers: JSONArray) {
        val i = 0
        do {
            try {
                val jsonObjectUser = jsonArrayUsers.get(i) as JSONObject

                val firstName = jsonObjectUser.getString("first_name")
                val lastName = jsonObjectUser.getString("last_name")
                invitationId = jsonObjectUser.getInt("invitation_id")

                Log.d(
                    TAG, "jsonArrayUsers :: 1 :: " +
                            " firstName--" + firstName + " " +
                            " lastName--" + lastName + " " +
                            " invitationId--" + invitationId
                )
                val userName = "$firstName $lastName"
                tvUserName.text = userName
                tvInvitationMessage.text = "If you accept " + userName + " invitation,\n" +
                        userName + " will track your time and tasks and also will assign tasks"
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } while (i == 1)
    }
}
