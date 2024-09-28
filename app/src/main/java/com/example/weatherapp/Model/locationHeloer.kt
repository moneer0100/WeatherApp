package com.example.weatherapp.Model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.weatherapp.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat.getString


fun getAddress(context: Context, lat: Double?, lon: Double?):String{
    try {
        var address: MutableList<Address>?
        val geocoder = Geocoder(context)
        address = geocoder.getFromLocation(lat!!, lon!!, 1)
        if (address?.isEmpty() == true) {
            return "Unkown location"
        } else if (address?.get(0)?.countryName.isNullOrEmpty()) {
            return "Unkown Country"
        } else if (address?.get(0)?.adminArea.isNullOrEmpty()) {
            return address?.get(0)?.countryName.toString()
        } else {
            return address?.get(0)?.countryName.toString() + ", " + address?.get(0)?.adminArea + ", " + address?.get(
                0
            )?.locality
        }
    }catch (e: IOException) {
        e.printStackTrace()
        return "Error fetching address"
    }
}
fun TextView.setDate(timeInMilliSecond: Long){
    text = getADateFormat(timeInMilliSecond)
}

fun TextView.setTime(timeInMilliSecond: Long) {
    text = getTimeFormat(timeInMilliSecond)
}
fun getTimeFormat(timeInMilliSecond: Long,timeZone : TimeZone): String {
    val calendar = GregorianCalendar(timeZone)
    calendar.timeInMillis = timeInMilliSecond
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    convertFormat.timeZone = timeZone
    return convertFormat.format(calendar.time).toString()
}

fun getTimeFormat(timeInMilliSecond: Long): String {
    val date = Date(timeInMilliSecond)
    val convertFormat =
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    return convertFormat.format(date).toString()
}
fun getADateFormat(timeInMilliSecond: Long):String{
    val pattern = "dd MMMM"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale(getLanguageLocale()))
    return simpleDateFormat.format(Date(timeInMilliSecond))
}

fun getLanguageLocale(): String {
    return AppCompatDelegate.getApplicationLocales().toLanguageTags()
}

private const val CHANNEL_ID = "my_channel_id"
private const val NOTIFICATION_ID = 1
fun sendNotification(context: Context, message:String) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && notificationManager.getNotificationChannel(CHANNEL_ID) == null
    ) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.enableVibration(true)
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

//    val intent = ReminderDescriptionActivity.newIntent(context.applicationContext, reminderDataItem)

//    //create a pending intent that opens ReminderDescriptionActivity when the user clicks on the notification
//    val stackBuilder = TaskStackBuilder.create(context)
//        .addParentStack(ReminderDescriptionActivity::class.java)
//        .addNextIntent(intent)
//    val notificationPendingIntent = stackBuilder
//        .getPendingIntent(getUniqueId(), PendingIntent.FLAG_UPDATE_CURRENT)

//    build the notification object with the data to be shown
    val notification = Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(context,R.string.app_name))
        .setContentText(message)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setDefaults(Notification.DEFAULT_ALL)
        .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
//        .setContentIntent(notificationPendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(0, notification)
}

fun String.toArabicNumerals(): String {
    val englishDigits = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    val arabicDigits = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
    var result = this
    for (i in englishDigits.indices) {
        result = result.replace(englishDigits[i], arabicDigits[i])
    }
    return result
}
