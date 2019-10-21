package com.syahputrareno975.sqlliteexample.repository

import androidx.lifecycle.LiveData
import com.syahputrareno975.sqlliteexample.interfaces.UserDao
import com.syahputrareno975.sqlliteexample.model.UserModel

class UserRepository(private val userDao: UserDao) {

    // add more crud
    val allUser : LiveData<List<UserModel>> = userDao.getAll()


    suspend fun insert(user : UserModel){
        userDao.insertAll(user)
    }

    suspend fun delete(user : UserModel){
        userDao.delete(user)
    }
}