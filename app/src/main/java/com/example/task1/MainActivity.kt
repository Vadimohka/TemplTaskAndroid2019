package com.example.task1

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders

import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T


class MainViewModel : ViewModel() {}


class MainActivity : AppCompatActivity() {

    private val STATE_PERMISSION_CODE = 1

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this)[MainViewModel::class.java]
        val verNameTextView = findViewById<TextView>(R.id.verNameView)
        val deviceIdTextView = findViewById<TextView>(R.id.deviceIdView)

        val pinfo = packageManager.getPackageInfo(packageName, 0)


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            requestStatePermission();
        }

        verNameTextView.text = "The app version name is " + pinfo.versionName
        deviceIdTextView.text = getDeviceId()
    }

    private fun requestStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)
        ) {

            AlertDialog.Builder(this)
                .setTitle("The app need an access to your phone state")
                .setMessage("This permission is required to display your device ID")
                .setPositiveButton("ok", DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.READ_PHONE_STATE), STATE_PERMISSION_CODE
                    )
                })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create().show()

        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_PHONE_STATE), STATE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == STATE_PERMISSION_CODE) {
            val deviceIdTextView = findViewById<TextView>(R.id.deviceIdView)
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                deviceIdTextView.text = getDeviceId()
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
                deviceIdTextView.text = getDeviceId()
            }
            deviceIdTextView.text = getDeviceId()
        }
    }

    @SuppressLint("NewApi")
    fun getDeviceId(): String {
        val telephonyManager: TelephonyManager
        try {
            telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
            return "DeviceID: " + telephonyManager.imei
        } catch (e: SecurityException) {
            return "DeviceID: Permission denied"
        }
    }
}