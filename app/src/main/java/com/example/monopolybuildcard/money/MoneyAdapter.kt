package com.example.monopolybuildcard.money

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.card.AssetData
import com.example.monopolybuildcard.card.CardData

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
open class MoneyAdapter(
    private var dataset: MutableList<MoneyData>
): RecyclerView.Adapter<MoneyAdapter.PlayerViewHolder>() {

    var onItemClick: ((MoneyData, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.money_item, parent, false)

        return PlayerViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = dataset[position]
        holder.cardImage.setImageResource(item.image)
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardImage: ImageView = itemView.findViewById(R.id.iv_money_card)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun addMoneyCard(cardData: CardData) {
        dataset.add(MoneyData(cardData.image))
        notifyDataSetChanged()
    }

    fun replaceListMoneyCard(listMoneyData: MutableList<MoneyData>) {
        dataset = listMoneyData
    }
}