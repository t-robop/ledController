package com.example.dai.ledcontroller

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val sharedPreferences = getSharedPreferences("UdpSetting", MODE_PRIVATE)

        val ip = sharedPreferences.getString("ip", "")
        val port = sharedPreferences.getString("port", "")

        ip?.let { edit_ip.setText(ip) }
        port?.let { edit_port.setText(port) }

        save_setting.setOnClickListener {
            val ipText = edit_ip.text.toString()
            val portText = edit_port.text.toString()

            if (ipText.isNotEmpty() && portText.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                editor.putString("ip", ipText)
                editor.putString("port", portText)
                editor.apply()
            }

            finish()
        }
    }
}
