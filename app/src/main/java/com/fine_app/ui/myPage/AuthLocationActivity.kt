package com.fine_app.ui.myPage

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.fine_app.databinding.ActivityAuthLocationBinding
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.util.*
import kotlin.properties.Delegates

class AuthLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthLocationBinding
    lateinit var userInfo: SharedPreferences
    var userId by Delegates.notNull<Long>()

    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    private var presentLocation = ""
    private var present = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userInfo = getSharedPreferences("userInfo", MODE_PRIVATE)
        userId = userInfo.getString("userInfo", "2")!!.toLong()

        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // 버튼 이벤트를 통해 현재 위치 찾기
        binding.authPresentLocationBt.setOnClickListener {
            if (checkPermissionForLocation(this)) {
                startLocationUpdates()
            }
        }

        binding.authLocationOk.setOnClickListener {
            verifyLocationAuth()
            finish()
        }
    }

    private fun verifyLocationAuth() {
        val residenceDto = ResidenceDto(
            userResidence = binding.authLocation.text.toString(),
            updateDate = LocalDateTime.now()
        )
        val call: Call<Long> = ServiceCreator.service.verifyLocationAuth(userId, residenceDto)

        call.enqueue(object : Callback<Long> {
            override fun onResponse(call: Call<Long>, response: Response<Long>) {
                if (response.isSuccessful) {

                    //Toast.makeText(this@AuthLocationActivity, "인증 성공", Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this@AuthLocationActivity, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Long>, t: Throwable) {
                Toast.makeText(this@AuthLocationActivity, "서버 연결 실패", Toast.LENGTH_SHORT).show()

            }
        })
        finish()
    }


    private fun startLocationUpdates() {
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        //주소 초기화
        var address: List<String> = listOf("서울특별시", "중구", "명동")

        // Got last known location. In some rare situations this can be null.
        var geocoder = Geocoder(this, Locale.KOREA)
        if (location != null) {
            val addrList =
                geocoder.getFromLocation(location.latitude, location.longitude, 1)

            for (addr in addrList) {
                val splitedAddr = addr.getAddressLine(0).split(" ")
                address = splitedAddr
            }

            // 현재 주소
            presentLocation = address[1] + " " + address[2] + " " + address[3] + " " + address[4]
            present = address[1] + " " + address[2]
            binding.authPresentLocationEt.setText(presentLocation)
            binding.authLocation.setText(present)
        }

    }


    // 위치 권한이 있는지 확인하는 메서드
    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 권한이 없으므로 권한 요청 알림 보내기
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
                false
            }
        } else {
            true
        }
    }

    // 사용자에게 권한 요청 후 결과에 대한 처리 로직
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()

            } else {
                Log.d("ttt", "onRequestPermissionsResult() _ 권한 허용 거부")
                Toast.makeText(this, "권한이 없어 해당 기능을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}