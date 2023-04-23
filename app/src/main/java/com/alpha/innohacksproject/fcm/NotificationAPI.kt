package com.alpha.innohacksproject.fcm

import com.android.volley.Response
import com.alpha.innohacksproject.fcm.Constants.Companion.CONTENT_TYPE
import com.alpha.innohacksproject.fcm.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY","Content_Type:$CONTENT_TYPE")
    @POST("com/alpha/innohacksproject/fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}