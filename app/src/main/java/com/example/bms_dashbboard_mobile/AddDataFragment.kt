import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.bms_dashbboard_mobile.DashboardActivity
import com.example.bms_dashbboard_mobile.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class AddDataFragment : Fragment() {
    private lateinit var mapController: MapController
    private lateinit var mapView: MapView
    private lateinit var edt_lat: EditText
    private lateinit var edt_long: EditText
    private lateinit var btn_find : ImageView
    private lateinit var edt_id : EditText
    private lateinit var edt_nama : EditText
    private lateinit var edt_alamat : EditText
    private lateinit var btn_regist : Button
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastX: Float = 0f
    private var lastY: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_data, container, false)

        Configuration.getInstance().load(activity, PreferenceManager.getDefaultSharedPreferences(activity))
        mapView = view.findViewById(R.id.mapView)
        edt_lat = view.findViewById(R.id.edt_lat)
        edt_long = view.findViewById(R.id.edt_long)
        edt_id = view.findViewById(R.id.edt_addidews)
        edt_nama = view.findViewById(R.id.edtnama)
        edt_alamat = view.findViewById(R.id.edt_alamatews)
        btn_find = view.findViewById(R.id.btn_get_current_location)
        btn_regist = view.findViewById(R.id.btn_simpan)

        mapView.setMultiTouchControls(true)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)

        mapController = mapView.controller as MapController

        locationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Set default location
        val defaultLocation = GeoPoint(-7.7896, 110.4263)
        mapController.setCenter(defaultLocation)
        mapController.zoomTo(9.0)

        // Add a marker for the default location
        val marker = Marker(mapView)
        marker.position = defaultLocation
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        mapView.invalidate()

        btn_find.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                // Request location permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1
                )
            }
        }

        locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000) // 10 seconds
            .setFastestInterval(5000) // 5 seconds

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.lastLocation?.let { location ->
                    mapController.setCenter(GeoPoint(location.latitude, location.longitude))
                    mapController.zoomTo(9.0)

                    edt_lat.setText(location.latitude.toString())
                    edt_long.setText(location.longitude.toString())

                    // Clear existing overlays (markers) on the map
                    mapView.overlays.clear()

                    // Add a new marker to the current location
                    val marker = Marker(mapView)
                    marker.position = GeoPoint(location.latitude, location.longitude)
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)

                    mapView.invalidate() // Update the map view
                }
            }
        }

        btn_regist.setOnClickListener {
            registerews()
        }

        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Simpan posisi awal sentuhan
                lastX = event.x
                lastY = event.y
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                // Hitung pergeseran
                val deltaX = lastX - event.x
                val deltaY = lastY - event.y

                // Pindahkan peta sesuai pergeseran
                mapController.scrollBy(deltaX.toInt(), deltaY.toInt())

                // Perbarui posisi terakhir
                lastX = event.x
                lastY = event.y
            }
            true
        }

        return view
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, get current location
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                locationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        val geoPoint = GeoPoint(location.latitude, location.longitude)
                        mapController.setCenter(geoPoint)
                        mapController.zoomTo(20.0)

                        // Clear existing overlays (markers) on the map
                        mapView.overlays.clear()

                        // Add a new marker to the current location
                        val marker = Marker(mapView)
                        marker.position = geoPoint
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(marker)

                        mapView.invalidate() // Update the map view

                        edt_lat.setText(location.latitude.toString())
                        edt_long.setText(location.longitude.toString())
                    }
                }
            } catch (e: SecurityException) {
                // Handle security exception (e.g., show a message to the user)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        locationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun registerews() {
        val id = edt_id.text.toString().trim()
        val nama = edt_nama.text.toString().trim()
        val alamat = edt_alamat.text.toString().trim()
        val lat = edt_lat.text.toString().trim()
        val long = edt_long.text.toString().trim()

        // Cek apakah lat dan long dapat dikonversi ke tipe data double
        val latDouble: Double? = lat.toDoubleOrNull()
        val longDouble: Double? = long.toDoubleOrNull()

        //POST API
        val jsonObject = JSONObject()
        try {
            jsonObject.put("id", id)
            jsonObject.put("nama_ews", nama)
            jsonObject.put("alamat", alamat)
            jsonObject.put("lat", latDouble)
            jsonObject.put("long", longDouble)

        }catch ( e: JSONException){
            e.printStackTrace()
        }

        AndroidNetworking.post("https://4504-180-242-105-22.ngrok-free.app/api/ews/create")
            .addJSONObjectBody(jsonObject)
            .addHeaders("Content-Type", "application/json")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        Log.d(ContentValues.TAG, response.toString())
                        if (response.getString("success").equals("true")) {
                            Toast.makeText(context, "input berhasil", Toast.LENGTH_SHORT).show()
                            val intent = Intent(requireContext(), DashboardActivity::class.java)
                            startActivity(intent)

                        } else {
//                            binding.verifikasiEmailPassword.text = response.getString("message")
//                            binding.warning.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
//                        e.printStackTrace()
                        Toast.makeText(context, "kesalahan", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onError(error: ANError) {
                    // handle error
                }
            })
    }
}