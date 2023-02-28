package com.example.monopolybuildcard.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.Util.drawCard
import com.example.monopolybuildcard.player.PlayerStatus
import com.example.monopolybuildcard.room.RoomStatus
import com.google.firebase.database.*

class MainViewModel : ViewModel() {
    private val _roomDataData = MutableLiveData(RoomData())
    val roomDataData: LiveData<RoomData>
        get() = _roomDataData

    private val _isSuccessful = MutableLiveData(true)
    val isSuccessful: LiveData<Boolean>
        get() = _isSuccessful

    var onRoomMovingListener = DatabaseReference.CompletionListener { _, _ -> }

    fun updatePlayer(roomName: String) {
        val roomReference = FirebaseDatabase.getInstance().reference
            .child(Constant.ROOMS)
            .child(roomName)

        roomReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _roomDataData.value = snapshot.getValue(RoomData::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun playTheGame(roomName: String, roomData: RoomData) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        roomData.status = RoomStatus.STARTED
        roomData.users?.get(0)?.status = PlayerStatus.RUNNING

        for (i in 0 until roomData.users!!.size) {
            drawCard(roomData, i)
            drawCard(roomData, i)
            if (i == 0) {
                drawCard(roomData, i)
                drawCard(roomData, i)
            }
        }

        val roomFields = HashMap<String, Any>()
        roomFields[roomName] = roomData

        FirebaseDatabase.getInstance().reference.child(Constant.ROOMS)
            .updateChildren(roomFields, onRoomMovingListener)
    }

    fun nextPlayerTurn(roomName: String, roomData: RoomData) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        var currentPlayerIndex = roomData.users?.indexOfFirst { playerData ->
            playerData.status == PlayerStatus.RUNNING
        } ?: -1
        roomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.WAITING

        val totalUsers = (roomData.users!!.size - 1)
        if (currentPlayerIndex < totalUsers) {
            currentPlayerIndex += 1
        } else {
            currentPlayerIndex = 0
        }

        roomData.users?.get(currentPlayerIndex)?.status = PlayerStatus.RUNNING

        drawCard(roomData, currentPlayerIndex)
        drawCard(roomData, currentPlayerIndex)

        roomData.actions?.clear()

        val roomFields = HashMap<String, Any>()
        roomFields[roomName] = roomData

        FirebaseDatabase.getInstance().reference.child(Constant.ROOMS).updateChildren(
            roomFields,
            onRoomMovingListener
        )
    }

    fun postACard(
        roomName: String,
        roomData: RoomData,
        actionCards: MutableList<GlobalActionData>
    ) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        roomData.actions?.addAll(actionCards)

        val roomFields = HashMap<String, Any>()
        roomFields[roomName] = roomData

        FirebaseDatabase.getInstance().reference
            .child(Constant.ROOMS)
            .updateChildren(
                roomFields,
                onRoomMovingListener
            )
    }
}