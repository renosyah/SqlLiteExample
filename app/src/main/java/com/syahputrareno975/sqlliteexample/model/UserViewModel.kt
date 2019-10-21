package com.syahputrareno975.sqlliteexample.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.syahputrareno975.sqlliteexample.db.AppUserDatabase
import com.syahputrareno975.sqlliteexample.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(aplication : Application) : AndroidViewModel(aplication){
    private val repository : UserRepository

    // add more crud
    val allUser : LiveData<List<UserModel>>

    init {
        val userDao = AppUserDatabase.getDatabase(aplication,viewModelScope).userDao()
        repository = UserRepository(userDao)

        // add more crud
        allUser = repository.allUser
    }


    fun insert(user : UserModel) = viewModelScope.launch {
        repository.insert(user)
    }

    fun delete(user : UserModel) = viewModelScope.launch {
        repository.delete(user)
    }
}