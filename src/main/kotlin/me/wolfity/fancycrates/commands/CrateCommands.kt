package me.wolfity.fancycrates.commands

import me.wolfity.developmentutil.util.sendStyled
import me.wolfity.developmentutil.util.serialize
import me.wolfity.developmentutil.util.style
import me.wolfity.fancycrates.commands.params.CrateParameter
import me.wolfity.fancycrates.constants.Permissions.ADMIN_PERMISSION
import me.wolfity.fancycrates.plugin
import me.wolfity.fancycrates.util.toCrateItem
import me.wolfity.fancycrates.util.toKeyItem
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Named
import revxrsal.commands.annotation.Optional
import revxrsal.commands.annotation.Range
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.bukkit.annotation.CommandPermission
import revxrsal.commands.command.CommandActor

class CrateCommands {

    companion object {
        const val CRATE_ITEM_DATA_KEY: String = "fancycrate"
        const val CRATE_KEY_DATA_KEY: String = "fancycrate_key"
    }

    @Command("fancycrates givekey", "fancycrate givekey", "fc givekey")
    @CommandPermission(ADMIN_PERMISSION)
    fun onCrateKey(
        sender: BukkitCommandActor,
        @Named("crate") crateName: CrateParameter,
        @Named("amount") @Range(min = 1.0) amountOfKeys: Int,
        @Optional @Named("player") target: Player?
    ) {
        val finalTarget = target ?: if (sender.isPlayer) (sender.sender() as Player) else null
        if (finalTarget == null) {
            sender.reply(style("<red>Invalid Receiver"))
            return
        }

        val crate = plugin.crateManager.getCrateById(crateName.key)
        if (crate == null) {
            sender.reply(style("<red>This crate does not exist!"))
            return
        }

        val item = crate.toKeyItem(amountOfKeys)

        sender.reply(style("<green>Gave $amountOfKeys ${serialize(style(crate.displayName))} keys to ${finalTarget.name}"))
        finalTarget.sendStyled("<green>Received $amountOfKeys ${crate.displayName} keys")
        finalTarget.inventory.addItem(item)
    }

    @Command("fancycrate giveall", "fancycrates giveall", "fc giveall")
    @CommandPermission(ADMIN_PERMISSION)
    fun onGiveAll(
        sender: BukkitCommandActor,
        @Named("crate") crateName: CrateParameter,
        @Named("amount") @Range(min = 1.0) amountOfKeys: Int,
    ) {
        val crate = plugin.crateManager.getCrateById(crateName.key)
        if (crate == null) {
            sender.reply(style("<red>This crate does not exist!"))
            return
        }

        val item = crate.toKeyItem(amountOfKeys)

        Bukkit.broadcast(
            style(
                plugin.config.getString("crate-giveall-announcement")!!
                    .replace("{crate}", serialize(style(crate.displayName)))
                    .replace("{amount}", amountOfKeys.toString())
            )
        )

        Bukkit.getOnlinePlayers().forEach { it.inventory.addItem(item) }

    }

    @Command("fancycrates give", "fancycrate give", "fc give")
    @CommandPermission(ADMIN_PERMISSION)
    fun onCrateGive(sender: Player, crate: CrateParameter) {
        val crate = plugin.crateManager.getCrateById(crate.key)
        if (crate == null) {
            sender.sendStyled("<red>This crate does not exist!")
            return
        }

        sender.inventory.addItem(crate.toCrateItem())
    }

}