package com.syahputrareno975.sqlliteexample.repository

import androidx.lifecycle.LiveData
import com.syahputrareno975.sqlliteexample.interfaces.UserDao
import com.syahputrareno975.sqlliteexample.model.UserModel

class UserRepository(private val userDao: UserDao) {


    fun getAll() : LiveData<List<UserModel>> {
       return userDao.getAll()
    }

    fun getAllByName(nm : String) : LiveData<List<UserModel>> {
        return userDao.getAllByName(nm)
    }

    fun getOne(id : Int) : LiveData<UserModel> {
        return userDao.getOne(id)
    }

    suspend fun add(user : UserModel){
        userDao.add(user)
    }

    suspend fun delete(user : UserModel){
        userDao.delete(user)
    }

    suspend fun update(user : UserModel){
        userDao.update(user)
    }
}