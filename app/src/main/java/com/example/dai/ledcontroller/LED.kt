package com.example.dai.ledcontroller

data class LED(
    var colorR: String,
    var colorG: String,
    var colorB: String,
    var brightness: String
) {
    val getSendData: String
        get() = "$colorR$colorG$colorB$brightness"
}