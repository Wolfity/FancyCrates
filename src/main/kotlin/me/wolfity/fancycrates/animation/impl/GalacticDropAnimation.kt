package me.wolfity.fancycrates.animation.impl


import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.random.Random

class GalacticDropAnimation(crateConfig: CrateConfig) : HologramAnimation("galactic_drop", 3000L, crateConfig) {

    private var fallHologram: Hologram? = null
    private val startHeight = 15.0

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return

        val currentY = location.y + startHeight * (1.0 - progress)
        val dropPos = location.clone().add(0.0, currentY - location.y, 0.0)

        if (fallHologram == null) {
            fallHologram = createItemHologram(dropPos, rewardItem)
        } else {
            moveHologram(fallHologram!!, dropPos)
        }



        repeat(max(3, (progress * 20).toInt())) {
            world.spawnParticle(
                Particle.FLAME, dropPos.clone().add(randomOffset(), randomOffset(), randomOffset()),
                0, 0.0, -0.1, 0.0
            )
            world.spawnParticle(Particle.LARGE_SMOKE, dropPos, 0, 0.0, 0.1, 0.0)
        }


        if ((progress * 100).toInt() % 10 == 0) {
            val pitch = 0.5f + progress.toFloat()
            player.playSound(dropPos, Sound.ENTITY_BLAZE_AMBIENT, 0.6f, pitch)
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
        val world = location.world ?: return


        fallHologram?.let { DHAPI.removeHologram(it.name) }

        rewardHologram = createItemHologram(location.clone().add(0.0, 1.0, 0.0), rewardItem)

        repeat(3) {
            world.spawnParticle(Particle.CRIT, location.clone().add(0.0, 2.0, 0.0), 20, 0.5, 0.5, 0.5, 0.0)
            world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 0.4f, 1.0f + it * 0.1f)
        }

        player.playSound(location, Sound.ENTITY_WITHER_SPAWN, 0.4f, 0.8f)

        holograms.add(rewardHologram!!)
    }

    private fun randomOffset() = Random.nextDouble(-0.2, 0.2)
}
