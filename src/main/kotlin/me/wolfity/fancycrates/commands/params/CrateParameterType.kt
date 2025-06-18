package me.wolfity.fancycrates.commands.params

import me.wolfity.fancycrates.plugin
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream

data class CrateParameter(val key: String)

class CrateParameterType : ParameterType<BukkitCommandActor, CrateParameter> {
    override fun parse(p0: MutableStringStream, p1: ExecutionContext<BukkitCommandActor>): CrateParameter {
        return CrateParameter(p0.readString())
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider { _ ->
            plugin.crateManager.getAllCrates().map { it.name }
        }
    }
}