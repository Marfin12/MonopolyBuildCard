package com.example.monopolybuildcard.card

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.GlobalActionData
import com.example.monopolybuildcard.GlobalCardData
import com.example.monopolybuildcard.R

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class CardAdapter(
    private val dataset: MutableList<GlobalCardData>
): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onCardAdded: ((GlobalCardData, Int) -> Unit)? = null
    var onCardDiscard: ((GlobalCardData, Int) -> Unit)? = null

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

        holder.cardImage.setOnClickListener {
            if (isDiscard) onCardDiscard?.invoke(item, position)
            else if (!actions.any { it.card?.type == item.type }) onCardAdded?.invoke(item, position)
        }

        when (item.type) {
            CardType.PROPERTY_TYPE -> {
                holder.cardImage.setImageResource(R.drawable.spr_card_asset_brown_apartement)
                holder.assetName.visibility = View.VISIBLE
                holder.assetPrice.visibility = View.VISIBLE
                holder.assetName.text = "Blok ${item.id?.uppercase()}"
            }
            CardType.MONEY_TYPE -> {
                holder.cardImage.setImageResource(R.drawable.spr_card_money_1)
                holder.assetName.visibility = View.GONE
                holder.assetPrice.visibility = View.GONE
            }
            CardType.ACTION_TYPE -> {
                holder.cardImage.setImageResource(R.drawable.spr_card_action_deal_breaker)
                holder.cardActionRentImage.setImageResource(R.drawable.spr_card_action_deal_breaker)
                holder.assetName.visibility = View.GONE
                holder.assetPrice.visibility = View.GONE

                if (item.id?.contains("rent") == true) {
                    holder.cardRentRoot.visibility = View.VISIBLE
                    holder.cardRoot.visibility = View.GONE
                    holder.cardActionRentImage.setImageResource(R.drawable.spr_card_action_rent_any)

                    val itemId = item.id ?: ""
                    val lastCharId = itemId.length - 1
                    holder.cardActionRentTitle.text = "ASSET ${itemId[lastCharId]}"
                    holder.cardActionRentDesc.text = "All player pay based on this asset price"

                    if (itemId == RentType.RENT_BLOK_ANY_TYPE) {
                        holder.cardActionRentTitle.text = "ASSET ?"
                        holder.cardActionRentDesc.text = "All player pay based on any selected asset price"
                    }
                } else {
                    holder.cardRentRoot.visibility = View.GONE
                    holder.cardRoot.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRoot: View = itemView.findViewById(R.id.layout_parent_card)
        val cardImage: ImageView = itemView.findViewById(R.id.iv_card)
        val assetName: TextView = itemView.findViewById(R.id.tv_card_asset_name)
        val assetPrice: ConstraintLayout = itemView.findViewById(R.id.layout_card_asset_price)
        val cardAdd: ImageView = itemView.findViewById(R.id.iv_add_card)

        val cardRentRoot: View = itemView.findViewById(R.id.layout_parent_action_rent_card)
        val cardActionRentImage: ImageView = itemView.findViewById(R.id.iv_action_rent_card)
        val cardActionRentCenterImage: ImageView = itemView.findViewById(R.id.iv_action_rent_center_image)
        val cardActionRentTitle: TextView = itemView.findViewById(R.id.tv_action_rent_title)
        val cardActionRentDesc: TextView = itemView.findViewById(R.id.tv_action_rent_desc)
    }

    override fun getItemCount() = dataset.size

    fun getCard(byIndex: Int): GlobalCardData {
        return dataset[byIndex]
    }

    fun removeCard(whichCard: Int) {
        dataset.removeAt(whichCard)
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