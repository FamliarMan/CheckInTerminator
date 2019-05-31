package com.jianglei.checkinterminator.location

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.jianglei.checkinterminator.R
import com.baidu.location.LocationClientOption
import com.baidu.location.LocationClient
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.jianglei.checkinterminator.BaseActivity
import com.jianglei.checkinterminator.util.DialogUtils
import com.jianglei.smoothatyoperator.OnPermissionResultListener
import com.jianglei.smoothatyoperator.SmoothAtyOperator
import kotlinx.android.synthetic.main.activity_location_select.*


class LocationSelectActivity : BaseActivity() {


    private lateinit var mLocationClient: LocationClient
    private var addr: String? = null
    private var lat: Double? = null
    private var lng: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        mapInit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item == null) {
            return super.onOptionsItemSelected(item)
        }
        if (item.itemId == R.id.menuConfirm) {
            if (addr.isNullOrBlank()) {
                DialogUtils.showTipDialog(this, getString(R.string.locate_not_finished))
            } else {
                val intent = Intent()
                intent.putExtra("addr", addr)
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
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

                addr = location.addrStr
                lat = location.latitude
                lng = location.longitude

                Log.d(
                    "longyi",
                    "lat:" + location.latitude + "  lon:" + location.longitude + " add:" + location.addrStr
                )

            }
        })
        mapView.map.setOnMapLongClickListener {
            val locData = MyLocationData.Builder()
                .latitude(it.latitude)
                .longitude(it.longitude)
                .accuracy(50f)
                .build()
            mapView.map.setMyLocationData(locData)
            lat = it.latitude
            lng = it.longitude
        }

        SmoothAtyOperator.startPermission(this)
            .addPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .addPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            .build()
            .request(object : OnPermissionResultListener {
                override fun onGranted(permissions: Array<out String>?) {
                    mLocationClient.start()
                }

                override fun onDenied(permissions: Array<out String>?) {
                    DialogUtils.showTipDialog(this@LocationSelectActivity, getString(R.string.no_permission))
                }
            })
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
