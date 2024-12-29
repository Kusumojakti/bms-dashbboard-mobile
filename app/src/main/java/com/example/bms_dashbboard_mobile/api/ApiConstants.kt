package com.example.bms_dashbboard_mobile.api

object ApiConstants {
    private const val BASE_URL = "https://bms.zegion.site/api/"
    const val LOGIN = "${BASE_URL}auth/login"
    const val EWS_DETAILS  = "${BASE_URL}ews"
    const val EWS_CONDITIONS  = "${BASE_URL}iot/conditions/"
    const val EWS_CREATE  = "${BASE_URL}ews/create"

}