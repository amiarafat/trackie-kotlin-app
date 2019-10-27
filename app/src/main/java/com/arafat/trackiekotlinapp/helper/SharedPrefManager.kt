package com.arafat.trackiekotlinapp.helper

import android.content.Context
import android.content.SharedPreferences
import com.arafat.trackiekotlinapp.constants.AppConstant

class SharedPrefManager{

     val userPref : SharedPreferences
     val pref : SharedPreferences

     val editor: SharedPreferences.Editor
     val editorUser: SharedPreferences.Editor

     val context : Context

    val PRIVATE_MODE = 0

    // Shared preferences file name
    private val PREF_NAME_TRACKIE_INTRO = "trackie-welcome"
    private val PREF_NAME_TRACKIE_USER = "trackie-user"

    private val IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch"
    private val IS_USER_ROLE_SET = "isUserRoleSet"


    constructor(context: Context) {
        this.context = context
        pref = context.getSharedPreferences(PREF_NAME_TRACKIE_INTRO, PRIVATE_MODE)
        userPref = context.getSharedPreferences(PREF_NAME_TRACKIE_USER, PRIVATE_MODE)
        editor = pref.edit()
        editorUser = userPref.edit()
    }

    /**
     * set first time launching
     */
     fun setFirstTimeLaunch(isFirstTime:Boolean){

        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor.commit()
    }

    /**
     * check user is opening app first time or not
     */
     fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }

    /**
     * set user role
     */
    fun setUserRole(userRole: String) {
        editorUser.putString(AppConstant().USER_PREF_ROLE, userRole)
        editorUser.commit()
    }

    /**
    *Get User Role
    */
    fun  getUserRole(): String {
        return userPref.getString(AppConstant().USER_PREF_ROLE,"").toString()
    }

    /*
    * set User Access Token
    * */
    fun setUserAccessToken(accessToken:String){

        editorUser.putString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN,accessToken);
        editorUser.commit();
    }

    /*
    * get User Access Token
    * */
    fun getUserAccessToken(): String {

        return userPref.getString(AppConstant().USER_SHARED_PREF_ACCESS_TOKEN,"").toString()

    }

    /*
    * set User Online Status*/
    fun setUserOnlineStatus(status: String) {

        editorUser.putString(AppConstant().USER_SHARED_PREF_ONLINE_STATUS, status)
        editorUser.commit()
    }

    /*
    * get User Online Status
    * */

    fun getUserOnlineStatus(): String {

        return userPref.getString(AppConstant().USER_SHARED_PREF_ONLINE_STATUS, "").toString()
    }

    fun deleteSharedPrefManager() {
        editor.clear().commit()
        editorUser.clear().commit()

    }
}