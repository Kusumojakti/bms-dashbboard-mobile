package com.example.bms_dashbboard_mobile.dataclass

import android.util.FloatMath
import java.util.Date

//data class dataConditions(
//    val temperature: Float,
//    val voltage: Float,
//    val current: Float,
//    val soc: Float,
//    val soh: Float
//)
data class Condition(
    val _field: String,
    val _value: Float,
    val _time: String
)

data class DataConditions(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<Condition>
)

