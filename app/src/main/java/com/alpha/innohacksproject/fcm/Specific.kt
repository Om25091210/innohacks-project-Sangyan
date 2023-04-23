package com.alpha.innohacksproject.fcm

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Specific {

    private val TAG="send"

    fun noti(name:String,phone:String,token:String,key:String,section:String){

        if(name.isNotEmpty() && phone.isNotEmpty()){
            PushNotification(
                NotificationData(name, phone,key,section),
                token
            ).also {
                sendNotification(it)
            }
        }
    }

    private fun sendNotification(notification: PushNotification)= CoroutineScope(Dispatchers.IO).launch {

        try{
            val response= RetrofirInstance.api.postNotification(notification)
            if(response.isSuccessful){
                Log.d(TAG,"Response:${Gson().toJson(response)}")
            }else{
                Log.e(TAG,response.errorBody().toString())
            }

        }catch (e:Exception){
            Log.e(TAG,e.toString())
        }

    }

}