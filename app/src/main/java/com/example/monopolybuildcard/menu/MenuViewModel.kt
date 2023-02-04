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
import com.example.monopolybuildcard.card.CardType
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
        cardListField["ready"] = cardOnDeck.shuffled()

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
//        val cardsReference = FirebaseDatabase.getInstance().reference.child(CARDS)
//
//        cardsReference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                snapshot.children.forEach { playerData ->
//                    playerData.getValue(GlobalCardData::class.java)?.let {
//                        cardOnDeck.add(it)
//                    }
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
        var cluster = 4
        var asciiValue = 98
        val cardListField = HashMap<String, Any>()
        for(i in 0..4) {
            val money = i+1
            val cardName = "m${money}"
            val cardData = GlobalCardData(
                cardName, money, CardType.MONEY_TYPE, money
            )
            cardListField[i.toString()] = cardData
        }
        val cardName10 = "m10"
        val cardName20 = "m20"
        cardListField["5"] = GlobalCardData(
            cardName10, 10, CardType.MONEY_TYPE, 10
        )
        cardListField["6"] = GlobalCardData(
            cardName20, 20, CardType.MONEY_TYPE, 20
        )
        cardListField["7"] = GlobalCardData(
            "go_pass", 0, CardType.ACTION_TYPE, 2
        )
        cardListField["8"] = GlobalCardData(
            "deal_breaker", 0, CardType.ACTION_TYPE, 5
        )
        cardListField["9"] = GlobalCardData(
            "sly_deal", 0, CardType.ACTION_TYPE, 3
        )
        cardListField["10"] = GlobalCardData(
            "forced_deal", 0, CardType.ACTION_TYPE, 2
        )
//        cardListField["11"] = GlobalCardData(
//            "debt_collector", CardType.ACTION_TYPE, 3
//        )
//        cardListField["12"] = GlobalCardData(
//            "happy_birthday", CardType.ACTION_TYPE, 3
//        )
//        cardListField["13"] = GlobalCardData(
//            "say_no", CardType.ACTION_TYPE, 3
//        )
//        cardListField["14"] = GlobalCardData(
//            "rent_B", CardType.ACTION_TYPE, 2
//        )
//        cardListField["15"] = GlobalCardData(
//            "rent_C", CardType.ACTION_TYPE, 2
//        )
//        cardListField["16"] = GlobalCardData(
//            "rent_D", CardType.ACTION_TYPE, 2
//        )
//        cardListField["17"] = GlobalCardData(
//            "rent_E", CardType.ACTION_TYPE, 2
//        )
//        cardListField["18"] = GlobalCardData(
//            "rent_any", CardType.ACTION_TYPE, 3
//        )
        var price = 1
        for(i in 11..21) {
            if (cluster > 2) {
                cluster = 1
                if (asciiValue == 108) asciiValue+=2
                else asciiValue++
                price++
            } else cluster++
            val cardName = "${asciiValue.toChar()}"
            cardListField[i.toString()] = GlobalCardData(
                cardName, cluster, CardType.PROPERTY_TYPE, price
            )
        }
        FirebaseDatabase.getInstance().reference
            .child(CARDS)
            .updateChildren(cardListField)
    }

    private fun getRandomString(): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..roomNameLength)
            .map { allowedChars.random() }.shuffled()
            .joinToString("")
    }
}