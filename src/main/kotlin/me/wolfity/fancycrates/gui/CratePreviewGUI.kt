package me.wolfity.fancycrates.gui

import me.wolfity.developmentutil.gui.GUI
import me.wolfity.developmentutil.util.style
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.crate.CrateRewardConfig
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.roundToDecimals
import me.wolfity.fancycrates.util.toItemStack
import org.bukkit.entity.Player

class CratePreviewGUI(player: Player, private val config: CrateConfig) :
    GUI(plugin, 54, config.cratePreviewGuiTitle, player) {

    init {
        constructGUI()
        openGUI()
    }


    private fun constructGUI() {
        config.rewards.forEachIndexed { index, crateReward ->
            val percentage = getRewardDropPercentage(crateReward)

            val additionalLore = plugin.config.getStringList("crate-preview-item-lore")
                .map { it.replace("{percentage}", percentage.roundToDecimals(1).toString()) }
                .map { style(it) }
            val itemStack = crateReward.toItemStack(additionalLore)

            setItem(index, itemStack)
        }
    }

    fun getRewardDropPercentage(reward: CrateRewardConfig): Double {
        val totalWeight = config.rewards.sumOf { it.weight }
        return if (totalWeight > 0) (reward.weight.toDouble() / totalWeight) * 100 else 0.0
    }

}