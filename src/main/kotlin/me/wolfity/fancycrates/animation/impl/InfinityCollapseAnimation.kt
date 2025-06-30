package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.util.toItemStack
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class InfinityCollapseAnimation(crateConfig: CrateConfig) : HologramAnimation("infinity_collapse", 3500L, crateConfig) {

    private val hologramMap = mutableMapOf<Hologram, Double>()

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.0, 0.0)

        if (hologramMap.isEmpty()) {
            contents.take(5).forEachIndexed { i, item ->
                val angle = 2 * PI * i / contents.size
                val dist = 2.5
                val pos = center.clone().add(cos(angle) * dist, 0.3, sin(angle) * dist)
                val holo = createItemHologram(pos, item.toItemStack())
                hologramMap[holo] = angle
            }
        }

        hologramMap.forEach { (holo, baseAngle) ->
            val radius = 2.5 * (1 - progress)
            val yOffset = 0.3 + Random.nextDouble(-0.1, 0.1)
            val angle = baseAngle + progress * 8
            val x = cos(angle) * radius
            val z = sin(angle) * radius
            moveHologram(holo, center.clone().add(x, yOffset, z))

            world.spawnParticle(Particle.PORTAL, center.clone().add(x, yOffset, z), 0, 0.0, 0.1, 0.0)
        }

        if ((progress * 100).toInt() % 15 == 0) {
            player.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5f, 0.5f + progress.toFloat())
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
        val world = location.world ?: return

        hologramMap.forEach { (holo, _) ->
            DHAPI.removeHologram(holo.name)
        }

        rewardHologram = createItemHologram(location.clone().add(0.0, 1.8, 0.0), rewardItem.toItemStack())

        world.spawnParticle(Particle.EXPLOSION, location.clone().add(0.0, 1.0, 0.0), 1)
        player.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.4f, 0.8f)
    }
}
