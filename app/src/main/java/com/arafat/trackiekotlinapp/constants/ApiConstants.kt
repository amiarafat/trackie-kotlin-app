package com.arafat.trackiekotlinapp.constants

class ApiConstants {

    /*Info Bip Base Url*/
    val INFO_BIP_BASE_URL : String = "https://kw2w8.api.infobip.com/"
    val INFO_BIP_PIN = INFO_BIP_BASE_URL + "2fa/1/pin"

    private val API_DOMAIN = "https://trackieapi.muvasia.site/api/v1/" //TEST
    //private static final String API_DOMAIN = "https://trackieapi.muv.asia/api/v1/"; //LIVE

    val API_INVITATION = API_DOMAIN + "invitations/"
    val API_INVITE_USER = API_INVITATION + "send"
    val API_INVITE_REQUESTS = API_INVITATION + "requests"
    val API_INVITE_CANCEL = API_INVITATION + "cancel"
    val API_INVITE_REJECT = API_INVITATION + "reject"
    val API_INVITE_ACCEPT = API_INVITATION + "accept"
    val API_INVITE_CONNECTED = API_INVITATION + "connected"

  // Auth
    val APP_BOOT = API_DOMAIN + "app/boot"
    val APP_LOGIN = API_DOMAIN + "login"
    val APP_Register = API_DOMAIN + "register"
    val APP_DEVICE_INFO = API_DOMAIN + "users/device-info/store"
    val APP_STATE = API_DOMAIN + "app/state"


  //LocationUpdate
    val APP_ONLINE_STATUS = API_DOMAIN + "users/online"
    val APP_LOCATION_UPDATE = API_DOMAIN + "users/locations/update"
    val APP_SINGLE_USER_LOCATION_UPDATE = API_DOMAIN + "users/"

  //Users
    val API_CONNECTED_USERS_LOCATION = API_DOMAIN + "users/locations"
    val API_USERS_PROFILE = API_DOMAIN + "users/profile"

}