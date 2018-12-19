package com.example.dai.ledcontroller

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class ShakeActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        private lateinit var sensorManager: SensorManager
        private lateinit var ip: String
        private lateinit var port: String
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
            Log.d("accelerometer", "$sensorX $sensorY $sensorZ")

            //TODO 加速度がどう変わったら何を送るか決める

            val udp = Udp(ip, port.toInt())

        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
