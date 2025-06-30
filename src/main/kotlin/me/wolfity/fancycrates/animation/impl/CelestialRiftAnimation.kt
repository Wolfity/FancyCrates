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

class CelestialRiftAnimation(crateConfig: CrateConfig) : HologramAnimation("celestial_rift", 5000L, crateConfig) {

    private var hasSpawned = false

    override fun onTick(player: Player, location: Location, progress: Double) {
        val world = location.world ?: return
        val center = location.clone().add(0.0, 1.0, 0.0)
        val sky = center.clone().add(0.0, 5.0, 0.0)

        val swirlRadius = 3 * (1 - progress)
        val swirlCount = 30
        val swirlHeight = 3.0

        repeat(swirlCount) { i ->
            val angle = (i.toDouble() / swirlCount) * Math.PI * 2 + progress * Math.PI * 8
            val x = cos(angle) * swirlRadius
            val z = sin(angle) * swirlRadius
            val y = sin(progress * Math.PI) * swirlHeight

            val particleLoc = center.clone().add(x, y - 1.0, z)
            world.spawnParticle(Particle.DRAGON_BREATH, particleLoc, 0)
        }


        val ringRadius = 1.5
        repeat(20) { i ->
            val angle = i * (2 * Math.PI / 20)
            val x = cos(angle) * ringRadius
            val z = sin(angle) * ringRadius
            world.spawnParticle(Particle.PORTAL, sky.clone().add(x, -2.0, z), 1, 0.0, 0.0, 0.0, 0.0)
        }


        if ((progress * 100).toInt() % 10 == 0) {
            val strikeFrom = center.clone().add((Math.random() - 0.5) * 4, 0.0, (Math.random() - 0.5) * 4)
            world.spawnParticle(Particle.ELECTRIC_SPARK, strikeFrom, 5, 0.1, 0.5, 0.1)
            world.spawnParticle(Particle.SOUL_FIRE_FLAME, strikeFrom, 5, 0.1, 0.1, 0.1)
            player.playSound(strikeFrom, Sound.BLOCK_BEACON_POWER_SELECT, 0.2f, 2.0f)
        }


        if (!hasSpawned && progress >= 0.95) {
            val dropLoc = sky.clone().add(0.0, 1.0, 0.0)

            player.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.3f, 0.4f)
            player.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.3f, 0.4f)
            world.spawnParticle(Particle.FLASH, dropLoc, 3)
            world.spawnParticle(Particle.END_ROD, dropLoc, 50, 0.3, 0.5, 0.3)

            hasSpawned = true
        }
    }

    override fun onEnd(player: Player, location: Location) {
        val world = location.world!!
        val center = location.clone().add(0.0, 1.0, 0.0)


        rewardHologram =
            createItemHologram(center, rewardItem.toItemStack())

        world.spawnParticle(Particle.WITCH, center.clone().add(0.0, 2.0, 0.0), 20, 0.3, 0.5, 0.3, 0.01)
        player.playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, 0.4f, 0.4f)

        super.onEnd(player, location)
    }

}
