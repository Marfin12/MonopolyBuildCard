package com.example.monopolybuildcard.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.asset.AssetData
import com.example.monopolybuildcard.card.CardType
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

    fun shareTheCard(roomName: String, roomData: RoomData) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        roomData.status = "started"
        roomData.users?.get(0)?.shouldRunning = true

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

        FirebaseDatabase.getInstance().reference
            .child(Constant.ROOMS)
            .updateChildren(
                roomFields,
                onRoomMovingListener
            )
    }

    fun nextPlayerTurn(roomName: String, roomData: RoomData) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        var currentPlayerIndex = roomData.users?.indexOfFirst { playerData ->
            playerData.shouldRunning == true
        } ?: -1
        roomData.users?.get(currentPlayerIndex)?.shouldRunning = false

        val totalUsers = (roomData.users!!.size - 1)
        if (currentPlayerIndex < totalUsers) {
            currentPlayerIndex += 1
        } else {
            currentPlayerIndex = 0
        }

        roomData.users?.get(currentPlayerIndex)?.shouldRunning = true

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
        whichCard: GlobalCardData
    ) {
        onRoomMovingListener = DatabaseReference.CompletionListener { databaseError, _ ->
            if (databaseError != null) _isSuccessful.value = false
        }

        roomData.actions?.add(whichCard)

        val roomFields = HashMap<String, Any>()
        roomFields[roomName] = roomData

        FirebaseDatabase.getInstance().reference
            .child(Constant.ROOMS)
            .updateChildren(
                roomFields,
                onRoomMovingListener
            )
    }

    private fun drawCard(roomData: RoomData, currentPlayerIndex: Int) {
        val totalReadyCards = roomData.cards?.ready?.size ?: 0
        if (totalReadyCards > 0) {
            val sharedCard = roomData.cards?.ready?.removeAt(0) ?: GlobalCardData()
            roomData.users?.get(currentPlayerIndex)?.cards?.add(sharedCard)
        }
    }
}