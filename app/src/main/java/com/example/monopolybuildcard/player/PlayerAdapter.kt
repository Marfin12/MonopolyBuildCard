package com.example.monopolybuildcard.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.R


/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
open class PlayerAdapter(
    private val dataset: List<PlayerData>
): RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    var onItemInfoClick: ((PlayerData) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.player_item, parent, false)

        return PlayerViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val item = dataset[position]

        holder.playerName.text = item.name
        holder.playerMoney.text = "${item.money} M"
        holder.playerAsset.text = item.asset.toString()
        holder.playerTurnOutline.isVisible = item.isMyTurn

        holder.playerInfo.setOnClickListener {
            onItemInfoClick?.invoke(item)
        }
    }

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerName: TextView = itemView.findViewById(R.id.tv_enemy_name)
        val playerMoney: TextView = itemView.findViewById(R.id.tv_enemy_money)
        val playerAsset: TextView = itemView.findViewById(R.id.tv_enemy_asset)
        val playerInfo: ImageView = itemView.findViewById(R.id.iv_enemy_info)
        val playerTurnOutline: View = itemView.findViewById(R.id.view_enemy_square)
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun setPlayerTurn(whichPlayer: Int) {
        dataset.forEachIndexed { index, playerData ->
            playerData.isMyTurn = index == whichPlayer
        }
        notifyDataSetChanged()
    }

    fun listPlayer(): List<PlayerData> {
        return dataset
    }
}