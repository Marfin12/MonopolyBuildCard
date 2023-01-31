package com.example.monopolybuildcard.room

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolybuildcard.main.MainActivity
import com.example.monopolybuildcard.databinding.ActivityRoomBinding

class RoomActivity : AppCompatActivity() {
    companion object {
        fun launch(activity: Activity) {
            val intent = Intent(activity, RoomActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var binding: ActivityRoomBinding
    private lateinit var roomViewModel: RoomViewModel
    private lateinit var roomAdapter: RoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponent()
        initViewModel()
    }

    private fun initViewModel() {
        roomViewModel = ViewModelProvider(this)[RoomViewModel::class.java]

        roomViewModel.roomList.observe(this) { roomList ->
            roomAdapter.addRoom(roomList)
        }

        roomViewModel.roomResponseData.observe(this) { roomData ->
            if (roomData.isSuccessful)
                MainActivity.launch(this@RoomActivity, roomData.roomName)
        }

        roomViewModel.updateRoomList()
    }

    private fun initComponent() {
        roomAdapter = RoomAdapter(mutableListOf())
        roomAdapter.onItemClick = { roomName ->
            roomViewModel.joinRoom(roomName, this@RoomActivity)
        }

        binding.rvRoomList.adapter = roomAdapter
        binding.rvRoomList.layoutManager = LinearLayoutManager(this)
    }

}