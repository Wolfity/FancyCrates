package me.wolfity.fancycrates.util

import me.wolfity.developmentutil.util.*
import me.wolfity.fancycrates.commands.CrateCommands.Companion.CRATE_ITEM_DATA_KEY
import me.wolfity.fancycrates.commands.CrateCommands.Companion.CRATE_KEY_DATA_KEY
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.crate.CrateRewardConfig
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

fun CrateRewardConfig.toItemStack(additionalLore: List<Component> = listOf()): ItemStack {
    val combinedLore = (lore?.map { msg -> style(msg) } ?: listOf()) + additionalLore
    return ItemBuilder(item).apply {
        setAmount(amount)
        displayName?.let { setName(style(it)) }
        setLore(combinedLore)
        enchants?.forEach { (enchant, level) -> addEnchant(enchant, level) }
    }.build()
}

fun CrateConfig.toCrateItem(): ItemStack {
    val crateBlock = this.crateBlock
    return buildItem(crateBlock, style(displayName)) {
        addData(ItemDataFlag(CRATE_ITEM_DATA_KEY, name))
    }
}

fun CrateConfig.toKeyItem(amount: Int = 1): ItemStack {
    val keyItem = this.crateKeyMaterial
    return buildItem(keyItem, style("${serialize(style(displayName))} key")) {
        setAmount(amount)
        addData(ItemDataFlag(CRATE_KEY_DATA_KEY, name))
    }
}