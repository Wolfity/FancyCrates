package me.wolfity.fancycrates.animation

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.developmentutil.ext.uuid
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.plugin
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

abstract class HologramAnimation(id: String, duration: Long, crateConfig: CrateConfig) :
    BaseCrateAnimation(id, duration, crateConfig) {

    protected val holograms = mutableListOf<Hologram>()
    protected var rewardHologram: Hologram? = null

    protected fun createItemHologram(location: Location, item: ItemStack): Hologram {
        val hologram = DHAPI.createHologram(generateHologramName(), location)
        DHAPI.addHologramLine(hologram, item)
        holograms.add(hologram)
        return hologram
    }

    protected fun moveHologram(hologram: Hologram, to: Location) {
        DHAPI.moveHologram(hologram, to)
    }

    protected fun updateHologramIcon(hologram: Hologram, item: ItemStack) {
        DHAPI.setHologramLines(hologram, listOf())
        DHAPI.addHologramLine(hologram, item)
    }

    override fun onEnd(player: Player, location: Location) {
        fun reward() {
            super.onEnd(player, location)
        }
        object : BukkitRunnable() {
            override fun run() {
                holograms.forEach {
                    DHAPI.removeHologram(it.name)
                }
                holograms.clear()
                plugin.playerStateManager.setOpeningCrate(player.uuid, false)
                reward()
            }
        }.runTaskLater(plugin, 60L)
    }

    protected fun generateHologramName(): String {
        return "$id${System.currentTimeMillis()}${(0..10_000).random()}"
    }
}
