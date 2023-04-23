package com.alpha.innohacksproject.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log

import android.widget.RemoteViews

import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.alpha.innohacksproject.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID="my_channel"

class FirebaseService : FirebaseMessagingService() {
private const val CHANNEL_ID="my_channel"

class FirebaseService : FirebaseMessagingService(){

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("newToken", p0)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
       /* //val intent = Intent(this, temp_notification::class.java)
        Log.e("logging_info", message.data["key"] + "")
        Log.e("logging_info", message.data["section"] + "")
        intent.putExtra("sending_msg_data", "" + message.data["key"])*/
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotifionChannel(notificationManager)
        }

        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val intent= Intent(this, TempNotification::class.java)
        Log.e("logging_info",message.data["key"]+"")
        Log.e("logging_info",message.data["section"]+"")
        intent.putExtra("sending_msg_data",""+message.data["key"])
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            createNotifionChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationID,
         //   intent,
            PendingIntent.FLAG_MUTABLE
        )
        //TODO: Change mutable to flag update current
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["body"])
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.data["body"]))
            .setSmallIcon(R.drawable.ic_cap)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_cap))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)

        //Initialize Database
        if (message.data["section"].equals("data")) {
            val reference = FirebaseDatabase.getInstance().reference.child("data")
            reference.child(message.data["key"] + "")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            reference.child("" + message.data["key"]).child("sent").setValue("1")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        } else if (message.data["section"].equals("notice")) {
            val reference = FirebaseDatabase.getInstance().reference.child("notice")
            reference.child(message.data["key"] + "")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            reference.child("" + message.data["key"]).child("sent").setValue("1")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifionChannel(notificationManager: NotificationManager) {
        val channelName = "ChannelName"
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description = "My channel description"
                enableLights(true)
                lightColor = Color.GREEN
                
        if(message.data["section"].equals("data")) {
            val reference = FirebaseDatabase.getInstance().reference.child("data")
            reference.child(message.data["key"]+"").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        reference.child("" + message.data["key"]).child("sent").setValue("1")
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotifionChannel(notificationManager: NotificationManager){
        val channelName="ChannelName"
        val channel=
            NotificationChannel(CHANNEL_ID,channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description="My channel description"
                enableLights(true)
                lightColor= Color.GREEN
            }
        notificationManager.createNotificationChannel(channel)
    }
}