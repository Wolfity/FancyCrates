package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.holograms.Hologram

import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player

class SpinAnimation(crateConfig: CrateConfig) : HologramAnimation("spin", 5000L, crateConfig) {

    private var currentIndex = 0
    private var lastSwitchTime = 0L
    private var interval = 100L
    private var hologram: Hologram? = null
    private var ended = false

    override fun onTick(player: Player, location: Location, progress: Double) {
        val center = location.clone().add(0.0, 1.5, 0.0)
        val now = System.currentTimeMillis()

        if (hologram == null) {
            hologram = createItemHologram(center, contents.first())
        }

        if (!ended && now - lastSwitchTime > interval) {
            val item = contents[currentIndex % contents.size]
            updateHologramIcon(hologram!!, item)
            player.playSound(location, Sound.UI_BUTTON_CLICK, 0.4f, 1.0f + progress.toFloat())
            currentIndex++
            lastSwitchTime = now

            
            interval = (50 + (progress * 450)).toLong()

            if (progress >= 0.9) {
                updateHologramIcon(hologram!!, rewardItem)
                rewardHologram = hologram
                ended = true
                location.world?.spawnParticle(Particle.TOTEM_OF_UNDYING, center, 10, 0.3, 0.5, 0.3)
                player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 0.6f, 1.5f)
            }
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
    }
}
