package com.example.monopolybuildcard.card

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
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

    var actions = mutableListOf<GlobalCardData>()
    var isDiscard = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)

        return CardViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = dataset[position]

        holder.cardAdd.visibility = if (actions.any { it.type == item.type }) View.INVISIBLE
        else View.VISIBLE

        if (isDiscard) holder.cardAdd.setImageResource(R.drawable.spr_card_discard)
        else holder.cardAdd.setImageResource(R.drawable.spr_card_add)

        holder.cardImage.setOnClickListener {
            if (isDiscard) onCardDiscard?.invoke(item, position)
            else if (!actions.any { it.type == item.type }) onCardAdded?.invoke(item, position)
        }

        when (item.type) {
            CardType.ASSET_TYPE -> {
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
                holder.assetName.visibility = View.GONE
                holder.assetPrice.visibility = View.GONE
            }
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.iv_card)
        val assetName: TextView = itemView.findViewById(R.id.tv_card_asset_name)
        val assetPrice: ConstraintLayout = itemView.findViewById(R.id.layout_card_asset_price)
        val cardAdd: ImageView = itemView.findViewById(R.id.iv_add_card)
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

    fun replaceActionPostedCard(actionCards: MutableList<GlobalCardData>) {
        this.actions = actionCards
        notifyDataSetChanged()
    }

    fun listCard(): MutableList<GlobalCardData> {
        return dataset
    }
}