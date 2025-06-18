package me.wolfity.fancycrates.commands

import me.wolfity.developmentutil.util.sendStyled
import me.wolfity.fancycrates.commands.params.AnimationParameter
import me.wolfity.fancycrates.commands.params.CrateParameter
import me.wolfity.fancycrates.plugin
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command

class TestAnimationCommand {


    @Command("displayanimation")
    fun onDisplay(sender: Player, animation: AnimationParameter, crateKey: CrateParameter) {
        val crate = plugin.crateManager.getCrateById(crateKey.key)
        if (crate == null) {
            sender.sendStyled("<red>Please choose a valid crate to display this animation with! ${plugin.crateManager.getAllCrates()}")
            return
        }
        val animation = plugin.crateAnimationRegistry.get(animation.key, crate)

        if (animation == null) {
            sender.sendStyled("<red>Invalid Animation! ${plugin.crateAnimationRegistry.getAll()}")
            return
        }

        animation.startAnimation(sender, sender.location)
    }

}