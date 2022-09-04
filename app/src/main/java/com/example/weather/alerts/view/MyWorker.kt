package com.example.weather.alerts.view

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.weather.R
import com.example.weather.model.WeatherModel
import com.example.weather.network.WeatherClient
import kotlinx.coroutines.runBlocking


class MyWorker(var context: Context, workerParameters: WorkerParameters):
    Worker(context,workerParameters) {

    private lateinit var sharedPreferences: SharedPreferences


    companion object{
        const val CHANNEL_ID = "channelID"
    }
    override fun doWork(): Result {
        val lat =  inputData.getString("lat")
        val lon =  inputData.getString("lon")
        val address=inputData.getString("address")
        val apiKey= "68dea5913ee5edc56461d63440681c6c"
        sharedPreferences = applicationContext
            .getSharedPreferences("WeatherAPPSetting", Context.MODE_PRIVATE)
        val lang=sharedPreferences.getString("language","")!!

        val client: WeatherClient = WeatherClient.getInstance()
        var responseModel: WeatherModel
        runBlocking {
            responseModel = client.getCurrentWeather(lat.toString(), lon.toString(),lang,apiKey)
        }

        showNotification(address.toString(),"Weather now is ${responseModel.current!!.weather[0].description.toString()}")
      /*  if(responseModel.alerts!=null){
            showNotification(responseModel.current!!.temp.toString())

        }
        else{
            showNotification("No Alert!!")
        }*/

        return Result.success()
    }

    private fun showNotification(title:String,desc:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =applicationContext
                .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.alert)
            .setContentTitle(title)
            .setContentText(desc)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1, builder.build())
        }
    }
}