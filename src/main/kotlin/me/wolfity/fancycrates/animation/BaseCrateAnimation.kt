package me.wolfity.fancycrates.animation

import me.wolfity.developmentutil.ext.uuid
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.toItemStack
import me.wolfity.fancycrates.util.weightedRandomItem
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

abstract class BaseCrateAnimation(
    val id: String,
    override val duration: Long,
    val config: CrateConfig
) : CrateAnimation {

    protected val contents = config.rewards.map { it.toItemStack() }

    protected val rewardItem by lazy {
        weightedRandomItem(config.rewards.associate { it.toItemStack() to it.weight })!!
    }

    var taskId = -1

    override fun startAnimation(player: Player, location: Location) {
        plugin.playerStateManager.setOpeningCrate(player.uuid, true)
        val startTime = System.currentTimeMillis()
        taskId = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val elapsed = System.currentTimeMillis() - startTime
            val progress = (elapsed.toDouble() / duration).coerceIn(0.0, 1.0)

            if (progress >= 1.0) {
                Bukkit.getScheduler().cancelTask(taskId)
                onEnd(player, location)
            } else {
                onTick(player, location, progress)
            }
        }, 0L, 1L).taskId
    }

    protected abstract fun onTick(
        player: Player,
        location: Location,
        progress: Double
    )

    protected open fun onEnd(player: Player, location: Location) {
        player.inventory.addItem(rewardItem)
    }
}
