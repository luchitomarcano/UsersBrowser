package com.example.luis.usersbrowser.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "user_entity")
data class UserEntity(
        @PrimaryKey
        var username: String = "",
        var email: String = "",
        var name_first: String = "",
        var name_last: String = "",
        var phone: String = "",
        var picture_thumbnail: String = "",
        var picture_large: String = ""

)