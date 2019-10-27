package com.arafat.trackiekotlinapp.activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.*
import com.android.volley.toolbox.Volley
import com.arafat.trackiekotlinapp.R
import com.arafat.trackiekotlinapp.Trackie
import com.arafat.trackiekotlinapp.constants.ApiConstants
import com.arafat.trackiekotlinapp.constants.AppConstant
import com.arafat.trackiekotlinapp.helper.*

import kotlinx.android.synthetic.main.activity_registration.*
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.HashMap

class RegistrationActivity : BaseActivity() {

    private val TAG = RegistrationActivity::class.java.simpleName

    lateinit var tvPhone : TextView
    lateinit var etFirstName : EditText
    lateinit var etLastName : EditText

    lateinit var ivUserImage : ImageView
    lateinit var btnRegister : Button
    lateinit var dialog: Dialog

    private val CAMERA_PERMISION_REQUEST_CODE : Int = 201
    private val STORAGE_PERMISION_REQUEST_CODE : Int = 202

    private val REQUEST_CAMERA : Int = 101
    private val REQUEST_STORAGE : Int = 102

    internal var MobileNUmber = ""

    override lateinit var sharedPreferences: SharedPreferences
    override lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        setSupportActionBar(toolbarRegistration)

        initView()

        ivUserImage.setOnClickListener {

            chooseImageType()
        }

        btnRegister.setOnClickListener {

            prepareRegistration()
        }

    }

    private fun initView() {

        sharedPreferences = getSharedPreferences(AppConstant().USER_SHARED_PREF, MODE_PRIVATE)
        sharedPrefManager = SharedPrefManager(this)

        tvPhone = findViewById(R.id.tvPhone)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)

        ivUserImage = findViewById(R.id.ivUserImage)

        btnRegister = findViewById(R.id.btnRegister)

        if (intent != null) {
            MobileNUmber = intent.getStringExtra("-phone")
            tvPhone.text = "Mobile: $MobileNUmber"
        }
    }

    private fun chooseImageType(){

        dialog = Dialog(this)

        dialog.setContentView(R.layout.choose_image)

        val btnCam = dialog.findViewById(R.id.btnCam) as Button
        val btnGal = dialog.findViewById(R.id.btnGalary) as Button
        val btnCan = dialog.findViewById(R.id.btnCancelCam) as Button

        btnCam.setOnClickListener {

            dialog.dismiss()
            if(isPermissionCameraGranted()){
                OpenCamera()
            }
        }

        btnGal.setOnClickListener {
            dialog.dismiss()
            if (isPermissionStorageGranted()) {
                OpenGallery()
            }
        }

        btnCan.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun isPermissionCameraGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) === PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted")
                return true
            } else {
                Log.v("TAG", "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISION_REQUEST_CODE
                )
                return false
            }

        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            return true
        }
    }

    fun isPermissionStorageGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) === PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted")
                return true
            } else {
                Log.v("TAG", "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISION_REQUEST_CODE
                )
                return false
            }

        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted")
            return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {

            CAMERA_PERMISION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss()
                    OpenCamera()
                } else {
                    //TODO permission denied
                }
                return
            }
            STORAGE_PERMISION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialog.dismiss()
                    OpenGallery()
                } else {
                    // TODO :: Permission Denied
                }
                return
            }

            // other 'case' lines to check for other
            // permissions this app might request
            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
    }

    fun OpenCamera(){
        val pm : PackageManager = packageManager

        if(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            i.putExtra(MediaStore.EXTRA_OUTPUT, MyFileContentProvider().CONTENT_URI)
            startActivityForResult(i, REQUEST_CAMERA)
        } else run {

            Toast.makeText(baseContext, "Camera is not available", Toast.LENGTH_LONG).show()
        }
    }

    fun OpenGallery() {

        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, REQUEST_STORAGE)

        dialog.dismiss()
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) run {

            val out = File(filesDir, "userImage.jpg")
            if (!out.exists()) {
                Toast.makeText(baseContext, "Error while capturing image", Toast.LENGTH_LONG).show()

                return
            }

            val mBitmap = BitmapFactory.decodeFile(out.getAbsolutePath())
            ivUserImage.setImageBitmap(mBitmap)

        }else if (requestCode == REQUEST_STORAGE && resultCode == RESULT_OK &&  data != null) {

            val selectedImage : Uri? = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor = selectedImage?.let {
                contentResolver.query(
                    it,
                    filePathColumn, null, null, null
                )
            }
            if (cursor != null) {
                cursor.moveToFirst()
            }

            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            if (cursor != null) {
                cursor.close()
            }
            val bmOptions = BitmapFactory.Options()
            val bitmap =
                getResizedBitmap(BitmapFactory.decodeFile(picturePath, bmOptions), 1000, 1000)

            ivUserImage.setImageBitmap(bitmap)
        }
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
        )
        bm.recycle()
        return resizedBitmap
    }

    private fun prepareRegistration() {

        val fName = etFirstName.text.toString()
        val lName = etLastName.text.toString()

        if (TextUtils.isEmpty(fName) || TextUtils.isEmpty(lName) || ivUserImage.drawable == null) {


            if (TextUtils.isEmpty(fName)) {

                etFirstName.error = "Please Complete this field"

            } else if (TextUtils.isEmpty(lName)) {


                etLastName.error = "Please Complete this field"

            } else if (ivUserImage.drawable == null) {

                Toast.makeText(applicationContext, "Please give a Profile photo", Toast.LENGTH_LONG)
                    .show()
            }

        } else {

            showProgressDialog("Registration", "Registering user..")
            registerUser(fName, lName, MobileNUmber)
        }
    }

    private fun registerUser(fName: String, lName: String, phone: String) {

        val multipartRequest =
            object : VolleyMultipartRequest(Request.Method.POST, ApiConstants().APP_Register,
                Response.Listener<NetworkResponse> { response ->
                    val resultResponse = String(response.data)
                    Log.d("$TAG Res:: ", resultResponse)
                    hideProgressDialog()

                    try {
                        val jObj = JSONObject(resultResponse)

                        if (jObj.getString("success") == "true") {

                            val jData = JSONObject(jObj.getString("data"))
                            val Token = jData.getString("token")

                            val spEdit = sharedPreferences.edit()
                            spEdit.putString(AppConstant().USER_SHARED_PREF_PHONE, phone)
                            spEdit.putString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN, Token)
                            spEdit.apply()
                            spEdit.commit()

                            checkUserRole()


                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    error.printStackTrace()
                    hideProgressDialog()
                }) {



                override fun getParams(): MutableMap<String, String> {
                    val params = HashMap<String, String>()
                    params.put("first_name",fName)
                    params.put("last_name",lName)
                    params.put("phone",phone)
                    return params
                }

                override fun getByteData(): MutableMap<String, DataPart> {
                    val params = HashMap<String, DataPart>()
                    params.put("profile_picture", DataPart("userImage.jpg",AppHelper().getFileDataFromDrawable(baseContext,ivUserImage.drawable),"image/jpeg"))
                    return params
                }


            }

        multipartRequest.setRetryPolicy(
            DefaultRetryPolicy(
                0, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        )

        var queue : RequestQueue = Volley.newRequestQueue(this)
        queue.add(multipartRequest)
        //Trackie().getInstance().addToRequestQueue(multipartRequest, TAG)
    }

    /**
     * check user role is set or not
     * if not send user to RoleManagementActivity to set role for a user
     * if yes send user to HomeActivity
     */

    private fun checkUserRole() {
        if (sharedPrefManager.getUserRole().equals("")) {
            val intent = Intent(this, RoleManagementActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val `in` = Intent(this@RegistrationActivity, HomeActivity::class.java)
            startActivity(`in`)
            finish()
        }
    }

}
