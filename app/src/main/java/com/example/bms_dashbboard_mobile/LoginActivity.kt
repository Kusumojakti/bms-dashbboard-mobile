package com.example.bms_dashbboard_mobile

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.bms_dashbboard_mobile.api.ApiConstants
import com.example.bms_dashbboard_mobile.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            verifikasiLogin()
        }

    }
    private fun verifikasiLogin(){
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        val jsonObject = JSONObject()
        try {
            jsonObject.put("email", email)
            jsonObject.put("password", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        AndroidNetworking.post(ApiConstants.LOGIN)
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("message").equals("success")) {
                            Log.d("ini berhasil login", response.toString())
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()

                            //sharetoken
                            val shareToken = response.getString("accesstoken")
                            val sharedPreference =  getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
                            val editorToken = sharedPreference.edit()
                            editorToken.putString("accesstoken",shareToken)
                            editorToken.commit()

                        } else {
                            Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT).show()
//                            binding.verifikasiEmailPassword.text = response.getString("message").equals("Wrong Password!").toString()
                            binding.warning.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(this@LoginActivity, "kesalahan", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }
//
//    private fun alertDialogShow(){
//        val loading = loadingDialog(this)
//        loading.startLoading()
//
//    }
}