package com.example.dai.ledcontroller

data class LED(
    val colorR: String,
    val colorG: String,
    val colorB: String,
    val brightness: String
) {
    val getSendData: String
        get() = "$colorR$colorG$colorB$brightness"
}