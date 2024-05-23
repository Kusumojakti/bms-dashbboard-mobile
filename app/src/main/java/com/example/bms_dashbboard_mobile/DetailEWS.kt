package com.example.bms_dashbboard_mobile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.bms_dashbboard_mobile.databinding.ActivityDetailEwsBinding
import com.example.bms_dashbboard_mobile.dataclass.dataConditions
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DetailEWS : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEwsBinding
    private lateinit var circularTemp: CircularProgressBar
    private lateinit var circularVolt: CircularProgressBar
    private lateinit var circularAmp: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEwsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = this.intent.getStringExtra("nama_ews")
        val alamat = this.intent.getStringExtra("alamat")

        binding.namaews.text = nama
        binding.alamatews.text = alamat

        circularTemp = findViewById(R.id.circleTemp)
        circularVolt = findViewById(R.id.circleVoltage)
        circularAmp = findViewById(R.id.circleAmpere)

        fetchConditions()
    }

    private fun fetchConditions() {
        AndroidNetworking.get("https://4504-180-242-105-22.ngrok-free.app/api/iot/conditions")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(dataConditions::class.java, object : ParsedRequestListener<dataConditions> {
                override fun onResponse(response: dataConditions) {
                    // Handle success
                    updateCircularBars(response)
                }

                override fun onError(anError: ANError) {
                    // Handle error
                    Log.e("DetailEWS", "Error fetching data: ${anError.message}")
                }
            })
    }

    private fun updateCircularBars(condition: dataConditions) {
        circularTemp.apply {
            progress = condition.temperature
            setProgressWithAnimation(condition.temperature, 1000)
            progressMax = 100f
            progressBarColor = Color.RED
            backgroundProgressBarColor = Color.GRAY
        }

        circularVolt.apply {
            progress = condition.voltage
            setProgressWithAnimation(condition.voltage, 1000)
            progressMax = 300f
            progressBarColor = Color.GREEN
            backgroundProgressBarColor = Color.GRAY
        }

        circularAmp.apply {
            progress = condition.current
            setProgressWithAnimation(condition.current, 1000)
            progressMax = 50f
            progressBarColor = Color.BLUE
            backgroundProgressBarColor = Color.GRAY
        }
    }
}
