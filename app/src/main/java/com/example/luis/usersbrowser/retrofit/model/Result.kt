package com.example.luis.usersbrowser.retrofit.model

import java.io.Serializable

class Result(picture: Picture, email: String, phone: String, name: Name, login: Login) : Serializable {
    var picture: Picture? = picture
    var email: String? = email
    var phone: String? = phone
    var name: Name? = name
    var login: Login? = login

}