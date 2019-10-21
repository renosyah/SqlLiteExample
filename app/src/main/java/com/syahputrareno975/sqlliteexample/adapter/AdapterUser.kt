package com.syahputrareno975.sqlliteexample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.syahputrareno975.sqlliteexample.R
import com.syahputrareno975.sqlliteexample.model.UserModel

class AdapterUser : RecyclerView.Adapter<AdapterUser.ViewHolder> {

    private var context : Context
    private var objects : ArrayList<UserModel> = ArrayList()
    private var mInflater: LayoutInflater
    private lateinit var onCardClick : (UserModel) -> Unit

    constructor(context: Context) {
        this.context = context
        this.mInflater = LayoutInflater.from(context)
    }

    fun setUsers(objects : List<UserModel>){
        this.objects.clear()
        for (i in objects){
            this.objects.add(i)
        }
        notifyDataSetChanged()
    }

    fun setOnUserClick(onCardClick : (UserModel)  -> Unit){
        this.onCardClick = onCardClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.adapter_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = objects.get(position)

        holder.Name.setText("Number : ${item.Uid} Name : ${item.Name}\nPhone Number : ${item.PhoneNumber}")
        holder.Name.setOnClickListener{
            onCardClick.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var Name  : TextView

        constructor(itemView: View) : super(itemView) {
            this.Name = itemView.findViewById(R.id.name)
        }
    }
}