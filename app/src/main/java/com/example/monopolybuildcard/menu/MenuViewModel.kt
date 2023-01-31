package com.example.monopolybuildcard.menu

import android.content.Context
import android.os.Build.MODEL
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant.CARDS
import com.example.monopolybuildcard.Constant.ROOMS
import com.example.monopolybuildcard.Constant.RoomFields
import com.example.monopolybuildcard.Constant.RoomFields.PlayerFields
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.Util
import com.google.firebase.database.*


class MenuViewModel : ViewModel() {
    private val roomNameLength = 8

    private val _menuData = MutableLiveData(MenuData(
        null, null
    ))
    val menuData: LiveData<MenuData>
        get() = _menuData

    private var completionListener: DatabaseReference.CompletionListener? = null
    private var cardOnDeck = mutableListOf<GlobalCardData>()

    fun createRoom(context: Context) {
        val roomName = getRandomString()
        val roomFields = HashMap<String, Any>()
        val playerFields = HashMap<String, Any>()
        val playerListField = HashMap<String, Any>()
        val cardListField = HashMap<String, Any>()

        playerFields[PlayerFields.ID] = Util.getAndroidId(context)
        playerFields[PlayerFields.SHOULD_HOST] = true
        playerFields[PlayerFields.SHOULD_RUNNING] = false
        playerFields[PlayerFields.NAME] = MODEL

        playerListField["0"] = playerFields
        cardListField["ready"] = cardOnDeck

        roomFields[RoomFields.CARDS] = cardListField
        roomFields[RoomFields.MAX_PLAYER] = 2
        roomFields[RoomFields.STATUS] = "waiting"
        roomFields[RoomFields.USERS] = playerListField

        completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
            _menuData.value = MenuData(databaseError == null, roomName)
        }

        FirebaseDatabase.getInstance().reference
            .child(ROOMS)
            .child(roomName)
            .updateChildren(roomFields, completionListener)
    }

    fun initCardsFromFirebase() {
        val cardsReference = FirebaseDatabase.getInstance().reference.child(CARDS)

        cardsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { playerData ->
                    playerData.getValue(GlobalCardData::class.java)?.let {
                        cardOnDeck.add(it)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
//        var cluster = 4
//        var asciiValue = 98
//        val cardListField = HashMap<String, Any>()
//        for(i in 0..4) {
//            val money = i+1
//            val cardName = "m${money}"
//            val cardData = GlobalCardData(
//                cardName, "money"
//            )
//            cardListField[i.toString()] = cardData
//        }
//        val cardName10 = "m10"
//        val cardName20 = "m20"
//        cardListField["5"] = GlobalCardData(
//            cardName10, "money"
//        )
//        cardListField["6"] = GlobalCardData(
//            cardName20, "money"
//        )
//        for(i in 7..12) {
//            val action = i-6
//            val cardName = "a${action}"
//            cardListField[i.toString()] = GlobalCardData(
//                cardName, "action"
//            )
//        }
//        for(i in 13..99) {
//            if (cluster > 3) {
//                cluster = 1
//                if (asciiValue == 108) asciiValue+=2
//                else asciiValue++
//            } else cluster++
//            val cardName = "${asciiValue.toChar()}${cluster}"
//            cardListField[i.toString()] = GlobalCardData(
//                cardName, "asset"
//            )
//        }
//        FirebaseDatabase.getInstance().reference
//            .child(CARDS)
//            .updateChildren(cardListField)
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..roomNameLength)
            .map { allowedChars.random() }.shuffled()
            .joinToString("")
    }


    override fun onCleared() {
        if (completionListener != null) {
            FirebaseDatabase.getInstance().reference.removeValue(completionListener)
        }

        super.onCleared()
    }
}