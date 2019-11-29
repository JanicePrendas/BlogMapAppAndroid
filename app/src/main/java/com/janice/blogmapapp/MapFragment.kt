package com.janice.blogmapapp


import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.io.IOException

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var lastLocation: Location
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val search = view.findViewById<EditText>(R.id.search)
        search.onSubmit {
            searchAddress()
        }
        return view
    }

    private fun searchAddress() {
        map.clear() //Remove old markers

        val location = search.text.toString()
        var addressList : List<Address>? = null

        if(location != ""){
            val geocoder = Geocoder(requireContext())

            try{
                addressList = geocoder.getFromLocationName(location, 5)
            }catch (e : IOException){}

            if(addressList!!.isNotEmpty()){
                for(i in addressList!!.indices){
                    val address = addressList[i]
                    val latLng = LatLng(address.latitude,address.longitude)
                    placeMarkerOnMap(latLng)
                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                }
            }
            else{
                showNoResultsPopup()
            }
        }
    }

    fun showNoResultsPopup(){
        // Initialize a new instance of
        val builder = AlertDialog.Builder(requireContext())

        // Set the alert dialog title
        builder.setTitle("No results found")

        // Display a message on alert dialog
        builder.setMessage("Please, try again.")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("OK"){dialog, which ->

        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    fun EditText.onSubmit(func: () -> Unit) {
        setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                func()
            }

            true

        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PLACE_PICKER_REQUEST = 3

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)

        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)

        map.addMarker(markerOptions)
    }

    fun getAddress(latLng: LatLng): String{
        val context = this
        val lat = latLng.latitude
        val lon = latLng.longitude
        var addresses = mutableListOf<Address>()
        var errorMessage = ""
        var address: String ?= null

        try {
            addresses = Geocoder(requireContext()).getFromLocation(lat, lon, 1)
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
        }

        if(addresses.isEmpty()) {
            if(errorMessage.isEmpty()) {}
        }else{
            val addressItem = addresses.first()
            val addressFragments = (0 .. addressItem.maxAddressLineIndex).map { i ->
                addressItem.getAddressLine(i)
            }

            address = addressFragments.first()
        }

        return address ?: errorMessage
    }



}
