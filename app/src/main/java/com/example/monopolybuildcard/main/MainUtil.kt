package com.example.monopolybuildcard.main

import android.content.Context
import android.view.View
import androidx.annotation.Keep
import androidx.core.view.isVisible
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.Util.mapIdToImage
import com.example.monopolybuildcard.Util.substringLastTwoChar
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.databinding.ActivityMainBinding

@Keep
object MainUtil {
    fun prepareActionUI(binding: ActivityMainBinding, context: Context, onSkip: (() -> Unit)) {
        binding.rvListCard.visibility = View.GONE

        binding.btnSkip.backgroundTintList = context.resources.getColorStateList(
            R.color.red_700, context.theme
        )
        binding.btnSkip.text = "Cancel"
        binding.btnSkip.setOnClickListener { onSkip.invoke() }
    }

    fun showEnemyPostedCard(binding: ActivityMainBinding, roomData: RoomData) {
        binding.layoutEnemyCardPosted.isVisible = true

        roomData.actions?.forEach {
            when (it.card?.type) {
                CardType.MONEY_TYPE -> {
                    binding.layoutIncludeSelectedMoneyCard.root.isVisible = true
                    binding.layoutIncludeSelectedMoneyCard.ivCard.setImageResource(
                        mapIdToImage(it.card!!)
                    )
                }
                CardType.PROPERTY_TYPE -> {
                    if (it.card?.id == Constant.CardName.PROPERTY_FLIP) {
                        binding.layoutIncludeSelectedFlipCard.root.isVisible = true
                        binding.layoutIncludeSelectedAssetCard.ivCard.setImageResource(
                            R.drawable.spr_card_asset_flip
                        )
                        binding.layoutIncludeSelectedFlipCard.layoutParentWildCard.isVisible = true
                    } else {
                        binding.layoutIncludeSelectedAssetCard.root.isVisible = true
                        binding.layoutIncludeSelectedAssetCard.tvCardAssetName.text =
                            "Blok ${it.card?.id}"
                        binding.layoutIncludeSelectedAssetCard.ivCard.setImageResource(
                            mapIdToImage(it.card!!)
                        )
                    }
                }
                CardType.ACTION_TYPE -> {
                    with(binding.layoutIncludeSelectedActionCard) {
                        root.isVisible = true

                        if (it.card?.id?.contains("rent") == true) {
                            ivActionRentCard.setImageResource(mapIdToImage(it.card!!))
                            layoutParentCard.visibility = View.GONE
                            layoutParentActionRentCard.visibility = View.VISIBLE

                            val itemId = it.card?.id ?: ""

                            tvActionRentTitle.text = "ASSET ${substringLastTwoChar(itemId)}"
                            tvActionRentDesc.text =
                                "All player pay rent based on one of these assets"

                            when (itemId) {
                                Constant.CardName.RENT_BLOK_BC_TYPE -> {
                                    ivActionRentCenterImage.setImageResource(R.drawable.spr_card_asset_brown_apartement)
                                    ivActionRentCenterImage2.setImageResource(R.drawable.spr_card_asset_aqua_apartement)
                                }
                                Constant.CardName.RENT_BLOK_DE_TYPE -> {
                                    ivActionRentCenterImage
                                        .setImageResource(R.drawable.spr_card_asset_red_apartement)
                                    ivActionRentCenterImage2
                                        .setImageResource(R.drawable.spr_card_asset_lime_apartement)
                                }
                                Constant.CardName.RENT_BLOK_FG_TYPE -> {
                                    ivActionRentCenterImage.setImageResource(R.drawable.spr_card_asset_blue_apartement)
                                    ivActionRentCenterImage2.setImageResource(R.drawable.spr_card_asset_orange_apartement)
                                }
                                Constant.CardName.RENT_BLOK_ANY_TYPE -> {
                                    ivActionRentCenterImage
                                        .setImageResource(R.drawable.spr_card_action_rent_any)
                                    ivActionRentCenterImage2.visibility = View.GONE
                                    tvActionRentTitle.text = "ASSET ?"
                                    tvActionRentDesc.text =
                                        "All player pay rent based on any selected asset"
                                }
                                Constant.CardName.DOUBLE_THE_RENT -> {
                                    ivActionRentCenterImage
                                        .setImageResource(R.drawable.spr_card_action_double_the_rent)
                                    ivActionRentCenterImage2.visibility = View.GONE
                                    tvActionRentTitle.text = "RENT ?"
                                    tvActionRentDesc.text =
                                        "Select any rent card you want to double with"
                                }
                            }
                        }
                        else {
                            ivCard.setImageResource(mapIdToImage(it.card!!))

                            layoutParentCard.visibility = View.VISIBLE
                            layoutParentActionRentCard.visibility = View.GONE
                        }
                    }

                    binding.layoutIncludeSelectedAssetCardForcedDeal.root.visibility = View.GONE
                }
            }
        }
    }

    fun hideEnemyPostedCard(binding: ActivityMainBinding) {
        binding.layoutEnemyCardPosted.isVisible = false
        binding.layoutIncludeSelectedMoneyCard.root.isVisible = false
        binding.layoutIncludeSelectedAssetCard.root.isVisible = false
        binding.layoutIncludeSelectedActionCard.root.isVisible = false
    }

    fun adjustVisibilityPopupButtonGroup(binding: ActivityMainBinding, isPopup: Boolean) {
        if (isPopup) {
            binding.layoutIncludePopupPlayer.layoutFloatShowThisPopupGroup.visibility =
                View.VISIBLE
            binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.visibility =
                View.GONE
        } else {
            binding.layoutIncludePopupPlayer.layoutFloatShowThisPopupGroup.visibility =
                View.GONE
            binding.layoutIncludePopupPlayer.layoutFloatCheckGroup.visibility =
                View.VISIBLE
        }
    }
}
