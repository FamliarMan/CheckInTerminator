package com.jianglei.checkinterminator.location

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.jianglei.checkinterminator.R
import com.baidu.location.LocationClientOption
import com.baidu.location.LocationClient
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.activity_location_select.*


class LocationSelectActivity : AppCompatActivity() {

    private lateinit var mLocationClient: LocationClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        mapInit()
    }

    private fun mapInit() {
        //定位初始化
        mLocationClient = LocationClient(this)

        //通过LocationClientOption设置LocationClient相关参数
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setIsNeedAddress(true)

        //设置locationClientOption
        mLocationClient.locOption = option
        mapView.map.isMyLocationEnabled = true

        //注册LocationListener监听器
        mLocationClient.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(location: BDLocation?) {
                if (location == null || mapView == null) {
                    return
                }

                val locData = MyLocationData.Builder()
                    .accuracy(location.radius)
                    .direction(location.direction)
                    .latitude(location.latitude)
                    .longitude(location.longitude)
                    .build()
                mapView.map.setMyLocationData(locData)
                val f = mapView.map.getMaxZoomLevel()//19.0 最小比例尺
                val m = mapView.map.getMinZoomLevel()//3.0 最大比尺
                val ll = LatLng(location.latitude, location.longitude)//屏幕显示时以这个点为中心
                val u = MapStatusUpdateFactory.newLatLngZoom(ll, f - 2)
//MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,m);//设置缩放比例
                mapView.map.animateMapStatus(u)//屏幕显示地图时以指定LatLng为中心

                Log.d(
                    "longyi",
                    "lat:" + location.latitude + "  lon:" + location.longitude + " add:" + location.addrStr
                )

            }
        })

        mLocationClient.start()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mLocationClient.stop()
        mapView.map.isMyLocationEnabled = false
        mapView.onDestroy()
    }
}
