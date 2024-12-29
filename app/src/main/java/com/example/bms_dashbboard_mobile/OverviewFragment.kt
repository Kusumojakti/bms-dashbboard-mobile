package com.example.bms_dashbboard_mobile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.bms_dashbboard_mobile.adapter.adapterDataEws
import com.example.bms_dashbboard_mobile.api.ApiConstants
import com.example.bms_dashbboard_mobile.databinding.FragmentOverviewBinding
import com.example.bms_dashbboard_mobile.dataclass.dataEws
import org.json.JSONArray
import org.json.JSONObject

class OverviewFragment : Fragment() {
    private lateinit var binding: FragmentOverviewBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataewslist : ArrayList<dataEws>
    private lateinit var jumlahews : TextView

    private var totalEws: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_overview, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewSystem)
        dataewslist = arrayListOf<dataEws>()
        jumlahews = view.findViewById(R.id.txt_jumlahews)



        getData()

        return view
    }

    private fun getData() {

        AndroidNetworking.get(ApiConstants.EWS_DETAILS)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    Log.d("Ini respon fetch", response.toString())
                    if (response?.getString("success").equals("true")){
                        val res: JSONArray? = response?.getJSONArray("data")
                        for (i in 0 until res?.length()!!) {
                            val item = res.getJSONObject(i)
                            dataewslist.add(
                                dataEws(
                                    item.getString("id"),
                                    item.getString("nama_ews"),
                                    item.getString("alamat")
                                )
                            )
                        }
                        totalEws = res.length()
                        jumlahews.text = "$totalEws"
//                        shimmerFrameLayout.startShimmer()
//                        shimmerFrameLayout.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        populateData()
                    }else{
                        onDestroy()

                    }


                }

                override fun onError(anError: ANError?) {
                    onDestroy()

//                    alertDialogShow()
                }

            })

    }

    private fun populateData() {
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        val adp = activity?.let { adapterDataEws(it,dataewslist) }
        recyclerView.adapter = adp
    }

}