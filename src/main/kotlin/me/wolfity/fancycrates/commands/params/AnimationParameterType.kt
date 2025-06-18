package me.wolfity.fancycrates.commands.params

import me.wolfity.fancycrates.plugin
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream

data class AnimationParameter(val key: String)

class AnimationParameterType : ParameterType<BukkitCommandActor, AnimationParameter> {
    override fun parse(p0: MutableStringStream, p1: ExecutionContext<BukkitCommandActor>): AnimationParameter {
        return AnimationParameter(p0.readString())
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { _ ->
            plugin.crateAnimationRegistry.getAll()
        }
    }
}