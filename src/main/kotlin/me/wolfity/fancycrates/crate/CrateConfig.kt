package me.wolfity.fancycrates.crate

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import java.util.*

data class CrateConfig(
    val name: String,
    val displayName: String,
    val crateKeyMaterial: Material,
    val animationId: String,
    val crateBlock: Material,
    val rewards: List<CrateRewardConfig>,
    val crateHologramTitle: Component,
    val crateHologramYLevel: Double,
    val cratePreviewGuiTitle: Component
)

data class CrateRewardConfig(
    val item: Material,
    val amount: Int,
    val weight: Int,
    val displayName: String? = null,
    val lore: List<String>? = null,
    val enchants: Map<Enchantment, Int>? = null
)

data class CrateLocation(
    val id: UUID,
    val crateId: String,
    val world: String,
    val x: Double,
    val y: Double,
    val z: Double,
    val blockFace: BlockFace?
)