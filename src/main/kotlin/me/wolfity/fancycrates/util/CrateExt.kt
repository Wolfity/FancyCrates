package me.wolfity.fancycrates.util

import me.wolfity.developmentutil.util.*
import me.wolfity.fancycrates.commands.CrateCommands.Companion.CRATE_ITEM_DATA_KEY
import me.wolfity.fancycrates.commands.CrateCommands.Companion.CRATE_KEY_DATA_KEY
import me.wolfity.fancycrates.crate.CrateConfig
import me.wolfity.fancycrates.crate.CrateRewardConfig
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

fun CrateRewardConfig.toItemStack(): ItemStack {
    return ItemBuilder(item).apply {
        setAmount(amount)
        displayName?.let { setName(style(it)) }
        lore?.let { setLore(it.map { msg -> style(msg) }) }
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