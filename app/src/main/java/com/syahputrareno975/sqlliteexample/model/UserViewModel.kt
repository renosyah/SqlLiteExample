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

    init {
        val userDao = AppUserDatabase.getDatabase(aplication,viewModelScope).userDao()
        repository = UserRepository(userDao)
    }

    fun getAllUser() : LiveData<List<UserModel>>{
        return repository.getAll()
    }
    fun getAllByName(nm : String) : LiveData<List<UserModel>>{
        return repository.getAllByName(nm)
    }
    fun getOne(id : Int) : LiveData<UserModel>{
        return repository.getOne(id)
    }
    fun add(user : UserModel) = viewModelScope.launch {
        repository.add(user)
    }
    fun delete(user : UserModel) = viewModelScope.launch {
        repository.delete(user)
    }
    fun update(user : UserModel) = viewModelScope.launch {
        repository.update(user)
    }
}