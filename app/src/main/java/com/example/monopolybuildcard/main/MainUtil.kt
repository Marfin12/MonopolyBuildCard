package com.example.monopolybuildcard.main

import android.content.Context
import android.view.View
import androidx.annotation.Keep
import androidx.core.view.isVisible
import com.example.monopolybuildcard.Constant
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util.mapIdToImage
import com.example.monopolybuildcard.Util.substringLastTwoChar
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.databinding.ActivityMainBinding
import com.example.monopolybuildcard.databinding.CardItemWildPvBinding

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
                    val cardId = it.card?.id ?: ""
                    if (cardId == Constant.CardName.PROPERTY_FLIP) {
                        binding.layoutIncludeSelectedFlipCard.ivCard.setImageResource(
                            R.drawable.spr_card_asset_flip
                        )
                        binding.layoutIncludeSelectedFlipCard.root.isVisible = true
                    } else if (cardId.length > 1) {
                        renderWildCard(binding.layoutIncludeSelectedWildCard, it.card!!)
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

                            layoutParentCard.visibility = View.INVISIBLE
                            layoutParentActionRentCard.visibility = View.VISIBLE

                            val itemId = it.card?.id ?: ""
                            val whichBlock = substringLastTwoChar(itemId)

                            tvActionRentTitle.text = "ASSET ${whichBlock}"
                            tvActionRentDesc.text = "All player pay rent based on one of these assets"

                            when (itemId) {
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
                                else -> {
                                    val selectedCard = it.card!!

                                    val topImage = GlobalCardData(
                                        whichBlock[0].toString(),
                                        selectedCard.value,
                                        selectedCard.type,
                                        selectedCard.price
                                    )
                                    val downImage = GlobalCardData(
                                        whichBlock[1].toString(),
                                        selectedCard.value,
                                        selectedCard.type,
                                        selectedCard.price
                                    )

                                    ivActionRentCenterImage.setImageResource(mapIdToImage(topImage))
                                    ivActionRentCenterImage2.setImageResource(mapIdToImage(downImage))
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
        binding.layoutIncludeSelectedFlipCard.root.isVisible = false
        binding.layoutIncludeSelectedWildCard.root.isVisible = false
        binding.layoutIncludeDoubleTheRentCard.root.isVisible = false
        binding.layoutIncludeSelectedAssetCardForcedDeal.root.isVisible = false
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

    fun renderWildCard(layoutWildCard: CardItemWildPvBinding, cardData: GlobalCardData) {
        val cardId = cardData.id ?: ""

        with (layoutWildCard) {
            root.isVisible = true

            tvCardAssetName.text = "Blok ${cardId[0]}"
            tvWildCardAssetName.text = "Blok ${cardId[1]}"

            val topImage = GlobalCardData(
                cardId[0].toString(), cardData.value, cardData.type, cardData.price
            )
            val downImage = GlobalCardData(
                cardId[1].toString(), cardData.value, cardData.type, cardData.price
            )

            ivWildCardTop.setImageResource(mapIdToImage(topImage))
            ivWildCardBottom.setImageResource(mapIdToImage(downImage))
        }
    }

    fun isPaymentActionType(actionData: GlobalActionData): Boolean {
        return actionData.cardTaken == null && actionData.cardGiven == null
                && actionData.cardDealBreaker == null
    }
}
