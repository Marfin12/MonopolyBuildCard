package com.example.monopolybuildcard.menu

import android.content.Context
import android.os.Build.MODEL
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.Constant.CARDS
import com.example.monopolybuildcard.Constant.ROOMS
import com.example.monopolybuildcard.Constant.RoomFields
import com.example.monopolybuildcard.Constant.RoomFields.PlayerFields
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.Util.swap
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.player.PlayerStatus
import com.example.monopolybuildcard.room.RoomStatus
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
        playerFields[PlayerFields.STATUS] = PlayerStatus.WAITING
        playerFields[PlayerFields.NAME] = MODEL

        playerListField["0"] = playerFields
        if (cardOnDeck.size > 0) {
            // deal breaker, force deal, sly deal debt & happy birthday testing
//            cardOnDeck.swap(0, 8)
//            cardOnDeck.swap(1, 9)
//            cardOnDeck.swap(2, 10)
//            cardOnDeck.swap(4, 18)
//            cardOnDeck.swap(5, 19)
//            cardOnDeck.swap(6, 20)
            // rent, double the rent & say no test
//            cardOnDeck.swap(0, 19)
//            cardOnDeck.swap(1, 14)
//            cardOnDeck.swap(2, 18)
//            cardOnDeck.swap(3, 20)
//            cardOnDeck.swap(4, 15)
//            cardOnDeck.swap(5, 14)
//            cardOnDeck.swap(6, 25)
//            cardOnDeck.swap(7, 13)
            // wild card, flip & rent
            cardOnDeck.swap(0, 34)
            cardOnDeck.swap(1, 35)
            cardOnDeck.swap(2, 19)
            cardOnDeck.swap(3, 36)
            cardOnDeck.swap(4, 20)
            cardOnDeck.swap(5, 21)
            cardOnDeck.swap(6, 22)
            cardOnDeck.swap(7, 23)
            cardOnDeck.swap(8, 24)
            cardOnDeck.swap(9, 25)
            cardOnDeck.swap(10, 26)

            cardListField["ready"] = cardOnDeck

            roomFields[RoomFields.CARDS] = cardListField
            roomFields[RoomFields.MAX_PLAYER] = 2
            roomFields[RoomFields.STATUS] = RoomStatus.WAITING
            roomFields[RoomFields.USERS] = playerListField

            completionListener = DatabaseReference.CompletionListener { databaseError, _ ->
                _menuData.value = MenuData(databaseError == null, roomName)
            }

            FirebaseDatabase.getInstance().reference
                .child(ROOMS)
                .child(roomName)
                .updateChildren(roomFields, completionListener)
        } else _menuData.value = MenuData(false, roomName)
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
//        var asciiValue = 97
//        val cardListField = HashMap<String, Any>()
//        for(i in 0..4) {
//            val money = i+1
//            val cardName = "m"
//            val cardData = GlobalCardData(
//                cardName, money, CardType.MONEY_TYPE, money
//            )
//            cardListField[i.toString()] = cardData
//        }
//        cardListField["5"] = GlobalCardData(
//            "m", 10, CardType.MONEY_TYPE, 10
//        )
//        cardListField["6"] = GlobalCardData(
//            "m", 20, CardType.MONEY_TYPE, 20
//        )
//        cardListField["7"] = GlobalCardData(
//            CardName.ACTION_PASS_GO, 0, CardType.ACTION_TYPE, 2
//        )
//        cardListField["8"] = GlobalCardData(
//            CardName.ACTION_DEAL_BREAKER, 0, CardType.ACTION_TYPE, 5
//        )
//        cardListField["9"] = GlobalCardData(
//            CardName.ACTION_SLY_DEAL, 0, CardType.ACTION_TYPE, 3
//        )
//        cardListField["10"] = GlobalCardData(
//            CardName.ACTION_FORCED_DEAL, 0, CardType.ACTION_TYPE, 2
//        )
//        cardListField["11"] = GlobalCardData(
//            CardName.ACTION_DEBT_COLLECTOR, 5, CardType.ACTION_TYPE, 3
//        )
//        cardListField["12"] = GlobalCardData(
//            CardName.ACTION_HAPPY_BIRTHDAY, 2, CardType.ACTION_TYPE, 3
//        )
//        cardListField["13"] = GlobalCardData(
//            CardName.ACTION_SAY_NO, 0, CardType.ACTION_TYPE, 3
//        )
//        cardListField["14"] = GlobalCardData(
//            CardName.RENT_BLOK_BC_TYPE, 0, CardType.ACTION_TYPE, 2
//        )
//        cardListField["15"] = GlobalCardData(
//            CardName.RENT_BLOK_DE_TYPE, 0, CardType.ACTION_TYPE, 2
//        )
//        cardListField["16"] = GlobalCardData(
//            CardName.RENT_BLOK_FG_TYPE, 0, CardType.ACTION_TYPE, 2
//        )
//        cardListField["17"] = GlobalCardData(
//            CardName.RENT_BLOK_ANY_TYPE, 0, CardType.ACTION_TYPE, 3
//        )
//        cardListField["18"] = GlobalCardData(
//            CardName.DOUBLE_THE_RENT, 0, CardType.ACTION_TYPE, 5
//        )
//
//        var price = 1
//        var totalLevel = 2
//        for(i in 19..33) {
//            if (cluster > totalLevel) {
//                cluster = 1
//                if (asciiValue == 108) asciiValue+=2
//                else asciiValue++
//                price++
//            } else cluster++
//            val cardName = "${asciiValue.toChar()}"
//            cardListField[i.toString()] = GlobalCardData(
//                cardName, cluster, CardType.PROPERTY_TYPE, price
//            )
//
//            if (i >= 28) totalLevel = 1
//        }
//        cardListField["34"] = GlobalCardData("bc", 1, CardType.PROPERTY_TYPE, -1)
//        cardListField["35"] = GlobalCardData("bc", 2, CardType.PROPERTY_TYPE, -1)
//        cardListField["36"] = GlobalCardData("bc", 3, CardType.PROPERTY_TYPE, -1)
////        cardListField["36"] = GlobalCardData("s", 0, CardType.PROPERTY_TYPE, -1)
//
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
}