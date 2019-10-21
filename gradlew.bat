package com.syahputrareno975.simplecardbattle.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.syahputrareno975.simplecardbattle.R
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamController
import com.syahputrareno975.simplecardbattle.interfaces.LobbyStreamEvent
import com.syahputrareno975.simplecardbattle.model.card.CardModel
import com.syahputrareno975.simplecardbattle.model.player.PlayerModel
import com.syahputrareno975.simplecardbattle.model.playerWithCard.PlayerWithCardsModel
import com.syahputrareno975.simplecardbattle.model.room.RoomDataModel
import com.syahputrareno975.simplecardbattle.task.LobbyStreamTask
import com.syahputrareno975.simplecardbattle.ui.dialog.DialogCardInfo
import com.syahputrareno975.simplecardbattle.util.NetDefault.Companion.NetConfigDefault
import com.syahputrareno975.simplecardbattle.util.SerializableSave
import com.syahputrareno975.simpleuno.adapter.AdapterCard
import com.syahputrareno975.simpleuno.adapter.AdapterPlayer
import com.syahputrareno975.simpleuno.adapter.AdapterRoom
import kotlinx.android.synthetic.main.activity_lobby.*



class MainLobbyActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var IntentData : Intent

    var player = PlayerWithCardsModel()
    lateinit var adapterDeckProfile : AdapterCard
    lateinit var adapterReserveProfile : AdapterCard


    var cardShop = ArrayList<CardModel>()
    lateinit var adapterShop : AdapterCard
    lateinit var adapterReserve : AdapterCard


    lateinit var controller: LobbyStreamController


    lateinit var waiting : ProgressDialog
    lateinit var leaving : ProgressDialog
    lateinit var searchingBattle : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        ini