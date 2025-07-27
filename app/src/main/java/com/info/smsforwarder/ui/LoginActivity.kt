package com.info.smsforwarder.ui


import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.info.smsforwarder.api.ApiClient
import com.info.smsforwarder.databinding.ActivityLoginBinding
import com.info.smsforwarder.model.ApiResponse
import com.info.smsforwarder.ui.SecondActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.info.smsforwarder.R

class LoginActivity : ComponentActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences("user_session", MODE_PRIVATE)

        val savedUsername = prefs.getString("username", null)
        if (!savedUsername.isNullOrEmpty()) {
            navigateToSecondActivity(savedUsername)
            finish()
            return
        }



            binding.buttonLogin.setOnClickListener {
                val mobileNumber = binding.editTextMobile.text.toString().trim()

                if (mobileNumber.isEmpty()) {
                    Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    showLoading()

                    try {
                        val response = withContext(Dispatchers.IO) {
                            ApiClient.instance.updateAppStatus(
                                mobileNumber = mobileNumber,
                                appStatus = "Installed"
                            )
                        }

                        hideLoading()

                        if (response.isSuccessful) {
                            val apiResponse = response.body()

                            if (apiResponse?.status.equals("Success", ignoreCase = true)) {
                                prefs.edit().putString("username", mobileNumber).apply()
                                navigateToSecondActivity(mobileNumber)
                                finish()
                            } else {
                                showErrorDialog("This mobile number is not registered in Admin Panel" ?: "Unexpected error")
                            }
                        }
                        else {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
                            showErrorDialog(errorResponse.message)
                        }


                    } catch (e: Exception) {
                        hideLoading()
                        showErrorDialog("Network or Server Error: ${e.localizedMessage}")
                    }
                }
            }



    }

    private fun navigateToSecondActivity(username: String) {
        val intent = Intent(this, SecondActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }

    private var progressDialog: AlertDialog? = null

    private fun showLoading() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_loader, null)
        builder.setView(view)
        builder.setCancelable(false)
        progressDialog = builder.create()
        progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog?.show()
    }


    private fun hideLoading() {
        progressDialog?.dismiss()
    }


}
