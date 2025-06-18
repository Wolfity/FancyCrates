package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustTransition
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

class CelestialNexusAnimation(crateConfig: CrateConfig) :
    HologramAnimation("supreme_unseen_before", 10_000L, crateConfig) {

    private val orbitHolograms = mutableMapOf<Hologram, Double>()
    private var rewardSpawned = false

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.5, 0.0)

        
        if (orbitHolograms.isEmpty()) {
            val count = 12
            contents.take(count).forEachIndexed { i, item ->
                val angle = 2 * Math.PI * i / count
                val dist = 4.0
                val pos = center.clone().add(cos(angle) * dist, 0.0, sin(angle) * dist)
                val holo = createItemHologram(pos, item)
                orbitHolograms[holo] = angle
            }
        }

        
        if (progress <= 0.5) {
            val swirlSpeed = 5.0
            val maxRadius = 4.0
            orbitHolograms.forEach { (holo, baseAngle) ->
                val radius = maxRadius * progress * 2  
                val angle = baseAngle + progress * swirlSpeed * 2 * Math.PI
                val yOffset = 0.5 * sin(progress * 10 * Math.PI) 
                val newPos = center.clone().add(cos(angle) * radius, yOffset, sin(angle) * radius)
                moveHologram(holo, newPos)

                
                world.spawnParticle(Particle.PORTAL, newPos, 1, 0.0, 0.1, 0.0, 0.05)
                if ((progress * 1000).toInt() % 20 == 0) {
                    player.playSound(newPos, Sound.AMBIENT_CAVE, 0.1f, 0.7f + (progress * 0.5).toFloat())
                }
            }
            
            world.spawnParticle(
                Particle.DUST_COLOR_TRANSITION,
                center,
                5,
                1.5, 1.5, 1.5,
                DustTransition(Color.BLACK, Color.PURPLE, 1f)
            )
        }

        
        if (progress in 0.5..0.8) {
            val contractProgress = (progress - 0.5) / 0.3
            val swirlSpeed = 10.0
            val maxRadius = 4.0
            orbitHolograms.forEach { (holo, baseAngle) ->
                val radius = maxRadius * (1 - contractProgress)
                val angle = baseAngle + contractProgress * swirlSpeed * 2 * Math.PI
                val yOffset = 0.7 * sin(contractProgress * 15 * Math.PI)
                val newPos = center.clone().add(cos(angle) * radius, yOffset, sin(angle) * radius)
                moveHologram(holo, newPos)

                
                world.spawnParticle(Particle.FLAME, newPos, 1, 0.0, 0.1, 0.0)
                world.spawnParticle(Particle.SOUL, newPos.clone().add(0.0, 0.3, 0.0), 1, 0.0, 0.0, 0.0, 0.05)
            }
            
            if ((progress * 100).toInt() % 10 == 0) {
                player.playSound(center, Sound.ENTITY_WITHER_AMBIENT, 0.4f, 0.5f + contractProgress.toFloat())
            }
        }

        
        if (!rewardSpawned && progress >= 0.8) {
            rewardSpawned = true
            
            orbitHolograms.forEach { (holo, _) ->
                moveHologram(holo, center)
            }
            
            world.spawnParticle(Particle.EXPLOSION, center, 5)
            player.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1f, 0.6f)
            player.playSound(center, Sound.ENTITY_WITHER_BREAK_BLOCK, 0.8f, 0.9f)
        }

        
        if (rewardSpawned) {
            val pulse = (sin(progress * 20 * Math.PI) + 1) / 2
            val glowPos = center.clone().add(0.0, 1.0, 0.0)
            world.spawnParticle(
                Particle.DUST_COLOR_TRANSITION,
                glowPos,
                5,
                0.3, 0.3, 0.3,
                DustTransition(Color.PURPLE, Color.WHITE, pulse.toFloat())
            )
            if ((progress * 100).toInt() % 10 == 0) {
                player.playSound(glowPos, Sound.BLOCK_BEACON_ACTIVATE, 0.3f, 1.5f)
            }
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.0, 0.0)

        rewardHologram = createItemHologram(center, rewardItem)

        orbitHolograms.keys.forEach { holo ->
            DHAPI.removeHologram(holo.name)
        }
        orbitHolograms.clear()

        
        world.strikeLightningEffect(center)
        player.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 0.5f)
    }
}
