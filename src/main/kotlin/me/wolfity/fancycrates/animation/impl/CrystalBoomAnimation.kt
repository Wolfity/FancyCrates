package me.wolfity.fancycrates.animation.impl

import eu.decentsoftware.holograms.api.DHAPI
import me.wolfity.fancycrates.animation.HologramAnimation
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.toItemStack
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

class CrystalBoomAnimation(crateConfig: CrateConfig) : HologramAnimation("crystal_boom", 4500L, crateConfig) {

    private var playedFinalSound: Boolean = false

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val base = location.clone().add(0.0, 1.0, 0.0)

        val petals = 6
        val height = (1.0 - progress) * 2.5
        val radius = progress

        for (i in 0 until petals) {
            val angle = i * (2 * Math.PI / petals) + progress * 2
            val x = cos(angle) * radius
            val z = sin(angle) * radius
            val y = height

            val pos = base.clone().add(x, y, z)
            world.spawnParticle(Particle.END_ROD, pos, 0)
            world.spawnParticle(Particle.GLOW, pos, 1, 0.05, 0.05, 0.05, 0.01)
        }

        if (progress > 0.9 && !playedFinalSound) {
            playedFinalSound = true
            world.spawnParticle(Particle.FLASH, base.clone().add(0.0, -1.0, 0.0), 10)
            player.playSound(base, Sound.ENTITY_PLAYER_LEVELUP, 0.4f, 0.4f)
        }
    }

    override fun onEnd(player: Player, location: Location) {
        val world = location.world!!
        val center = location.clone().add(0.0, 1.0, 0.0)

        rewardHologram = createItemHologram(center, rewardItem.toItemStack())

        player.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.4f, 0.4f)
        world.spawnParticle(Particle.END_ROD, center.clone().add(0.0, 1.5, 0.0), 30, 0.2, 0.5, 0.2)

        super.onEnd(player, location)
    }
}
