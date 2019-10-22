package com.syahputrareno975.sqlliteexample.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
class UserModel{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var Uid: Int = 0

    @ColumnInfo(name = "name")
    var Name: String = ""

    @ColumnInfo(name = "phone_number")
    var PhoneNumber: String = ""

    // only one primary constructor
    // added secondary constructor
    // will error the kotlin kapt
    constructor(Name: String, PhoneNumber: String) {
        this.Name = Name
        this.PhoneNumber = PhoneNumber
    }

    companion object {
        fun newUser(Uid: Int, Name: String, PhoneNumber: String) : UserModel {
            val u = UserModel(Name,PhoneNumber)
            u.Uid = Uid
            return u
        }
    }
}