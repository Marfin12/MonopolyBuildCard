package com.example.monopolybuildcard

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monopolybuildcard.asset.AssetAdapter
import com.example.monopolybuildcard.card.CardAdapter
import com.example.monopolybuildcard.card.CardData
import com.example.monopolybuildcard.card.CardType
import com.example.monopolybuildcard.databinding.ActivityMainBinding
import com.example.monopolybuildcard.money.MoneyAdapter
import com.example.monopolybuildcard.player.PlayerAdapter
import com.example.monopolybuildcard.player.PlayerData

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isShowPopupAfterSelectingCard = true
    private var isPopupNeverShownAfterSelectingCard = true
    private var currentTurn = -1
    private val defaultCardData = mutableListOf(
        CardData(R.drawable.spr_card_money_1, CardType.MONEY_TYPE),
        CardData(R.drawable.spr_card_money_1, CardType.MONEY_TYPE),
        CardData(R.drawable.spr_card_money_1, CardType.MONEY_TYPE),
        CardData(R.drawable.spr_card_asset_brown_apartement, CardType.ASSET_TYPE),
        CardData(R.drawable.spr_card_asset_brown_apartement, CardType.ASSET_TYPE)
    )

    private var currentPlayerData = PlayerData(
        "Player 1",
        0,
        0,
        mutableListOf(),
        mutableListOf(),
        defaultCardData,
        true
    )

    private val playerAdapter: PlayerAdapter = PlayerAdapter(
        mutableListOf(
            PlayerData(
                "Player 2",
                0,
                0,
                mutableListOf(),
                mutableListOf(),
                defaultCardData,
                false
            ),PlayerData(
                "Player 3",
                0,
                0,
                mutableListOf(),
                mutableListOf(),
                defaultCardData,
                false
            ),PlayerData(
                "Player 4",
                0,
                0,
                mutableListOf(),
                mutableListOf(),
                defaultCardData,
                false
            )
        )
    )

    private val cardAdapter: CardAdapter = CardAdapter(currentPlayerData.listCard)
    private val moneyAdapter: MoneyAdapter = MoneyAdapter(currentPlayerData.listMoney)
    private val assetAdapter: AssetAdapter = AssetAdapter(currentPlayerData.listAsset)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayer()
        initComponent()
        initAdapter()
    }

    private fun initPlayer() {
        binding.tvPlayerName.text = currentPlayerData.name
        binding.tvPlayerAsset.text = "Asset: ${currentPlayerData.asset.toString()}"
        binding.tvPlayerMoney.text = "Money: ${currentPlayerData.money} M"
    }

    private fun initComponent() {
        binding.layoutIncludeSelectedMoneyCard.ivAddCard.visibility = View.GONE

        binding.ivPlayerInfo.setOnClickListener {
            assetAdapter.replaceListAssetCard(currentPlayerData.listAsset)
            moneyAdapter.replaceListMoneyCard(currentPlayerData.listMoney)

            binding.layoutIncludePopupPlayer.root.visibility = View.VISIBLE
        }

        binding.layoutIncludePopupPlayer.ivPopupClose.setOnClickListener {
            binding.layoutIncludePopupPlayer.root.visibility = View.GONE
        }

        binding.layoutIncludePopupPlayer.btnPopupNo.setOnClickListener {
            isPopupNeverShownAfterSelectingCard = false
            isShowPopupAfterSelectingCard = true
            binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = true
            binding.layoutIncludePopupPlayer.root.visibility = View.GONE

            adjustVisibilityPopupButtonGroup()
        }

        binding.layoutIncludePopupPlayer.btnPopupYes.setOnClickListener {
            isPopupNeverShownAfterSelectingCard = false
            isShowPopupAfterSelectingCard = false
            binding.layoutIncludePopupPlayer.cbShowPopup.isChecked = false
            binding.layoutIncludePopupPlayer.root.visibility = View.GONE

            adjustVisibilityPopupButtonGroup()
        }

        binding.layoutIncludePopupPlayer.cbShowPopup.setOnCheckedChangeListener { compoundButton, b ->
            isShowPopupAfterSelectingCard = (compoundButton as CompoundButton).isChecked
        }

        binding.btnSkip.setOnClickListener {
            var delaySetup = 0L
            playerAdapter.listPlayer().forEach { _ ->
                delaySetup += 1000L
                binding.rvListPlayer.postDelayed(nextPlayerTurn, delaySetup)
                delaySetup += 1000L
                binding.rvListPlayer.postDelayed(addAssetCard, delaySetup)
                delaySetup += 1000L
                binding.rvListPlayer.postDelayed(addMoneyCard, delaySetup)
            }
            delaySetup += 1000L
            binding.rvListPlayer.postDelayed(nextPlayerTurn, delaySetup)
        }
    }

    private fun initAdapter() {
        binding.rvListPlayer.adapter = playerAdapter
        binding.rvListPlayer.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        playerAdapter.onItemInfoClick = { playerData ->
            assetAdapter.replaceListAssetCard(playerData.listAsset)
            moneyAdapter.replaceListMoneyCard(playerData.listMoney)

            binding.layoutIncludePopupPlayer.root.visibility = View.VISIBLE
        }

        binding.rvListCard.adapter = cardAdapter
        binding.rvListCard.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        cardAdapter.onItemClick = { cardData, position ->
            if (currentPlayerData.isMyTurn) {
                addPlayerCard(cardData, position)

                if (isShowPopupAfterSelectingCard) {
                    binding.layoutIncludePopupPlayer.root.visibility = View.VISIBLE
                }
            }
        }

        with (binding.layoutIncludePopupPlayer) {
            rvListAsset.adapter = assetAdapter
            rvListAsset.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )

            rvListMoney.adapter = moneyAdapter
            rvListMoney.layoutManager = LinearLayoutManager(
                this@MainActivity, LinearLayoutManager.HORIZONTAL, false
            )
        }
    }

    private fun adjustVisibilityPopupButtonGroup() {
        if (isPopupNeverShownAfterSelectingCard) {
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

    private fun addPlayerCard(cardData: CardData, position: Int) {
        if (cardData.type == CardType.ASSET_TYPE) assetAdapter.addAssetCard(cardData)
        else moneyAdapter.addMoneyCard(cardData)

        cardAdapter.removeCard(position, cardData.type)
    }

    private val nextPlayerTurn = Runnable {
        currentTurn++

        if (currentTurn < playerAdapter.itemCount) {
            currentPlayerData.isMyTurn = false
            binding.viewNotMyTurn.isVisible = true
            binding.btnSkip.visibility = View.GONE
        }
        else {
            currentTurn = -1
            currentPlayerData.isMyTurn = true
            binding.viewNotMyTurn.isVisible = false

            cardAdapter.resetCardStatus()
            binding.btnSkip.visibility = View.VISIBLE
        }

        binding.layoutIncludeSelectedAssetCard.root.visibility = View.GONE
        binding.layoutIncludeSelectedMoneyCard.root.visibility = View.GONE
        playerAdapter.setPlayerTurn(currentTurn)
    }

    private val addAssetCard = Runnable {
        binding.layoutIncludeSelectedAssetCard.root.visibility = View.VISIBLE
    }

    private val addMoneyCard = Runnable {
        binding.layoutIncludeSelectedMoneyCard.root.visibility = View.VISIBLE
        binding.layoutIncludeSelectedMoneyCard.ivCard.setImageResource(R.drawable.spr_card_money_1)
    }
}