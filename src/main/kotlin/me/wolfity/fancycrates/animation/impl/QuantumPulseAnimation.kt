package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.sin

class QuantumPulseAnimation(crateConfig: CrateConfig) : HologramAnimation("quantum_pulse", 4000L, crateConfig) {

    private var currentHologram: Hologram? = null
    private var lastIndex = -1

    override fun onTick(player: Player, location: Location, progress: Double) {
        val center = location.clone().add(0.0, 1.7, 0.0)
        val itemsCount = contents.size
        if (itemsCount == 0) return

        val index = ((sin(progress * Math.PI * 10) + 1) / 2 * (itemsCount - 1)).toInt()
        if (index != lastIndex) {
            lastIndex = index

            
            currentHologram?.let { DHAPI.removeHologram(it.name) }

            val item = contents[index]
            currentHologram = createItemHologram(center, item)
        }

        player.world.spawnParticle(Particle.ELECTRIC_SPARK, center, 8, 0.3, 0.3, 0.3, 0.2)
        player.world.spawnParticle(Particle.SOUL_FIRE_FLAME, center, 4, 0.2, 0.5, 0.2, 0.05)
    }

    override fun onEnd(player: Player, location: Location) {
        val center = location.clone().add(0.0, 2.0, 0.0)

        currentHologram?.let {
            DHAPI.removeHologram(it.name)
            currentHologram = null
        }

        rewardHologram = createItemHologram(center, rewardItem)
        player.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 0.4f, 0.8f)
        player.world.spawnParticle(Particle.FLASH, center, 20, 0.4, 0.4, 0.4, 0.05)

        super.onEnd(player, location)
    }
}

