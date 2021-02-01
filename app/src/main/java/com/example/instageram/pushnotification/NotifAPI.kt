package com.example.instageram.pushnotification

import com.example.instageram.pushnotification.Constant.Companion.CONTENT_TYPE
import com.example.instageram.pushnotification.Constant.Companion.SERVER_KEY
import com.google.firebase.firestore.auth.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.ResponseBody


interface NotifAPI {

//    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
//    @POST("fcm/send")
//    fun postNotif(
//        @Body notification: PushNotifModel
//    ): Response<okhttp3.ResponseBody>

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotif(
        @Body notification: PushNotifModel
    ): Response<ResponseBody>

}