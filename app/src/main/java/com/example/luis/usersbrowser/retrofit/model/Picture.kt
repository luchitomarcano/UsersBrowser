package com.example.luis.usersbrowser.retrofit.model

import java.io.Serializable

class Picture(thumbnail: String, large: String) : Serializable {
    var thumbnail: String? = thumbnail
    var large: String? = large

}