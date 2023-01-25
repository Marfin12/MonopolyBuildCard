package com.example.monopolybuildcard.asset

import android.annotation.SuppressLint
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.monopolybuildcard.R
import com.example.monopolybuildcard.card.AssetData
import com.example.monopolybuildcard.card.CardData

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
open class AssetAdapter(
    private var dataset: MutableList<AssetData>
): RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    var onItemClick: ((AssetData, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_item, parent, false)

        return AssetViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val item = dataset[position]
        holder.setImageSource(item)
    }

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val assetImage1: View = itemView.findViewById(R.id.inc_asset1)
        private val assetImage2: View = itemView.findViewById(R.id.inc_asset2)
        private val assetImage3: View = itemView.findViewById(R.id.inc_asset3)

        fun setImageSource(
            cardItem: AssetData
        ) {
            assetImage1.findViewById<ImageView>(R.id.iv_card).setImageResource(cardItem.image)
            assetImage2.findViewById<ImageView>(R.id.iv_card).setImageResource(cardItem.image)
            assetImage3.findViewById<ImageView>(R.id.iv_card).setImageResource(cardItem.image)

            assetImage1.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage2.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage3.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE

            assetImage1.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage2.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage3.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE

            when (cardItem.level) {
                1 -> {
                    assetImage1.visibility = View.VISIBLE
                    assetImage2.visibility = View.GONE
                    assetImage3.visibility = View.GONE
                }
                2 -> {
                    assetImage1.visibility = View.VISIBLE
                    assetImage2.visibility = View.VISIBLE
                    assetImage3.visibility = View.GONE
                }
                3 -> {
                    assetImage1.visibility = View.VISIBLE
                    assetImage2.visibility = View.VISIBLE
                    assetImage3.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = dataset.size

    @SuppressLint("NotifyDataSetChanged")
    fun addAssetCard(cardData: CardData) {
        val sameAssetIndex = dataset.indexOfFirst { assetCard ->
            assetCard.image == cardData.image
        }

        if (sameAssetIndex < 0) {
            dataset.add(
                AssetData(
                    cardData.image,
                    1
                )
            )
        } else {
            val assetLevel = dataset[sameAssetIndex].level
            dataset[sameAssetIndex] = AssetData(
                cardData.image, assetLevel + 1
            )
        }

        notifyDataSetChanged()
    }

    fun replaceListAssetCard(listAssetData: MutableList<AssetData>) {
        dataset = listAssetData
    }
}