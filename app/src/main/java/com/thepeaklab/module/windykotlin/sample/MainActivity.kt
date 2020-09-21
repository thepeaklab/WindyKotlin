package com.thepeaklab.module.windykotlin.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.thepeaklab.module.windykotlin.core.models.Coordinate
import com.thepeaklab.module.windykotlin.core.models.Icon
import com.thepeaklab.module.windykotlin.core.models.Marker
import com.thepeaklab.module.windykotlin.core.models.Point
import com.thepeaklab.module.windykotlin.core.models.WindyEventContent
import com.thepeaklab.module.windykotlin.core.models.WindyIcon
import com.thepeaklab.module.windykotlin.core.models.WindyInitOptions
import com.thepeaklab.module.windykotlin.core.models.WindyZoomPanOptions
import com.thepeaklab.module.windykotlin.sample.databinding.ActivityMainBinding
import com.thepeaklab.module.windykotlin.view.WindyEventHandler
import java.util.UUID


class MainActivity : AppCompatActivity(), WindyEventHandler {

    private lateinit var binding: ActivityMainBinding
    private val markerList = mutableListOf<Marker>()
    private val initOptions = WindyInitOptions(
        "YOUR-WINDY-API-KEY",
        true,
        53.528740,
        8.452565,
        5
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // create databinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // set eventhandler for events thrown by windy
        binding.windyMapView.setEventHandler(this)

        // init windyMapView
        binding.windyMapView.setOptions(initOptions)

        // example: pan to
        binding.btnMove.setOnClickListener {

            val lat = (1..165).random().toDouble()
            val lng = (1..80).random().toDouble()

            binding.windyMapView.panTo(Coordinate(lat, lng))
        }

        // example: set zoom
        binding.btnZoom.setOnClickListener {

            binding.windyMapView.setZoom(10, WindyZoomPanOptions(true))
        }

        // example: fit bounds
        binding.btnFitBounds.setOnClickListener {

            val coordinates = listOf(
                Coordinate(lat = 53.528740, lng = 8.452565),
                Coordinate(lat = 53.505438, lng = 8.476923),
                Coordinate(lat = 53.488712, lng = 8.410969),
                Coordinate(lat = 53.456613, lng = 8.477863),
                Coordinate(lat = 53.449079, lng = 8.425022),
                Coordinate(lat = 53.415919, lng = 8.468630),
                Coordinate(lat = 53.406520, lng = 8.394845)
            )

            binding.windyMapView.fitBounds(coordinates)
        }

        // example: get map center
        binding.btnMapCenter.setOnClickListener {

            binding.windyMapView.getMapCenter { coordinate ->

                coordinate?.let {
                    binding.contentInfo.text = "lat: ${it.lat}\nlng: ${it.lng}"
                } ?: let {
                    binding.contentInfo.text = "no coordinate"
                }
            }
        }

        // example: add marker
        binding.btnAddMarker.setOnClickListener {

            val uuid = UUID.randomUUID()

            val lat = (1..165).random().toDouble()
            val lng = (1..80).random().toDouble()

            val marker = Marker(
                uuid = uuid,
                coordinate = Coordinate(lat, lng),
                icon = WindyIcon(
//                    icon = Icon(asset = "dancing-banana.gif"),    // asset example
//                    icon = Icon(drawable = R.drawable.baseline_build_black_24),    // drawable example
                    icon = Icon(url = "https://image.flaticon.com/icons/svg/1397/1397898.svg"),  // url example
                    iconSize = Point(30, 40),
                    iconAnchor = Point(0, 0),
                    popupAnchor = Point(0, 0)
                )
            )
            markerList.add(marker)


            binding.windyMapView.addMarker(marker)
            binding.windyMapView.panTo(marker.coordinate) // move to generated marker
        }

        // example: remove marker
        binding.btnRemoveMarker.setOnClickListener {
            markerList.forEach { marker ->
                binding.windyMapView.removeMarker(marker.uuid)
            }
        }

        // reset view
        binding.btnReset.setOnClickListener {

            binding.windyMapView.setOptions(initOptions)
        }
    }

    /**
     * handle windy events
     *
     */
    override fun onEvent(event: WindyEventContent) {

        Log.d("WindyMapView", "--> onEvent main \n$event")

        var text = ""
        when (event.name) {
            WindyEventContent.EventName.initialize,
            WindyEventContent.EventName.zoomstart,
            WindyEventContent.EventName.zoomend,
            WindyEventContent.EventName.movestart,
            WindyEventContent.EventName.moveend,
            WindyEventContent.EventName.zoom,
            WindyEventContent.EventName.move -> text = "${event.name}"
            WindyEventContent.EventName.markerclick -> text =
                    "${event.name}: ${event.options?.uuid}"
        }
        setInfoText(text)
    }

    override fun onWebViewLoadingFinished() {
        Log.d("WindyMapView", "WebView onPageFinished")
    }


    private fun setInfoText(text: String) {

        runOnUiThread {
            binding.contentInfo.text = text
        }
    }
}