package com.example.monopolybuildcard.card

import android.view.View
import androidx.annotation.Keep
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util

@Keep
object CardUtil {
    fun renderPropertyCard(
        holder: CardAdapter.CardViewHolder,
        item: GlobalCardData,
        onCardAdded: ((GlobalCardData, Int) -> Unit)?,
        position: Int
    ) {
        val totalCharId = item.id?.length ?: -1

        holder.assetName.visibility = View.VISIBLE
        holder.assetPrice.visibility = View.VISIBLE

        holder.assetName.text = "Blok ${item.id?.get(0)?.uppercase()}"

        if (totalCharId > 1) {
            holder.wildCardRoot.visibility = View.VISIBLE

            val topImage = GlobalCardData(
                item.id?.get(0)?.toString(), item.value, item.type, item.price
            )
            val downImage = GlobalCardData(
                item.id?.get(1)?.toString(), item.value, item.type, item.price
            )

            holder.wildCardTopImage.setImageResource(Util.mapIdToImage(topImage))
            holder.wildCardBottomImage.setImageResource(Util.mapIdToImage(downImage))

            holder.wildCardAssetName.text = "Blok ${item.id?.get(1)?.uppercase()}"

            holder.wildCardTopImage.setOnClickListener {
                onCardAdded?.invoke(item, position)
            }
            holder.wildCardBottomImage.setOnClickListener {
                item.id?.reversed()
                onCardAdded?.invoke(item, position)
            }
        } else {
            holder.wildCardRoot.visibility = View.GONE
            holder.cardImage.setImageResource(Util.mapIdToImage(item))
        }
    }

    fun renderActionCard(holder: CardAdapter.CardViewHolder, item: GlobalCardData) {
        if (item.id?.contains("rent") == true) {
            holder.cardRentRoot.visibility = View.VISIBLE
            holder.cardActionRentCenterImage2.visibility = View.VISIBLE
            holder.cardNoRent.visibility = View.GONE

            holder.cardActionRentImage.setImageResource(Util.mapIdToImage(item))

            val itemId = item.id ?: ""
            holder.cardActionRentTitle.text = "ASSET ${Util.substringLastTwoChar(itemId)}"
            holder.cardActionRentDesc.text = "All player pay rent based on one of these assets"


            when(itemId) {
                CardName.RENT_BLOK_BC_TYPE -> {
                    holder.cardActionRentCenterImage
                        .setImageResource(R.drawable.spr_card_asset_blue_apartement)
                    holder.cardActionRentCenterImage2
                        .setImageResource(R.drawable.spr_card_asset_aqua_apartement)
                }
                CardName.RENT_BLOK_DE_TYPE -> {
                    holder.cardActionRentCenterImage
                        .setImageResource(R.drawable.spr_card_asset_red_apartement)
                    holder.cardActionRentCenterImage2
                        .setImageResource(R.drawable.spr_card_asset_brown_apartement)
                }
                CardName.RENT_BLOK_FG_TYPE -> {
                    holder.cardActionRentCenterImage
                        .setImageResource(R.drawable.spr_card_asset_orange_apartement)
                    holder.cardActionRentCenterImage2
                        .setImageResource(R.drawable.spr_card_asset_lime_apartement)
                }
                CardName.RENT_BLOK_ANY_TYPE -> {
                    holder.cardActionRentCenterImage
                        .setImageResource(R.drawable.spr_card_action_rent_any)
                    holder.cardActionRentCenterImage2.visibility = View.GONE
                    holder.cardActionRentTitle.text = "ASSET ?"
                    holder.cardActionRentDesc.text = "All player pay rent based on any selected asset"
                }
                CardName.DOUBLE_THE_RENT -> {
                    holder.cardActionRentCenterImage
                        .setImageResource(R.drawable.spr_card_action_double_the_rent)
                    holder.cardActionRentCenterImage2.visibility = View.GONE
                    holder.cardActionRentTitle.text = "RENT ?"
                    holder.cardActionRentDesc.text = "Select any rent card you want to double with"
                }
            }
        } else {
            holder.cardActionRentCenterImage2.visibility = View.GONE
            holder.cardRentRoot.visibility = View.GONE
            holder.cardNoRent.visibility = View.VISIBLE

            holder.cardImage.setImageResource(Util.mapIdToImage(item))
        }
    }

    fun renderMoneyCard(holder: CardAdapter.CardViewHolder, item: GlobalCardData) {
        holder.cardImage.setImageResource(Util.mapIdToImage(item))
    }
}
