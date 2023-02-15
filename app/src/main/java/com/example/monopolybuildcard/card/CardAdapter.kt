package com.example.monopolybuildcard.card

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.Constant.CardName
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.Util.substringLastTwoChar
import com.example.monopolybuildcard.main.RoomData

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class CardAdapter(
    private val dataset: MutableList<GlobalCardData>
): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onCardAdded: ((GlobalCardData, Int) -> Unit)? = null
    var onCardDiscard: ((GlobalCardData, Int) -> Unit)? = null
    var onSayNo: ((Int) -> Unit)? = null

    var actions = mutableListOf<GlobalActionData>()
    var isDiscard = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)

        return CardViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = dataset[position]

        holder.cardAdd.visibility = if (actions.any { it.card?.type == item.type }) View.INVISIBLE
        else View.VISIBLE

        if (isDiscard) holder.cardAdd.setImageResource(R.drawable.spr_card_discard)
        else holder.cardAdd.setImageResource(R.drawable.spr_card_add)

        holder.cardRoot.setOnClickListener {
            if (isDiscard) onCardDiscard?.invoke(item, position)
            else if (!actions.any { it.card?.type == item.type }) onCardAdded?.invoke(item, position)
            onSayNo?.invoke(position)
        }

        holder.cardRentRoot.visibility = View.GONE
        holder.cardNoRent.visibility = View.VISIBLE

        holder.assetName.visibility = View.GONE
        holder.assetPrice.visibility = View.GONE

        when (item.type) {
            CardType.PROPERTY_TYPE -> {
                holder.cardImage.setImageResource(Util.mapIdToImage(item))
                holder.assetName.text = "Blok ${item.id?.uppercase()}"

                val totalCharId = item.id?.length ?: -1

                holder.assetName.visibility = View.VISIBLE
                holder.assetPrice.visibility = View.VISIBLE

                if (totalCharId > 1) {
                    holder.wildCardRoot.visibility = View.VISIBLE
                    holder.wildCardTopImage.setImageResource(R.drawable.spr_card_asset_brown_apartement)
                    holder.wildCardBottomImage.setImageResource(R.drawable.spr_card_asset_blue_apartement)

                    holder.wildCardTopImage.setOnClickListener {
                        onCardAdded?.invoke(item, position)
                    }
                    holder.wildCardBottomImage.setOnClickListener {
                        item.id?.reversed()
                        onCardAdded?.invoke(item, position)
                    }
                }
            }
            CardType.MONEY_TYPE -> {
                holder.cardImage.setImageResource(Util.mapIdToImage(item))
            }
            CardType.ACTION_TYPE -> {
                if (item.id?.contains("rent") == true) {
                    holder.cardRentRoot.visibility = View.VISIBLE
                    holder.cardNoRent.visibility = View.GONE
                    holder.cardActionRentImage.setImageResource(Util.mapIdToImage(item))

                    val itemId = item.id ?: ""
                    holder.cardActionRentTitle.text = "ASSET ${substringLastTwoChar(itemId)}"
                    holder.cardActionRentDesc.text = "All player pay rent based on one of these assets"

                    holder.cardActionRentCenterImage2.visibility = View.VISIBLE

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
                    holder.cardImage.setImageResource(Util.mapIdToImage(item))
                }
            }
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRoot: View = itemView.findViewById(R.id.layout_root_card)
        val cardNoRent: View = itemView.findViewById(R.id.layout_parent_card)
        val cardImage: ImageView = itemView.findViewById(R.id.iv_card)
        val assetName: TextView = itemView.findViewById(R.id.tv_card_asset_name)
        val assetPrice: ConstraintLayout = itemView.findViewById(R.id.layout_card_asset_price)
        val cardAdd: ImageView = itemView.findViewById(R.id.iv_add_card)

        val cardRentRoot: View = itemView.findViewById(R.id.layout_parent_action_rent_card)
        val cardActionRentImage: ImageView = itemView.findViewById(R.id.iv_action_rent_card)
        val cardActionRentCenterImage: ImageView = itemView.findViewById(R.id.iv_action_rent_center_image)
        val cardActionRentCenterImage2: ImageView = itemView.findViewById(R.id.iv_action_rent_center_image2)
        val cardActionRentTitle: TextView = itemView.findViewById(R.id.tv_action_rent_title)
        val cardActionRentDesc: TextView = itemView.findViewById(R.id.tv_action_rent_desc)

        val wildCardRoot: View = itemView.findViewById(R.id.layout_parent_wild_card)
        val wildCardTopImage: ImageView = itemView.findViewById(R.id.iv_wild_card_top)
        val wildCardBottomImage: ImageView = itemView.findViewById(R.id.iv_wild_card_bottom)
    }

    override fun getItemCount() = dataset.size

    fun getCard(byIndex: Int): GlobalCardData {
        return dataset[byIndex]
    }

    fun removeCard(whichCard: Int) {
        dataset.removeAt(whichCard)
        notifyDataSetChanged()
    }

    fun removeCards(cards: GlobalCardData) {
        dataset.remove(cards)
        notifyDataSetChanged()
    }

    fun discardCardMode() {
        isDiscard = true
        notifyDataSetChanged()
    }

    fun undoDiscardMode() {
        isDiscard = false
        notifyDataSetChanged()
    }

    fun resetCardStatus() {
        actions.clear()
        notifyDataSetChanged()
    }

    fun addCard(cardData: MutableList<GlobalCardData>) {
        dataset.addAll(cardData)
        notifyDataSetChanged()
    }

    fun draw2Card(roomData: RoomData) {
        val totalReadyCard = roomData.cards?.ready?.size ?: 0

        if (totalReadyCard > 0) {
            val sharedCard = roomData.cards?.ready?.removeAt(0)
            if (sharedCard != null) {
                dataset.add(sharedCard)
            }
        }

        if (totalReadyCard > 0) {
            val sharedCard = roomData.cards?.ready?.removeAt(0)
            if (sharedCard != null) {
                dataset.add(sharedCard)
            }
        }

        notifyDataSetChanged()
    }

    fun replaceCard(cardData: MutableList<GlobalCardData>) {
        dataset.clear()
        dataset.addAll(cardData)
        notifyDataSetChanged()
    }

    fun replaceActionPostedCard(actionCards: MutableList<GlobalActionData>) {
        this.actions = actionCards
        notifyDataSetChanged()
    }

    fun listCard(): MutableList<GlobalCardData> {
        return dataset
    }
}