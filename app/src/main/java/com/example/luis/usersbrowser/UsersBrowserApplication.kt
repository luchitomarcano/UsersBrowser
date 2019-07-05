package com.example.luis.usersbrowser

import android.app.Application
import androidx.room.Room
import com.example.luis.usersbrowser.db.UsersDatabase


class UsersBrowserApplication: Application() {

    companion object {
        lateinit var database: UsersDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database =  Room.databaseBuilder(this, UsersDatabase::class.java, "users-db").build()
    }
}
