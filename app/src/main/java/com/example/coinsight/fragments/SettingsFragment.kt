package com.example.coinsight.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.coinsight.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val themeSwitch = findPreference<SwitchPreferenceCompat>("dark_mode")
        themeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkMode = newValue as Boolean
            activity?.let {
                it.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                    .putBoolean("dark_mode", isDarkMode)
                    .apply()
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
            true
        }

        val notificationsSwitch = findPreference<SwitchPreferenceCompat>("notifications")
        notificationsSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val enableNotifications = newValue as Boolean
            activity?.let {
                it.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                    .putBoolean("notifications", enableNotifications)
                    .apply()
                if (enableNotifications) {
                    setupNotifications()
                } else {
                    disableNotifications()
                }
            }
            true
        }

        val locationSwitch = findPreference<SwitchPreferenceCompat>("location")
        locationSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val enableLocation = newValue as Boolean
            activity?.let {
                it.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
                    .putBoolean("location", enableLocation)
                    .apply()
                if (enableLocation) {
                    if (ContextCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            it,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_LOCATION_PERMISSION
                        )
                    } else {
                        try {
                            startLocationTracking()
                        } catch (e: SecurityException) {
                            Toast.makeText(context, "Location permission is required for this feature.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    stopLocationTracking()
                }
            }
            true
        }
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION_PERMISSION
            )
            return
        }

        createNotificationChannel()

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Notifications Enabled")
            .setContentText("You will receive notifications")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(1, builder.build())
        }

        val confirmationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Notifications")
            .setContentText("Notifications have been turned on")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(2, confirmationBuilder.build())
        }

        Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
    }

    private fun disableNotifications() {
        Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()
    }

    private fun startLocationTracking() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                Toast.makeText(context, "Location tracking enabled", Toast.LENGTH_SHORT).show()

                val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.location)
                    .setContentTitle("Location Tracking")
                    .setContentText("Location tracking has been turned on")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                with(NotificationManagerCompat.from(requireContext())) {
                    notify(3, builder.build())
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Location permission is required to start tracking.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Location permission is required to start tracking.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopLocationTracking() {
        Toast.makeText(context, "Location tracking disabled", Toast.LENGTH_SHORT).show()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.location)
                .setContentTitle("Location Tracking")
                .setContentText("Location tracking has been turned off")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            with(NotificationManagerCompat.from(requireContext())) {
                notify(4, builder.build())
            }
        } else {

            Toast.makeText(context, "App does not have permission to send notifications", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Channel for app notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val REQUEST_NOTIFICATION_PERMISSION = 2
        private const val CHANNEL_ID = "default_channel"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        startLocationTracking()
                    } catch (e: SecurityException) {
                        Toast.makeText(context, "Location permission is required to start tracking.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    findPreference<SwitchPreferenceCompat>("location")?.isChecked = false
                    Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_NOTIFICATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setupNotifications()
                } else {
                    findPreference<SwitchPreferenceCompat>("notifications")?.isChecked = false
                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
