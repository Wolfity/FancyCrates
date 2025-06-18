package me.wolfity.fancycrates.animation.impl

import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player

class ChronoShiftAnimation(crateConfig: CrateConfig) : HologramAnimation("chrono_shift", 4000L, crateConfig) {

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.0, 0.0)

        val timeFreezePhase = progress < 0.7
        if (timeFreezePhase) {
            
            world.spawnParticle(Particle.PORTAL, center, 5, 0.3, 0.3, 0.3, 0.01)
            world.spawnParticle(Particle.ENCHANT, center.clone().add(0.0, 0.5, 0.0), 2, 0.2, 0.2, 0.2)

            if ((progress * 100).toInt() % 20 == 0) {
                player.playSound(location, Sound.BLOCK_NOTE_BLOCK_HAT, 0.4f, 0.4f + progress.toFloat())
            }
        } else {
            
            if (rewardHologram == null) {
                rewardHologram = createItemHologram(center, rewardItem)

                world.spawnParticle(Particle.FLASH, center, 10)
                world.spawnParticle(Particle.EXPLOSION, center, 1)
                world.spawnParticle(Particle.END_ROD, center, 20, 0.2, 0.5, 0.2)
                player.playSound(location, Sound.ENTITY_ENDER_EYE_DEATH, 0.4f, 0.4f)
            }
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
    }
}
