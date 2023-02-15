package com.example.monopolybuildcard.room

import android.content.Context
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.Constant.ROOMS
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.player.PlayerStatus
import com.google.firebase.database.*


class RoomViewModel : ViewModel() {
    private val _roomList = MutableLiveData(mutableListOf<String>())
    val roomList: LiveData<MutableList<String>>
        get() = _roomList

    private val _roomResponseData = MutableLiveData(RoomResponseData())
    val roomResponseData: LiveData<RoomResponseData>
        get() = _roomResponseData

    var roomEventListener: ValueEventListener? = null
    var completionListener: DatabaseReference.CompletionListener? = null
    var onceJoined: Boolean = true

    fun updateRoomList() {
        val roomReference = FirebaseDatabase.getInstance().reference.child(ROOMS)
        roomReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val snapshotList = mutableListOf<String>()
                snapshot.children.forEach {
                    snapshotList.add(it.key.toString())
                }
                _roomList.value = snapshotList
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun joinRoom(roomName: String, context: Context) {
        completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
            _roomResponseData.value = RoomResponseData(databaseError == null, roomName)
        }

        roomEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (onceJoined) {
                    val playerIndex = snapshot.children.last().key?.toInt() ?: -1
                    val updatedPlayerIndex = playerIndex + 1

                    val playerFields = HashMap<String, Any>()

                    playerFields[Constant.RoomFields.PlayerFields.ID] = Util.getAndroidId(context)
                    playerFields[Constant.RoomFields.PlayerFields.SHOULD_HOST] = false
                    playerFields[Constant.RoomFields.PlayerFields.STATUS] = PlayerStatus.WAITING
                    playerFields[Constant.RoomFields.PlayerFields.NAME] = Build.MODEL

                    FirebaseDatabase.getInstance().reference
                        .child(ROOMS)
                        .child(roomName)
                        .child(Constant.RoomFields.USERS)
                        .child(updatedPlayerIndex.toString())
                        .updateChildren(playerFields, completionListener)
                    FirebaseDatabase.getInstance().reference.child(ROOMS).removeEventListener(this)
                    onceJoined = false
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        FirebaseDatabase.getInstance().reference
            .child(ROOMS)
            .child(roomName)
            .child(Constant.RoomFields.USERS).addValueEventListener(roomEventListener as ValueEventListener)
    }
}