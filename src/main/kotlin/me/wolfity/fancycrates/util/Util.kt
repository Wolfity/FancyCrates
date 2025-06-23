package me.wolfity.fancycrates.util

import eu.decentsoftware.holograms.api.DHAPI
import me.wolfity.developmentutil.util.miniMessageToLegacy
import me.wolfity.developmentutil.util.serialize
import me.wolfity.developmentutil.util.style
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.crate.CrateLocation
import me.wolfity.fancycrates.plugin
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.math.pow
import kotlin.math.round
import kotlin.random.Random

fun runSync(block: () -> Unit) {
    object : BukkitRunnable() {
        override fun run() {
            block.invoke()
        }
    }.runTask(plugin)
}

fun weightedRandomItem(weightedItems: Map<ItemStack, Int>): ItemStack? {
    if (weightedItems.isEmpty()) return null

    val totalWeight = weightedItems.values.sum()
    if (totalWeight <= 0) return null

    val randomValue = Random.nextInt(totalWeight)
    var cumulativeWeight = 0

    for ((item, weight) in weightedItems) {
        cumulativeWeight += weight
        if (randomValue < cumulativeWeight) {
            return item
        }
    }

    return weightedItems.keys.firstOrNull()
}

fun CrateLocation.addCrateHologram(crateConfig: CrateConfig) {
    val location = Location(Bukkit.getWorld(world), x, y, z)
    val holo =
        DHAPI.createHologram(this.id.toCleanString(), location.clone().add(0.5, crateConfig.crateHologramYLevel, 0.5))
    DHAPI.addHologramLine(
        holo,
        0,
        miniMessageToLegacy(miniMessageToLegacy(serialize(crateConfig.crateHologramTitle)))
    )
}

fun UUID.toCleanString(): String = this.toString().replace("-", "")

fun fromCleanString(clean: String): UUID {
    require(clean.length == 32) { "Invalid UUID string (must be 32 characters): $clean" }

    val dashed = "${clean.substring(0, 8)}-" +
            "${clean.substring(8, 12)}-" +
            "${clean.substring(12, 16)}-" +
            "${clean.substring(16, 20)}-" +
            clean.substring(20)

    return UUID.fromString(dashed)
}

fun Double.roundToDecimals(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}