package com.example.luis.usersbrowser.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM user_entity")
    fun getAllUsers(): MutableList<UserEntity>

    @Insert
    fun addUser(userEntity : UserEntity)

    @Query("SELECT COUNT(username) FROM user_entity WHERE username = :id")
    fun isUserFavorite(id: String): Int

    @Query("DELETE FROM user_entity WHERE username = :username")
    fun deleteUser(username: String)

    @Query("SELECT * FROM user_entity WHERE username = :username")
    fun getUser(username: String): UserEntity

}