package com.example.monopolybuildcard.menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.monopolybuildcard.main.MainActivity
import com.example.monopolybuildcard.room.RoomActivity
import com.example.monopolybuildcard.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuViewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        initComponent()
    }

    private fun initViewModel() {
        menuViewModel = ViewModelProvider(this)[MenuViewModel::class.java]
        menuViewModel.initCardsFromFirebase()

        menuViewModel.menuData.observe(this) { data ->
            if (data != null) {
                if (data.isSuccessful == true) {
                    data.roomName?.let { MainActivity.launch(this@MenuActivity, it) }
                } else if (data.isSuccessful == false) {
                    Toast.makeText(this, "Something wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initComponent() {
        binding.btnMenuCreateGame.setOnClickListener {
            menuViewModel.createRoom(this@MenuActivity)
        }
        binding.btnMenuJoinGame.setOnClickListener {
            RoomActivity.launch(this@MenuActivity)
        }
    }
}