package com.example.payflowapp.ui.auth

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.example.payflowapp.R
import com.example.payflowapp.data.viewmodel.AuthViewModel
import androidx.core.content.edit


/*
class SafeZoneFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_safe_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)


        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}
 */

class SafeZoneFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var safeLatLng: LatLng? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_safe_zone, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Verifica y solicita permisos de ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }
        Log.d("TAG-NICO-0","${R.string.google_maps_key}");
        // Inicializa Places
        if (!Places.isInitialized()) {
            Log.d("TAG-NICO","${R.string.google_maps_key}");
            Places.initialize(requireContext(), "AIzaSyAnVuIGNlIC3iT0n1I28sjjDZFrHccziDg")
        }

        val etSearchPlace = view.findViewById<TextInputEditText>(R.id.etSearchPlace)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarUbicacion)

        etSearchPlace.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("PE") // Solo Perú
                .build(requireContext())
            startActivityForResult(intent, 100)
        }

        btnGuardar.setOnClickListener {
            safeLatLng?.let {
                // Imprimir latitud y longitud
                Log.d("SafeZone", "Latitud: ${it.latitude}, Longitud: ${it.longitude}")

                val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                prefs.edit() {
                    putFloat("latitud", it.latitude.toFloat())
                        .putFloat("longitud", it.longitude.toFloat())
                }
                val userid = prefs.getString("user_id", "") ?: ""
                viewModel.updateUserLocation(userid, it.latitude, it.longitude)

                Toast.makeText(requireContext(), "Zona segura guardada ", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } ?: Toast.makeText(requireContext(), "Selecciona una ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMyLocationButtonEnabled = true

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                } else {
                    // Si no hay ubicación disponible, centramos en Lima
                    val lima = LatLng(-12.0464, -77.0428)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
                }
            }
        }

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Zona segura"))
            safeLatLng = latLng
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            place.latLng?.let {
                map.clear()
                map.addMarker(MarkerOptions().position(it).title(place.name))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
                safeLatLng = it
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}


/*class SafeZoneFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var safeLatLng: LatLng? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_safe_zone, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }

        val etSearchPlace = view.findViewById<TextInputEditText>(R.id.etSearchPlace)
        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarUbicacion)

        // Inicializar Places
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }

        etSearchPlace.setOnClickListener {
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
            ).build(requireContext())
            startActivityForResult(intent, 100)
        }

        btnGuardar.setOnClickListener {
            safeLatLng?.let {
                val prefs = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
                prefs.edit()
                    .putFloat("latitud", it.latitude.toFloat())
                    .putFloat("longitud", it.longitude.toFloat())
                    .apply()
                Toast.makeText(requireContext(), "Zona segura guardada", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            } ?: Toast.makeText(requireContext(), "Selecciona una ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

        map.setOnMapClickListener { latLng ->
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Zona segura"))
            safeLatLng = latLng
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val place = Autocomplete.getPlaceFromIntent(data)
            place.latLng?.let {
                map.clear()
                map.addMarker(MarkerOptions().position(it).title(place.name))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
                safeLatLng = it
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }
}
*/

