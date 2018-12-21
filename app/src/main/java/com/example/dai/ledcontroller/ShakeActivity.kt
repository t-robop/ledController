package com.example.dai.ledcontroller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlin.random.Random

class ShakeActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private lateinit var ip: String
        private lateinit var port: String

        private const val TIME_THRESHOLD = 200
        private const val SHAKE_TIMEOUT = 500
        private const val FORCE_THRESHOLD = 50
        private const val SHAKE_DURATION_TIME = 100
        private const val SHAKE_COUNT = 2

        private var lastSensorX = -1.0f
        private var lastSensorY = -1.0f
        private var lastSensorZ = -1.0f
        private var shakeCount: Int = 0
        private var lastTime: Long = 0
        private var lastForceTime: Long = 0
        private var lastShakeTime: Long = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

    }

    override fun onResume() {
        super.onResume()
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER){
            val sensorX = event.values[0]
            val sensorY = event.values[1]
            val sensorZ = event.values[2]
            //Log.d("accelerometer", "$sensorX $sensorY $sensorZ")

            //TODO 加速度がどう変わったら何を送るか決める
            val nowTime = System.currentTimeMillis()

            //最後に動かしてから500ミリ秒経過していたら、連続してないのでカウントリセット
            if ((nowTime - lastForceTime) > SHAKE_TIMEOUT){
                shakeCount = 0
            }

            if ((nowTime - lastTime) > TIME_THRESHOLD){
                val diff = nowTime - lastTime

                //10秒間でどれだけ加速したか
                val speed = Math.abs(sensorX + sensorY + sensorZ - lastSensorX - lastSensorY - lastSensorZ) / diff * 10000

                if (speed > FORCE_THRESHOLD){
                    if ((++shakeCount >= SHAKE_COUNT) && nowTime - lastShakeTime > SHAKE_DURATION_TIME){

                        val colorR = "%03d".format(Random.nextInt(10, 255))
                        val colorG = "%03d".format(Random.nextInt(10, 255))
                        val colorB = "%03d".format(Random.nextInt(10, 255))
                        val led = LED(colorR, colorG, colorB, "200")

                        val udp = Udp(ip, port.toInt())
                        Log.d("shake", led.getSendData)
                        udp.udpSend(led.getSendData)

                        lastShakeTime = nowTime
                        shakeCount = 0
                    }
                }
                lastForceTime = nowTime
            }
            lastTime = nowTime
            lastSensorX = sensorX
            lastSensorY = sensorY
            lastSensorZ = sensorZ

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
}
