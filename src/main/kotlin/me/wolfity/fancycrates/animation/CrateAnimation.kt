package me.wolfity.fancycrates.animation

import org.bukkit.Location
import org.bukkit.entity.Player

interface CrateAnimation {
    val duration: Long
    fun startAnimation(player: Player, crateLocation: Location)
}
