package com.example.monopolybuildcard.card

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.MainActivity
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.asset.AssetAdapter

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
open class CardAdapter(
    private val dataset: MutableList<CardData>
): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var onItemClick: ((CardData, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item, parent, false)

        return CardViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = dataset[position]

        holder.cardImage.setImageResource(item.image)
        holder.cardAdd.visibility = if (item.isAbleToAdd) View.VISIBLE
        else View.INVISIBLE

        holder.cardImage.setOnClickListener {
            if (item.isAbleToAdd) onItemClick?.invoke(item, position)
        }

        if (item.image == R.drawable.spr_card_asset_brown_apartement) {
            holder.assetName.visibility = View.VISIBLE
            holder.assetPrice.visibility = View.VISIBLE
        }
    }

    inner class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.iv_card)
        val assetName: TextView = itemView.findViewById(R.id.tv_card_asset_name)
        val assetPrice: ConstraintLayout = itemView.findViewById(R.id.layout_card_asset_price)
        val cardAdd: ImageView = itemView.findViewById(R.id.iv_add_card)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun removeCard(whichCard: Int, whichType: String) {
        dataset.removeAt(whichCard)
        removePlusButton(whichType)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetCardStatus() {
        dataset.forEach {
            it.isAbleToAdd = true
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removePlusButton(forCardType: String) {
        dataset.filter { cardData ->  cardData.type == forCardType }.forEach {
            it.isAbleToAdd = false
        }
        notifyDataSetChanged()
    }
}