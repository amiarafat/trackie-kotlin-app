package com.arafat.trackiekotlinapp.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.adapters.MultipleContactInviteAdapter
import com.arafat.trackiekotlinapp.adapters.TeamOwnerInvitationContactAdapter
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.BaseActivity
import com.arafat.trackiekotlinapp.model.Contact
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.util.ArrayList

class TeamOwnerInvitationActivity : BaseActivity() {

    private val TAG = TeamOwnerInvitationActivity::class.java.simpleName

    //Layouts
    private lateinit var llInvitePeopleStatus  :LinearLayout
    private lateinit var llContactPermissionStatus  :LinearLayout
    private lateinit var rlInvitePeopleStatus  :RelativeLayout
    private lateinit var rootLayoutTeamOwnerInvitation  :CoordinatorLayout

    //View Items
    private lateinit var btnInvitePeopleLayout : Button
    private lateinit var btnAllowContactPermission : Button
    private lateinit var btnAddNewContact : Button

    private lateinit var tvLabelSkip : TextView
    private lateinit var etPhoneNumber : EditText
    private lateinit var recyclerViewContactList : RecyclerView

    private var isEdittext = false
    private lateinit var adapter: TeamOwnerInvitationContactAdapter
    private lateinit var contactArrayList: ArrayList<Contact>
    private var phoneNumber = ""

    private var accessToken: String? = ""
    override lateinit var sharedPreferences: SharedPreferences


    private lateinit var itemMultiInvite: ArrayList<Contact>
    private lateinit var recyclerViewContactListTemporary: RecyclerView
    private lateinit var adapter1: MultipleContactInviteAdapter
    private lateinit var btnInviteAll: Button
    private var count = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_owner_invitation)

        initView()
        getUserInfo()

        btnInvitePeopleLayout.setOnClickListener {

            llInvitePeopleStatus.visibility = View.GONE
            rlInvitePeopleStatus.visibility = View.VISIBLE
        }

        btnAllowContactPermission.setOnClickListener {

            Log.d(TAG, "btnAllowContactPermission called")
            requestContactPermission()
        }

        tvLabelSkip.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnAddNewContact.setOnClickListener {

            isEdittext = true
            inviteUser(AppConstant().BD_CODE + etPhoneNumber.text.toString().trim())
        }

        btnInviteAll.setOnClickListener {

            if (itemMultiInvite.size > 0) {
                isEdittext = false
                inviteUser(itemMultiInvite[0].getNumber())
            } else {

            }
        }
    }

    private fun initView() {

        sharedPreferences = getSharedPreferences(AppConstant().USER_SHARED_PREF, MODE_PRIVATE)

        rootLayoutTeamOwnerInvitation = findViewById(R.id.rootLayoutTeamOwnerInvitation)
        llInvitePeopleStatus = findViewById(R.id.llInvitePeopleStatus)
        rlInvitePeopleStatus = findViewById(R.id.rlInvitePeopleStatus)
        llContactPermissionStatus = findViewById(R.id.llContactPermissionStatus)
        btnInvitePeopleLayout = findViewById(R.id.btnInvitePeopleLayout)
        btnAllowContactPermission = findViewById(R.id.btnAllowContactPermission)
        btnAddNewContact = findViewById(R.id.btnAddNewContact)
        tvLabelSkip = findViewById(R.id.tvLabelSkip)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        btnInviteAll = findViewById(R.id.btnInviteAll)

        recyclerViewContactList = findViewById(R.id.recyclerViewContactList)
        val mLayoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerViewContactList.layoutManager = mLayoutManager
        contactArrayList = ArrayList()
        adapter = TeamOwnerInvitationContactAdapter(this,contactArrayList,onClickListener = object : TeamOwnerInvitationContactAdapter.MyAdapterListener{
            override fun btnRequestInviteOnClick(position: Int) {

                contactArrayList[position].setStatus(2)
                itemMultiInvite.add(contactArrayList[position])
                adapter1.notifyDataSetChanged()
            }

        })
        recyclerViewContactList.adapter = adapter

        itemMultiInvite = ArrayList()
        recyclerViewContactListTemporary = findViewById(R.id.recyclerViewContactListTemporary)
        val lManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerViewContactListTemporary.layoutManager = lManager
        adapter1 = MultipleContactInviteAdapter(this,itemMultiInvite,onClickListener = object : MultipleContactInviteAdapter.MyAdapterListener{
            override fun btnRequestInviteOnClick(position: Int) {

                itemMultiInvite.removeAt(position)
                adapter1.notifyDataSetChanged()
            }
        })
        recyclerViewContactListTemporary.adapter = adapter1

    }

    private fun getUserInfo() {
        accessToken = sharedPreferences.getString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN, "")
        Log.d(TAG, "accessToken :: $accessToken")
    }

    private fun inviteUser(number: String) {

        Log.d(TAG, " phoneNumber :: $number")

        val params = HashMap<String,String>()
        params.put("phone",number)
        val jsonBody = JSONObject(params as Map<*, *>)

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, ApiConstants().API_INVITE_USER, jsonBody,
            Response.Listener {response ->

                Log.d(TAG, " inviteUser response ${response.toString()}")

                val code = response.getInt(AppConstant().CODE)

                when (code) {
                    200 -> {
                        //show success message
                        Log.d(TAG, "success")
                        showSnackBar(rootLayoutTeamOwnerInvitation, "Invitation Sent")

                        if (!isEdittext) {
                            //itemMultiInvite.remove(count);
                            itemMultiInvite[count].setStatus(1)
                            adapter1.notifyDataSetChanged()
                            adapter1.showProgresss(false, count)
                        }
                    }
                    404 ->{

                    }
                    406 -> {
                        // show error message
                        val errors = response.getString(AppConstant().ERRORS)
                        val jsonObjectErrors = JSONObject(errors)
                        val showMessage = jsonObjectErrors.getString(AppConstant().SHOW_MESSAGE)
                        Log.d(TAG, "showMessage :: $showMessage")
                        showSnackBar(rootLayoutTeamOwnerInvitation, showMessage)

                        if (!isEdittext) {
                            itemMultiInvite[count].setStatus(0)
                            adapter1.showProgresss(false, count)
                        }
                    }
                    401 -> {
                        logOut()
                    }
                }

            }, Response.ErrorListener {


            }) {

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers.put(AppConstant().AUTHORIZATION , AppConstant().BEARER + accessToken)
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

    protected override fun onResume() {
        super.onResume()

        if (!checkContactPermission()) {
            llContactPermissionStatus.visibility = View.VISIBLE
        } else {
            llContactPermissionStatus.visibility = View.GONE
            if (isNetworkAvailable(applicationContext)) {
                contactArrayList.clear()
                getContacts()
            } else {
                showSnackBar(rootLayoutTeamOwnerInvitation, "internet missing")
            }
        }
    }

    private fun requestContactPermission() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_CONTACTS
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(R.string.contact_permission_rationale,android.R.string.ok, View.OnClickListener {
                // Request permission
                startContactPermissionRequest()
            })
            /*showSnackbar(R.string.contact_permission_rationale, android.R.string.ok,
                { view ->
                    // Request permission
                    startContactPermissionRequest()
                })*/

        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startContactPermissionRequest()
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkContactPermission(): Boolean {
        val permissionState =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        Log.d(TAG, " permissionState :: $permissionState")
        return permissionState == PackageManager.PERMISSION_GRANTED
    }


    private fun startContactPermissionRequest() {
        Log.d(TAG, "startContactPermissionRequest called")
        ActivityCompat.requestPermissions(
            this@TeamOwnerInvitationActivity,
            arrayOf(Manifest.permission.READ_CONTACTS),
            AppConstant().CONTACT_PERMISSIONS_REQUEST_CODE
        )
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == AppConstant().CONTACT_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                llContactPermissionStatus.visibility = View.GONE
                if (isNetworkAvailable(applicationContext)) {
                    contactArrayList.clear()
                    getContacts()
                } else {
                    // show internet missing
                }
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.contact_permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    private fun getContacts() {
        Log.d(TAG, " getContacts called")
        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor!!.count > 0) {
            while (cursor.moveToNext()) {

                val hasPhoneNumber =
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
                if (hasPhoneNumber > 0) {
                    val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    val contact = Contact()
                    contact.setName(name)

                    val phoneCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    if (phoneCursor!!.moveToNext()) {
                        phoneNumber =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contact.setNumber(phoneNumber)
                    }

                    phoneCursor.close()

                    contactArrayList.add(contact)
                }
            }

            adapter.notifyDataSetChanged()
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param mainTextStringId The id for the string resource for the Snackbar text.
     * @param actionStringId   The text of the action item.
     * @param listener         The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
        mainTextStringId: Int,
        actionStringId: Int,
        listener: View.OnClickListener
    ) {
        Snackbar.make(
            findViewById(android.R.id.content),
            getString(mainTextStringId),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(actionStringId), listener).show()
    }

}
