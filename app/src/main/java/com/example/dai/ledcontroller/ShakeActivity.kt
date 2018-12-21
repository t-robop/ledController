package com.example.dai.ledcontroller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_shake.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class ShakeActivity : AppCompatActivity(), SensorEventListener, CoroutineScope {

    companion object {
        private lateinit var sensorManager: SensorManager
        private lateinit var ip: String
        private lateinit var port: String

        private const val TIME_THRESHOLD = 200
        private const val SHAKE_TIMEOUT = 500
        private const val FORCE_THRESHOLD = 100
        private const val SHAKE_DURATION_TIME = 1800
        private const val SHAKE_COUNT = 2

        private var lastSensorX = -1.0f
        private var lastSensorY = -1.0f
        private var lastSensorZ = -1.0f
        private var shakeCount: Int = 0
        private var lastTime: Long = 0
        private var lastForceTime: Long = 0
        private var lastShakeTime: Long = 0

        private lateinit var job: Job
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shake)

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT)

        job = Job()

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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val sensorX = event.values[0]
            val sensorY = event.values[1]
            val sensorZ = event.values[2]
            //Log.d("accelerometer", "$sensorX $sensorY $sensorZ")

            //TODO 加速度がどう変わったら何を送るか決める
            val nowTime = System.currentTimeMillis()

            //最後に動かしてから$SHAKE_TIMEOUTミリ秒経過していたら、連続してないのでカウントリセット
            if ((nowTime - lastForceTime) > SHAKE_TIMEOUT) {
                shakeCount = 0
            }

            //最後に振ってから$TIME_THRESHOLDミリ秒経ったら実行
            if ((nowTime - lastTime) > TIME_THRESHOLD) {
                val diff = nowTime - lastTime

                //10秒間でどれだけ加速したか
                val speed =
                    Math.abs(sensorX + sensorY + sensorZ - lastSensorX - lastSensorY - lastSensorZ) / diff * 10000

                if (speed > FORCE_THRESHOLD) {
                    if ((++shakeCount >= SHAKE_COUNT) && nowTime - lastShakeTime > SHAKE_DURATION_TIME) {

                        val colorR = "%03d".format(Random.nextInt(10, 255))
                        val colorG = "%03d".format(Random.nextInt(10, 255))
                        val colorB = "%03d".format(Random.nextInt(10, 255))
                        val led = LED(colorR, colorG, colorB, "200")

                        send(led)

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

    private fun send(led: LED) = launch {
        shake_progressbar.visibility = android.widget.ProgressBar.VISIBLE
        shake_progressbar.bringToFront()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        async(Dispatchers.IO) {
            val udp = Udp(ip, port.toInt())
            Log.d("sendData", led.getSendData)
            udp.udpSend(led.getSendData)
        }.await()

        delay(1800L)

        shake_progressbar.visibility = android.widget.ProgressBar.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
}
