package com.example.monopolybuildcard.asset

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
import com.example.monopolybuildcard.Util
import com.example.monopolybuildcard.card.CardType

/**
 * Adapter for the task list. Has a reference to the [TodoListModel] to send actions back to it.
 */
@SuppressLint("NotifyDataSetChanged")
open class AssetAdapter(
    private var dataset: MutableList<GlobalCardData>
): RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

    var onItemClick: ((GlobalCardData) -> Unit)? = null
    var onRotateCard: ((MutableList<GlobalCardData>, Boolean) -> Unit)? = null

    private var assetHasBeenPosted = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.asset_item, parent, false)

        return AssetViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val item = dataset[position]
        val imageCard = Util.mapIdToImage(item)

        holder.setImageSource(item, imageCard)
        holder.setOnClickAssetImage(item, onItemClick)
    }

    inner class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val assetImage1: View = itemView.findViewById(R.id.inc_asset1)
        private val assetImage2: View = itemView.findViewById(R.id.inc_asset2)
        private val assetImage3: View = itemView.findViewById(R.id.inc_asset3)

        private val rootWildCard: View = itemView.findViewById(R.id.layout_parent_wild_card)

        fun setOnClickAssetImage(
            cardItem: GlobalCardData,
            onItemClick: ((GlobalCardData) -> Unit)?
        ) {
            assetImage1.setOnClickListener { onItemClick?.invoke(cardItem) }
            assetImage2.setOnClickListener { onItemClick?.invoke(cardItem) }
            assetImage3.setOnClickListener { onItemClick?.invoke(cardItem) }
        }

        fun setImageSource(cardItem: GlobalCardData, image: Int) {
            setCardImageResource(cardItem)

            assetImage1.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage2.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE
            assetImage3.findViewById<TextView>(R.id.tv_card_asset_name).visibility = View.VISIBLE

            assetImage1.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage2.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE
            assetImage3.findViewById<ConstraintLayout>(R.id.layout_card_asset_price).visibility = View.VISIBLE

            assetImage1.visibility = View.GONE
            assetImage2.visibility = View.GONE
            assetImage3.visibility = View.GONE

            val charAsset = cardItem.id?.get(0) ?: ' '
            if (!assetHasBeenPosted.contains(charAsset) && charAsset != ' ') {
                assetHasBeenPosted += charAsset

                assetImage1.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"
                assetImage2.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"
                assetImage3.findViewById<TextView>(R.id.tv_card_asset_name).text =
                    "Blok ${cardItem.id?.uppercase()}"

                when (dataset.count { it.id?.get(0) == charAsset }) {
                    1 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.GONE
                        assetImage3.visibility = View.GONE

                        checkWildCard(assetImage1, cardItem)
                    }
                    2 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.VISIBLE
                        assetImage3.visibility = View.GONE

                        checkWildCard(assetImage2, cardItem)
                    }
                    3 -> {
                        assetImage1.visibility = View.VISIBLE
                        assetImage2.visibility = View.VISIBLE
                        assetImage3.visibility = View.VISIBLE

                        checkWildCard(assetImage3, cardItem)
                    }
                }
            }
        }

        private fun setCardImageResource(cardData: GlobalCardData) {
            val idCard = cardData.id ?: ""
            if (idCard.length > 1) {
                val topImage = GlobalCardData(
                    cardData.id?.get(0)?.toString(), cardData.value, cardData.type, cardData.price
                )
                val downImage = GlobalCardData(
                    cardData.id?.get(1)?.toString(), cardData.value, cardData.type, cardData.price
                )

                rootWildCard.findViewById<ImageView>(R.id.iv_wild_card_top)
                    .setImageResource(Util.mapIdToImage(topImage))
                rootWildCard.findViewById<ImageView>(R.id.iv_wild_card_bottom)
                    .setImageResource(Util.mapIdToImage(downImage))
            } else {
                assetImage1.findViewById<ImageView>(R.id.iv_card)
                    .setImageResource(Util.mapIdToImage(cardData))
                assetImage2.findViewById<ImageView>(R.id.iv_card)
                    .setImageResource(Util.mapIdToImage(cardData))
                assetImage3.findViewById<ImageView>(R.id.iv_card)
                    .setImageResource(Util.mapIdToImage(cardData))
            }
        }

        private fun checkWildCard(view: View, cardData: GlobalCardData) {
            val idCard = cardData.id ?: ""

            if (idCard.length > 2) {
                view.setOnClickListener {
                    var isOriginId = false
                    dataset.find { it.id == idCard }.let {
                        it?.id?.reversed()
                        it?.price = AssetUtil.assetPrice[it?.id?.get(0).toString()]
                        isOriginId = AssetUtil.assetWildCard.contains(it?.id)
                    }

                    notifyDataSetChanged()
                    onRotateCard?.invoke(dataset, isOriginId)
                }
            }
        }
    }

    override fun getItemCount() = dataset.size

    fun replaceListAssetCard(listAssetData: MutableList<GlobalCardData>) {
        dataset = listAssetData
        assetHasBeenPosted = ""

        notifyDataSetChanged()
    }

    fun listAsset(): MutableList<GlobalCardData> = dataset

    fun totalPriceOfAsset(cardItem: GlobalCardData): Int {
        return dataset.filter {it.id == cardItem.id }.sumOf {
            cardItem.price ?: 0
        }
    }
}