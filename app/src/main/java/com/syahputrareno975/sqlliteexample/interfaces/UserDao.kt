package com.syahputrareno975.sqlliteexample.interfaces

import androidx.lifecycle.LiveData
import androidx.room.*
import com.syahputrareno975.sqlliteexample.model.UserModel

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE name LIKE :nm ")
    fun getAllByName(nm: String): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE uid = :id LIMIT 1")
    fun getOne(id : Int): LiveData<UserModel>

    @Insert
    suspend fun add(user: UserModel)

    @Update
    suspend fun update(user: UserModel)

    @Delete
    suspend fun delete(user: UserModel)
}