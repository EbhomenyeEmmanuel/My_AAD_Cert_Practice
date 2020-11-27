package com.esq.myaadcertpractice.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esq.myaadcertpractice.MainActivity
import com.esq.myaadcertpractice.R
import com.esq.myaadcertpractice.utils.Constants.NOTIFICATION_ID
import com.esq.myaadcertpractice.utils.Constants.PRIMARY_CHANNEL_ID


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private lateinit var notificationManager: NotificationManager
    private val ACTION_UPDATE_NOTIFICATION =
            "com.esq.myaadcertpractice.ACTION_UPDATE_NOTIFICATION"

    // Obtain ViewModel from ViewModelProviders
    private val applicationContext by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        activity.applicationContext
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createNotificationChannel()
        view.findViewById<Button>(R.id.button_second).setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

        view.findViewById<Button>(R.id.notify).setOnClickListener {
            sendNotification()
        }

        view.findViewById<Button>(R.id.notify).setOnLongClickListener {
            updateNotification()
            return@setOnLongClickListener true
        }
        context?.registerReceiver(mReceiver, IntentFilter(ACTION_UPDATE_NOTIFICATION))
    }

    private fun sendNotification() {
        val updateIntent = Intent(ACTION_UPDATE_NOTIFICATION)
        val updatePendingIntent = PendingIntent.getBroadcast(context, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT)
        val notifyBuilder = getNotificationBuilder()
        notifyBuilder!!.addAction(R.drawable.ic_update_24, "Update Notification", updatePendingIntent)
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder!!.build())

    }

    private fun updateNotification() {
        val notifyBuilder = getNotificationBuilder()
        val androidImage = BitmapFactory
                .decodeResource(resources, R.drawable.mascot_1)
        notifyBuilder!!.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Notification Updated!"))
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build())
    }

    private fun createNotificationChannel() {
        notificationManager = getSystemService(applicationContext, NotificationManager::class.java)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Null Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true);
            notificationChannel.lightColor = Color.RED;
            notificationChannel.enableVibration(true);
            notificationChannel.description = "Notification from Null";
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private fun getNotificationBuilder(): NotificationCompat.Builder? {
        val notificationIntent = Intent(context, MainActivity::class.java)
        val notificationPendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat.Builder(applicationContext, PRIMARY_CHANNEL_ID)
                .setContentTitle("First Notification!")
                .setContentText("This is your notification text.")
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Now you can call all your fragments method here
            updateNotification()
        }
    }

    override fun onDestroy() {
        context?.unregisterReceiver(mReceiver)
        super.onDestroy()
    }
}