package com.example.dai.ledcontroller

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_color_picker.*

class ColorPickerActivity : AppCompatActivity() {

    companion object {
        const val ledOffValueR = "000"
        const val ledOffValueG = "000"
        const val ledOffValueB = "000"
        const val ledOffValueLightness = "000"
        private lateinit var ip: String
        private lateinit var port: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT)

        colorPickerView.addOnColorSelectedListener {
            val red = "%03d".format(Color.red(it))
            val green = "%03d".format(Color.green(it))
            val blue = "%03d".format(Color.blue(it))

            val hsv = FloatArray(3)
            Color.colorToHSV(it, hsv)
            val lightness = "%03d".format((hsv[2] * 255).toInt())

            val led = LED(red, green, blue, lightness)
            val udp = Udp(ip, port.toInt())
            Log.d("sendData", led.getSendData)
            udp.udpSend(led.getSendData)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.led_off -> {
                val led = LED(ledOffValueR, ledOffValueG, ledOffValueB, ledOffValueLightness)
                val udp = Udp(ip, port.toInt())
                udp.udpSend(led.getSendData)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
