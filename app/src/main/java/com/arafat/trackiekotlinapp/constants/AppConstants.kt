package com.arafat.trackiekotlinapp.constants

class AppConstant {
    //shared pref
    val USER_SHARED_PREF = "userSharedPref"
    val USER_SHARED_PREF_PHONE = "user_phone_number"
    val USER_SHARED_PREF_LOCATION_UPDATE_TRACK_ID = "user_track_id"
    val USER_SHARED_PREF_PASSWORD = "user_password"
    val USER_SHARED_PREF_ACCESS_TOKEN = "user_access_token"
    val USER_SHARED_PREF_ONLINE_TIME = "user_online_time"
    val USER_SHARED_PREF_ONLINE_STATUS = "user_online_status"
    val USER_PREF_ROLE = "user_role"
    val USER_ROLE_TEAM_OWNER = "team_owner"
    val USER_ROLE_TEAM_MEMBER = "team_member"


    //API RESPONSE CONSTANTS
    val CODE = "code"
    val SUCCESS = "success"
    val ERRORS = "errors"
    val SHOW_MESSAGE = "show_message"
    val DATA = "data"
    val APP = "app"
    val UPDATE_AVAILABLE = "update_available"
    val UPDATE_LINK = "update_link"

    //api headers constants
    val AUTHORIZATION = "Authorization"
    val BEARER = "Bearer "
    val TOKEN = "token"
    val ACCESS_TOKEN = "access_token"


    // notification
    val PUSH_DIGNOSIS_NOTIFICATION = "PUSH_NOTIFICATION"
    val CONTACT_PERMISSIONS_REQUEST_CODE = 1001

    val CHANNEL_ID = "com.muvasia.trackie"
    val CHANNEL_NAME = "TRACKIE CHANNEL"


    //Activity Recognition
    val BROADCAST_DETECTED_ACTIVITY = "activity_recognition"
    val DETECTION_INTERVAL_IN_MILLISECONDS = (2 * 1000).toLong()
    val CONFIDENCE = 70

    val UNKNOWN = 0
    val STILL = 1
    val ON_FOOT = 2
    val WALKING = 3
    val RUNNING = 4
    val ON_BICYCLE = 5
    val IN_VEHICLE = 6


    //UTIL CONSTANTS
    val REQUEST_GPS_CHECK_SETTINGS = 2001


    //CountryCode
    val BD_CODE = "+88"


    //Intent Changer Constants

    val INTENT_USER_ID = "-userId"
}