package com.hellfish.evemento.event.transport

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hellfish.evemento.EventViewModel
import com.hellfish.evemento.NavigatorFragment
import com.hellfish.evemento.R
import kotlinx.android.synthetic.main.fragment_transport.*
import android.support.v4.content.ContextCompat
import android.support.annotation.DrawableRes
import com.hellfish.evemento.SessionManager
import com.hellfish.evemento.api.User


class TransportFragment : NavigatorFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    override val titleId = R.string.title_activity_maps

    private lateinit var eventViewModel: EventViewModel

    var transports: List<TransportItem> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.loadRides { _ -> showToast(R.string.errorLoadingRides) }
        eventViewModel.rides.observe(this, Observer { rides ->
            transports = rides ?: ArrayList()
            if (::mMap.isInitialized) loadTransportsOnMap()
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_transport, container, false)

    private lateinit var loggedInUser: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cardView.setOnClickListener { navigatorListener.replaceFragment(TransportListFragment()) }
        this.loggedInUser = SessionManager.getCurrentUser()!!


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun loadTransportsOnMap() {
        transports.forEach {
            val marker = MarkerOptions()
                    .position(it.latLong())
                    .title(it.startpoint.name)
                    .icon(bitmapDescriptorFromVector(context!!, R.drawable.ic_map_car_white_30dp))

            mMap.addMarker(marker)

        }
        val currentLocation = transports.firstOrNull()?.latLong() ?: getLastLocation()

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

        mMap.setOnMarkerClickListener { marker ->
            val transportClicked = transports.find { it.latLong().equals(marker.position) }
            if (transportClicked != null) {
                val transportDetailFragment = TransportDetailFragment()
                val args = Bundle()
                args.putParcelable("driver", transportClicked.driver)
                transportDetailFragment.arguments = args
                navigatorListener.replaceFragment(transportDetailFragment)
            }
            return@setOnMarkerClickListener false
        }
    }

    private fun getLastLocation() = LatLng(2.0, 2.0)

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        loadTransportsOnMap()
    }

    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes vectorDrawableResourceId: Int): BitmapDescriptor {
        val background = ContextCompat.getDrawable(context, R.drawable.ic_place_primary_blue_48dp)
        background!!.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable!!.setBounds(25, 3, vectorDrawable.intrinsicWidth + 20, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        background.draw(canvas)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
