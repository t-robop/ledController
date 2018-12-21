package com.example.dai.ledcontroller

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_color_picker.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ColorPickerActivity : AppCompatActivity(), CoroutineScope {

    companion object {
        const val ledOffValueR = "000"
        const val ledOffValueG = "000"
        const val ledOffValueB = "000"
        const val ledOffValueLightness = "000"
        private lateinit var ip: String
        private lateinit var port: String

        private lateinit var job: Job
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_picker)

        ip = intent.getStringExtra(StartActivity.INTENT_KEY_IP)
        port = intent.getStringExtra(StartActivity.INTENT_KEY_PORT)

        job = Job()

        colorPickerView.addOnColorSelectedListener {
            val red = "%03d".format(Color.red(it))
            val green = "%03d".format(Color.green(it))
            val blue = "%03d".format(Color.blue(it))

            val hsv = FloatArray(3)
            Color.colorToHSV(it, hsv)
            val lightness = "%03d".format((hsv[2] * 255).toInt())

            val led = LED(red, green, blue, lightness)
            send(led)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun send(led: LED) = launch {
        picker_progressbar.visibility = android.widget.ProgressBar.VISIBLE
        picker_progressbar.bringToFront()
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

        picker_progressbar.visibility = android.widget.ProgressBar.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.led_off -> {
                val led = LED(ledOffValueR, ledOffValueG, ledOffValueB, ledOffValueLightness)
                send(led)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
