package com.example.messengerproject.models

data class CommonModel(
    val id:String = "",
    var username:String = "",
    var fullname:String = "",
    var phone:String ="",
    var photoUrl:String = "default",

    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",

    var lastMessage: String = ""
)

