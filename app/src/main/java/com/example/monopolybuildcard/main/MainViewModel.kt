package com.example.monopolybuildcard.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.GlobalCardData
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
//                val listPlayerData = mutableListOf<PlayerData>()
//
//                snapshot.children.forEach { playerData ->
//                    playerData.getValue(RoomData::class.java)?.let { listPlayerData.add(it) }
//                }

                _roomDataData.value = snapshot.getValue(RoomData::class.java)
                println("check is host")
                println(snapshot.getValue(RoomData::class.java)?.users?.get(0))
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
            val firstSharedCard = roomData.cards?.ready?.removeAt(0) ?: GlobalCardData()
            val secondSharedCard = roomData.cards?.ready?.removeAt(0) ?: GlobalCardData()
            roomData.users?.get(i)?.cards?.add(firstSharedCard)
            roomData.users?.get(i)?.cards?.add(secondSharedCard)
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
            roomData.users?.get(currentPlayerIndex)?.shouldRunning = true
        } else {
            currentPlayerIndex = 0
            roomData.users?.get(currentPlayerIndex)?.shouldRunning = true
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

    override fun onCleared() {
        FirebaseDatabase.getInstance().reference.removeValue(onRoomMovingListener)
        super.onCleared()
    }
}