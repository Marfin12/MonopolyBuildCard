package com.example.monopolybuildcard

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.annotation.Keep
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.main.RoomCardData
import com.example.monopolybuildcard.main.RoomData

@Keep
object Util {
    @SuppressLint("HardwareIds")
    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    fun updateRoomData(
        curRoomData: RoomData,
        roomCardData: RoomCardData
    ): RoomData {
        return RoomData(
            roomCardData,
            curRoomData.actions,
            curRoomData.status,
            curRoomData.maxPlayer,
            curRoomData.users
        )
    }

    fun drawCard(roomData: RoomData, currentPlayerIndex: Int): RoomData {
        val totalReadyCards = roomData.cards?.ready?.size ?: 0
        if (totalReadyCards > 0) {
            val sharedCard = roomData.cards?.ready?.removeAt(0) ?: GlobalCardData()
            roomData.users?.get(currentPlayerIndex)?.cards?.add(sharedCard)
        }

        return roomData
    }

    fun isIdEqualToAnyAction(id: String): Boolean =
        id == CardName.ACTION_DEAL_BREAKER ||
        id == CardName.ACTION_FORCED_DEAL ||
        id == CardName.ACTION_SLY_DEAL ||
        id == CardName.ACTION_DEBT_COLLECTOR ||
        id == CardName.ACTION_HAPPY_BIRTHDAY ||
        id == CardName.RENT_BLOK_ANY_TYPE ||
        id == CardName.RENT_BLOK_BC_TYPE ||
        id == CardName.RENT_BLOK_DE_TYPE ||
        id == CardName.RENT_BLOK_FG_TYPE

    fun substringLastTwoChar(str: String): String {
        val lastIdx = str.length - 2
        return str.slice(lastIdx..lastIdx+1)
    }

    fun checkDoubleTheRent(actionCard: MutableList<GlobalActionData>): GlobalActionData {
        val rentCard = actionCard.last()

        if (actionCard.size > 1) {
            if (actionCard[actionCard.size - 2].card?.id == CardName.DOUBLE_THE_RENT) {
                rentCard.card?.value = rentCard.card?.value?.times(2)
            }
        }

        return rentCard
    }

    fun mapIdToImage(cardData: GlobalCardData): Int {
        with(CardName) {
            return when (cardData.id) {
                MONEY -> {
                    when (cardData.value) {
                        1 -> R.drawable.spr_card_money_1
                        2 -> R.drawable.spr_card_money_2
                        3 -> R.drawable.spr_card_money_3
                        4 -> R.drawable.spr_card_money_4
                        5 -> R.drawable.spr_card_money_5
                        10 -> R.drawable.spr_card_money_10
                        20 -> R.drawable.spr_card_money_20
                        else -> -1
                    }
                }
                PROPERTY_B -> R.drawable.spr_card_asset_blue_apartement
                PROPERTY_C -> R.drawable.spr_card_asset_aqua_apartement
                PROPERTY_D -> R.drawable.spr_card_asset_red_apartement
                PROPERTY_E -> R.drawable.spr_card_asset_brown_apartement
                PROPERTY_F -> R.drawable.spr_card_asset_orange_apartement
                PROPERTY_G -> R.drawable.spr_card_asset_lime_apartement
                PROPERTY_H -> R.drawable.spr_card_asset_grey_apartement
                PROPERTY_FLIP -> R.drawable.spr_card_asset_flip
                ACTION_PASS_GO -> R.drawable.spr_card_action_pass_go
                ACTION_SLY_DEAL -> R.drawable.spr_card_action_sly_deal
                ACTION_FORCED_DEAL -> R.drawable.spr_card_action_forced_deal
                ACTION_DEAL_BREAKER -> R.drawable.spr_card_action_deal_breaker
                ACTION_DEBT_COLLECTOR -> R.drawable.spr_card_action_debt_collector
                ACTION_HAPPY_BIRTHDAY -> R.drawable.spr_card_action_happy_birthday
                ACTION_SAY_NO -> R.drawable.spr_card_action_say_no
                else -> getRentTypeCard(cardData.id ?: "")
            }
        }
    }

    fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
        val tmp = this[index1]
        this[index1] = this[index2]
        this[index2] = tmp
    }

    private fun getRentTypeCard(cardId: String): Int {
        return if (cardId.contains("rent")) {
            if (cardId.contains("double") || cardId.contains("any")) {
                R.drawable.spr_card_action_rent_4m
            } else {
                R.drawable.spr_card_action_rent_2m
            }
        } else -1
    }
}
