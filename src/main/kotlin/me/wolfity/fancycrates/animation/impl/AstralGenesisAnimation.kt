package me.wolfity.fancycrates.animation.impl

import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.util.toItemStack
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustTransition
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

class AstralGenesisAnimation(crateConfig: CrateConfig) : HologramAnimation("astral_genesis", 8000L, crateConfig) {

    private var hasSpawned = false

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.5, 0.0)


        if (progress < 0.3) {
            val swirl = 10
            val radius = 2.5 * (progress / 0.3)
            repeat(swirl) { i ->
                val angle = i * (2 * Math.PI / swirl) + progress * 20
                val x = cos(angle) * radius
                val z = sin(angle) * radius
                world.spawnParticle(
                    Particle.DUST_COLOR_TRANSITION,
                    center.clone().add(x, progress * 2, z),
                    0,
                    DustTransition(Color.PURPLE, Color.AQUA, 1f)
                )
                world.spawnParticle(Particle.REVERSE_PORTAL, center.clone().add(x / 2, 0.2, z / 2), 0)
            }
            if ((progress * 100).toInt() % 5 == 0) {
                player.playSound(center, Sound.BLOCK_BEACON_AMBIENT, 0.4f, 2f)
            }
        }


        if (progress in 0.3..0.6) {
            val beamFrom = center.clone().add(0.0, 1.0 * (1 - (progress - 0.3) / 0.3), 0.0)
            world.spawnParticle(Particle.ELECTRIC_SPARK, beamFrom, 10, 0.1, 0.5, 0.1)
            world.spawnParticle(Particle.DRAGON_BREATH, beamFrom.clone().add(0.0, -0.2, 0.0), 3)
            world.spawnParticle(Particle.FIREWORK, center.clone(), 2)

            if ((progress * 100).toInt() % 10 == 0) {
                player.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 0.4f, 0.8f)
            }
        }


        if (!hasSpawned && progress >= 0.65) {
            if (rewardHologram == null) {
                rewardHologram = createItemHologram(center.clone().add(0.0, 0.5, 0.0), rewardItem.toItemStack())
            }
            hasSpawned = true

            world.spawnParticle(Particle.FLASH, center.clone(), 1)
            repeat(30) {
                val angle = it * (2 * Math.PI / 30)
                val loc = center.clone().add(cos(angle) * 2, 1.5, sin(angle) * 2)
                world.spawnParticle(Particle.SONIC_BOOM, loc, 1)
            }

            player.playSound(center, Sound.ENTITY_WITHER_SPAWN, 0.4f, 0.7f)
            player.playSound(center, Sound.BLOCK_END_PORTAL_SPAWN, 0.4f, 0.8f)
        }


        if (hasSpawned) {
            val y = 1.5 - ((progress - 0.75) / 0.25) * 2.5
            val loc = center.clone().add(0.0, y, 0.0)
            rewardHologram?.let {
                moveHologram(it, loc)
            }

            val orbitCount = 8
            val orbRad = 1.2
            repeat(orbitCount) {
                val angle = it * (2 * Math.PI / orbitCount) + progress * 15
                val x = cos(angle) * orbRad
                val z = sin(angle) * orbRad
                val orbitLoc = loc.clone().add(x, 0.1, z)
                world.spawnParticle(Particle.SOUL, orbitLoc, 1, 0.0, 0.0, 0.0, 0.01)
            }

            player.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 0.4f, 1.2f)
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
        player.playSound(location, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.4f, 0.5f)
    }
}
