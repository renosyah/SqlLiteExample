package com.syahputrareno975.sqlliteexample.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.syahputrareno975.sqlliteexample.R
import com.syahputrareno975.sqlliteexample.adapter.AdapterUser
import com.syahputrareno975.sqlliteexample.model.UserModel
import com.syahputrareno975.sqlliteexample.model.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var context : Context
    lateinit var adapterUser : AdapterUser
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initWidget()
    }

    fun initWidget(){
        this.context = this@MainActivity

        setAdapter()

        userViewModel = ViewModelProvider(context as ViewModelStoreOwner).get(UserViewModel::class.java)
        userViewModel.allUser.observe(context as LifecycleOwner, Observer {
            it.let {
                adapterUser.setUsers(it)
            }
        })

        add_user.setOnClickListener {
            dialogInsert()
        }
    }

    fun setAdapter(){
        adapterUser = AdapterUser(context)
        adapterUser.setOnUserClick {
            dialogOpsi(it)
        }
        list_user.adapter = adapterUser
        list_user.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun dialogOpsi(item : UserModel){

        AlertDialog.Builder(context)
            .setTitle(item.Name)
            .setMessage("Number : ${item.Uid} Name : ${item.Name}\nPhone Number : ${item.PhoneNumber}")
            .setPositiveButton("Delete") { dialog, which ->
                userViewModel.delete(item)
                dialog.dismiss()
            }
            .setNegativeButton("Back") { dialog, which ->
                dialog.dismiss()
            }.create()
            .show()
    }

    fun dialogInsert(){

        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_add,null)

        val name : EditText = v.findViewById(R.id.add_user_name)
        val phone : EditText = v.findViewById(R.id.add_user_phone)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Add") { dialog, which ->
                userViewModel.insert(UserModel(name.text.toString(),phone.text.toString()))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()

        dialog.setView(v)
        dialog.setCancelable(false)
        dialog.show()
    }
}
