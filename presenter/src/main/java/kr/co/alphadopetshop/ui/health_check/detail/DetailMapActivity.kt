package kr.co.alphadopetshop.ui.health_check.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.ActivityDetailMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

class DetailMapActivity : AppCompatActivity() {
    private var binding : ActivityDetailMapBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_map)

        binding!!.txtTitle.text = intent.getStringExtra("hospitalName")

        binding!!.btnClose.setOnClickListener {
            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.mapView.removeAllViews()
                finish()
            },300)
        }

        setMap(intent.getDoubleExtra("latitude", 0.0), intent.getDoubleExtra("longitude", 0.0))
    }

    private fun setMap(latitude: Double, longitude: Double) {

        var mapView = MapView(this)
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true)


        val marker = MapPOIItem()
        marker.apply {
            itemName = intent.getStringExtra("hospitalName")
            mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude)
            markerType = MapPOIItem.MarkerType.CustomImage
            customImageResourceId = R.drawable.icon_location_purple_18
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            customSelectedImageResourceId = R.drawable.icon_location_purple_24

        }
        mapView.addPOIItem(marker)

        binding!!.mapView.addView(mapView)
    }

    override fun onBackPressed() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding!!.mapView.removeAllViews()
            finish()
        },300)


    }
}