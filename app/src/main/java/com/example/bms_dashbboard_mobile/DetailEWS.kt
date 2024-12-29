package com.example.bms_dashbboard_mobile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.example.bms_dashbboard_mobile.api.ApiConstants
import com.example.bms_dashbboard_mobile.databinding.ActivityDetailEwsBinding
import com.example.bms_dashbboard_mobile.dataclass.DataConditions
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class DetailEWS : AppCompatActivity() {
    private lateinit var binding: ActivityDetailEwsBinding
    private lateinit var circularSOC: CircularProgressBar
    private lateinit var circularSOH: CircularProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEwsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = this.intent.getStringExtra("id")
        val nama = this.intent.getStringExtra("nama_ews")
        val alamat = this.intent.getStringExtra("alamat")

        binding.namaews.text = nama
        binding.alamatews.text = alamat

        circularSOH = findViewById(R.id.circleSOH)
        circularSOC = findViewById(R.id.circleSOC)

        fetchConditions(id.toString())

        binding.apply {
            linechartId.gradientFillColors = intArrayOf(
                Color.parseColor("#FF9F1C"),
                Color.TRANSPARENT
            )

            linechartVoltageId.gradientFillColors = intArrayOf(
                Color.parseColor("#FF9F1C"),
                Color.TRANSPARENT
            )
            linechartId.animation.duration= animationDuration
        }

        binding.apply {
            linechartAmpereId.gradientFillColors = intArrayOf(
                Color.parseColor("#FF9F1C"),
                Color.TRANSPARENT
            )

            linechartAmpereId.animation.duration= animationDuration
        }
    }

    override fun onResume() {
        super.onResume()
        val id = this.intent.getStringExtra("id")
        setupSwipeRefresh(id.toString())
    }

    private fun setupSwipeRefresh(id: String?) {
        binding.swipeRefreshLayout.setOnRefreshListener {
            id?.let {
                fetchConditions(it)
            }
        }
    }

    private fun fetchConditions(id: String) {
        binding.swipeRefreshLayout.isRefreshing = true
        val url = "${ApiConstants.EWS_CONDITIONS}$id"
        Log.d("ini url", url)
        AndroidNetworking.get(url)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsObject(DataConditions::class.java, object : ParsedRequestListener<DataConditions> {
                override fun onResponse(response: DataConditions) {

                    if (response.data.isNullOrEmpty()) {
                        binding.nodataimg.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.GONE
                    } else {
                        binding.nodataimg.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE

                        updateCircularBars(response)
                        updateCharts(response)
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                override fun onError(anError: ANError) {
                    Log.e("DetailEWS", "Error fetching data: ${anError.message}")
                    binding.swipeRefreshLayout.isRefreshing = false

                }
            })
    }


    private fun updateCircularBars(condition: DataConditions) {
        val conditionsMap = condition.data.associateBy { it._field }

        conditionsMap["soc"]?.let {
            circularSOC.apply {
                progress = it._value
                setProgressWithAnimation(it._value, 1000)
                progressMax = 100f
                progressBarColor = Color.RED
                backgroundProgressBarColor = Color.GRAY
            }
            binding.socIndicator.text = String.format("%.0f%%", conditionsMap["soc"]?._value ?: 0f)
        }

        conditionsMap["soh"]?.let {
            circularSOH.apply {
                progress = it._value
                setProgressWithAnimation(it._value, 1000)
                progressMax = 100f
                progressBarColor = Color.GREEN
                backgroundProgressBarColor = Color.GRAY
            }
            binding.sohIndicator.text = String.format("%.0f%%", conditionsMap["soh"]?._value ?: 0f)

        }

        conditionsMap["temp"]?.let {
            binding.tempIndicator.text = "${it._value}°C"
        }

        conditionsMap["ampere"]?.let {
            binding.ampereIndicator.text = String.format("%.1f A", conditionsMap["ampere"]?._value ?: 0f)

        }

        conditionsMap["voltage"]?.let {
//            binding.voltIndicator.text = "${it._value} "
            binding.voltIndicator.text = String.format("%.1f V", conditionsMap["voltage"]?._value ?: 0f)


        }

    }

    private fun updateCharts(condition: DataConditions) {
        val dataTemperature = mutableListOf<Pair<String, Float>>()
        val dataVoltage = mutableListOf<Pair<String, Float>>()
        val dataAmpere = mutableListOf<Pair<String, Float>>()

        condition.data.forEach { dataPoint ->
            Log.d("DataPoint", "Field: ${dataPoint._field}, Time: ${dataPoint._time}, Value: ${dataPoint._value}")
            when (dataPoint._field) {
                "temp" -> dataTemperature.add(dataPoint._time to dataPoint._value)
                "voltage" -> dataVoltage.add(dataPoint._time to dataPoint._value)
                "ampere" -> dataAmpere.add(dataPoint._time to dataPoint._value)
            }
        }

        Log.d("DataTemperature", dataTemperature.toString())
        Log.d("DataVoltage", dataVoltage.toString())
        Log.d("DataAmpere", dataAmpere.toString())

        binding.linechartId.animate(dataTemperature)
        binding.linechartVoltageId.animate(dataVoltage)
        binding.linechartAmpereId.animate(dataAmpere)
    }



    companion object {
        private const val animationDuration = 1000L
    }
}
