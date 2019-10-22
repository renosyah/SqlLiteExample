package com.syahputrareno975.sqlliteexample.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
        userViewModel.getAllUser().observe(context as LifecycleOwner, Observer {
            it.let {
                adapterUser.setUsers(it)
            }
        })

        add_user.setOnClickListener(onAddUser)
        find_user.addTextChangedListener(onFindUser)
    }

    val onAddUser = object : View.OnClickListener {
        override fun onClick(v: View?) {
            dialogInsert()
        }
    }

    val onFindUser = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            userViewModel.getAllByName("%${s.toString()}%").observe(context as LifecycleOwner, Observer {
                it.let {
                    adapterUser.setUsers(it)
                }
            })
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
            .setPositiveButton("Edit") { dialog, which ->
                dialogEdit(item)
                dialog.dismiss()
            }
            .setNeutralButton("Delete") { dialog, which ->
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

        val title : TextView = v.findViewById(R.id.title)
        title.setText("Add New User")

        val name : EditText = v.findViewById(R.id.add_user_name)
        val phone : EditText = v.findViewById(R.id.add_user_phone)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Add") { dialog, which ->
                userViewModel.add(UserModel(name.text.toString(),phone.text.toString()))
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, which ->
                dialog.dismiss()
            }.create()

        dialog.setView(v)
        dialog.setCancelable(false)
        dialog.show()
    }

    fun dialogEdit(item : UserModel) {
        val v = (context as Activity).layoutInflater.inflate(R.layout.dialog_add,null)

        val title : TextView = v.findViewById(R.id.title)
        title.setText("Edit User : ${item.Name}")

        val name : EditText = v.findViewById(R.id.add_user_name)
        name.setText(item.Name)

        val phone : EditText = v.findViewById(R.id.add_user_phone)
        phone.setText(item.PhoneNumber)

        val dialog = AlertDialog.Builder(context)
            .setPositiveButton("Update") { dialog, which ->
                userViewModel.update(UserModel.newUser(item.Uid,name.text.toString(),phone.text.toString()))
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
