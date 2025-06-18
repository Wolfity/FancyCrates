package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.util.runSync
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class TemporalRiftAnimation(crateConfig: CrateConfig) : HologramAnimation("temporal_rift", 4000L, crateConfig) {

    private var glitchHologram: Hologram? = null
    private val particleRadiusBase = 0.5

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return

        val bobbingOffset = sin(progress * Math.PI * 4) * 0.1 + 1.5

        if (glitchHologram == null) {
            glitchHologram = createItemHologram(location.clone().add(0.0, bobbingOffset, 0.0), contents.random())
        } else {
            val holo = glitchHologram!!
            moveHologram(holo, location.clone().add(0.0, bobbingOffset, 0.0))

            val glitchChance = 0.1 + progress * 0.6
            if (Random.nextDouble() < glitchChance) {
                updateHologramIcon(holo, contents.random())
            }
        }

        val swirlParticles = (10 + (progress * 30)).toInt()
        val radius = particleRadiusBase + progress * 1.2
        val swirlHeight = 1.2 + progress * 0.5

        for (i in 0 until swirlParticles) {
            val angle = progress * 10 * PI + i * (2 * PI / swirlParticles)
            val x = cos(angle) * radius
            val z = sin(angle) * radius
            val particleLoc = location.clone().add(x, swirlHeight, z)

            world.spawnParticle(Particle.PORTAL, particleLoc, 0, 0.0, 0.0, 0.0)
            if (i % 3 == 0) {
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 0, 0.0, 0.0, 0.0)
            }
        }

        if ((progress * 100).toInt() % 15 == 0) {
            player.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 0.5f, 1.0f + (progress * 0.5f).toFloat())
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)

        val world = location.world ?: return

        runSync {
            rewardHologram = createItemHologram(location.clone().add(0.0, 1.5, 0.0), rewardItem)

            glitchHologram?.let { DHAPI.removeHologram(it.name) }

            world.spawnParticle(Particle.EXPLOSION, location.clone().add(0.0, 1.5, 0.0), 1)
            world.spawnParticle(Particle.ENCHANT, location.clone().add(0.0, 1.5, 0.0), 80, 0.6, 0.6, 0.6, 0.0)
            world.spawnParticle(Particle.FIREWORK, location.clone().add(0.0, 1.5, 0.0), 50, 0.4, 0.4, 0.4, 0.1)

            player.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.4f, 0.4f)
            player.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 0.4f, 0.4f)
        }
    }
}
