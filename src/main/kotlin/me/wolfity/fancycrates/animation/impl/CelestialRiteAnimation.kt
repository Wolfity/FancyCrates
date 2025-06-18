package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
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

class CelestialRiteAnimation(crateConfig: CrateConfig) : HologramAnimation("celestial_rite", 7000L, crateConfig) {

    private var summoned = false
    private var glyphHolograms = mutableListOf<String>()

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.4, 0.0)

        
        if (progress < 0.3) {
            val radius = 2.5 - progress * 1.5
            val stars = 12
            repeat(stars) {
                val angle = 2 * PI * it / stars + progress * 10
                val x = cos(angle) * radius
                val z = sin(angle) * radius
                val pos = center.clone().add(x, sin(progress * PI * 2) * 0.3, z)

                world.spawnParticle(Particle.END_ROD, pos, 1, 0.0, 0.01, 0.0, 0.01)
                world.spawnParticle(Particle.GLOW, pos, 0)
            }

            if ((progress * 100).toInt() % 10 == 0) {
                player.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_HIT, 0.3f, 1.5f)
            }
        }

        
        if (progress in 0.3..0.6) {
            if (glyphHolograms.isEmpty()) {
                val symbols = listOf("✧", "☽", "✦", "☼", "☯")
                symbols.forEachIndexed { i, symbol ->
                    val glyphLoc = center.clone().add(cos(i * 2 * PI / symbols.size) * 1.5, 0.1, sin(i * 2 * PI / symbols.size) * 1.5)
                    val holo = DHAPI.createHologram(generateHologramName(), glyphLoc)
                    DHAPI.addHologramLine(holo, symbol)
                    glyphHolograms.add(holo.name)
                    holograms.add(holo)
                }
            }

            glyphHolograms.forEachIndexed { i, name ->
                val hologram = DHAPI.getHologram(name) ?: return@forEachIndexed
                val baseAngle = i * 2 * PI / glyphHolograms.size
                val angle = baseAngle + progress * 10
                val radius = 1.5
                val x = cos(angle) * radius
                val z = sin(angle) * radius
                val y = 0.3 + sin(progress * 6 + i) * 0.2
                moveHologram(hologram, center.clone().add(x, y, z))
            }

            world.spawnParticle(Particle.WITCH, center, 3, 0.4, 0.4, 0.4, 0.01)
            player.playSound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.3f, 1.0f + progress.toFloat())
        }

        if (!summoned && progress >= 0.6) {
            summoned = true
            player.playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8f, 0.5f)
            world.spawnParticle(Particle.FLASH, center, 1)

            runSync {
                rewardHologram = createItemHologram(center.clone().add(0.0, 0.7, 0.0), rewardItem)
            }
        }

        if (progress >= 0.6 && rewardHologram != null) {
            val descentY = 0.7 - ((progress - 0.6) / 0.4) * 1.2
            moveHologram(rewardHologram!!, center.clone().add(0.0, descentY, 0.0))

            val orbitRadius = 0.6
            repeat(5) {
                val angle = it * (2 * PI / 5) + progress * 15
                val x = cos(angle) * orbitRadius
                val z = sin(angle) * orbitRadius
                val orbitLoc = center.clone().add(x, descentY + 0.2, z)
                world.spawnParticle(Particle.CRIT, orbitLoc, 1, 0.0, 0.0, 0.0, 0.01)
            }

            player.playSound(center, Sound.BLOCK_BEACON_ACTIVATE, 0.4f, 1.2f)
        }
    }

    override fun onEnd(player: Player, location: Location) {
        super.onEnd(player, location)
        player.playSound(location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.6f, 1.5f)
        player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 1.0f)
    }
}
