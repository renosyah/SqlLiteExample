package com.syahputrareno975.sqlliteexample.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.syahputrareno975.sqlliteexample.model.UserModel

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): LiveData<List<UserModel>>

    @Query("SELECT * FROM user WHERE name LIKE :nm LIMIT 1")
    fun findByName(nm: String): LiveData<UserModel>

    @Insert
    suspend fun insertAll(vararg users: UserModel)

    @Delete
    suspend fun delete(user: UserModel)
}