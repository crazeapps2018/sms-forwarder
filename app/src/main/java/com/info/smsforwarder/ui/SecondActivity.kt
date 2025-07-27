
package com.info.smsforwarder.ui

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.info.smsforwarder.databinding.ActivitySecondBinding


class SecondActivity : ComponentActivity() {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var progressDialog: ProgressDialog

    companion object {
        const val SMS_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: "Unknown"
        binding.textViewUsername.text = "Username: $username"

        binding.buttonLogout.setOnClickListener {
            logout()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        // Check and request permission here
        if (!checkSmsPermission()) {
            requestSmsPermission()
        }

        binding.buttonSubmit.setOnClickListener {
            val number = binding.editTextNumber.text.toString()
            val message = binding.editTextMessage.text.toString()

            if (number.isBlank() || message.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkSmsPermission()) {
                Toast.makeText(this, "SMS permission not granted yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sendApiAndSms(number, message)
        }
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                // Clear the shared preferences
                val prefs = getSharedPreferences("user_session", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Navigate back to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun sendApiAndSms(number: String, message: String) {
        progressDialog.show()

        runOnUiThread {
            progressDialog.dismiss()
            Toast.makeText(
                this@SecondActivity,
                "API Success. Sending SMS...",
                Toast.LENGTH_SHORT
            ).show()
            sendSms(number, message)
        }

    }

    private fun sendSms(destinationNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(
                destinationNumber,
                null,
                message,
                null,
                null
            )
            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "SMS failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSmsPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.SEND_SMS),
            SMS_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS permission denied. Cannot send SMS.", Toast.LENGTH_LONG).show()
            }
        }
    }
}


//private fun openSmsApp(destinationNumber: String, message: String) {
//    try {
//        val smsUri = Uri.parse("smsto:$destinationNumber")
//        val intent = Intent(Intent.ACTION_SENDTO, smsUri)
//        intent.putExtra("sms_body", message)
//        startActivity(intent)
//    } catch (e: Exception) {
//        Toast.makeText(this, "Unable to open SMS app: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
//    }
//}