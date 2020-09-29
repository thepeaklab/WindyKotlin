package de.rustyhamsterr.windydemo.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.thepeaklab.module.windykotlin.core.models.WindyEventContent
import com.thepeaklab.module.windykotlin.view.WindyEventHandler

class MainViewModel : ViewModel(), WindyEventHandler {
    override fun onEvent(event: WindyEventContent) {
        Log.d("WindyMapView", "--> onEvent main \n$event")

        when (event.name) {
            WindyEventContent.EventName.initialize,
            WindyEventContent.EventName.zoomstart,
            WindyEventContent.EventName.zoomend,
            WindyEventContent.EventName.movestart,
            WindyEventContent.EventName.moveend,
            WindyEventContent.EventName.zoom,
            WindyEventContent.EventName.move ,
            WindyEventContent.EventName.markerclick -> {}
        }
    }
    // TODO: Implement the ViewModel
}