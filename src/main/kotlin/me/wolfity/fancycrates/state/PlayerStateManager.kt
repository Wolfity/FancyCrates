package me.wolfity.fancycrates.state

import java.util.*
import java.util.concurrent.ConcurrentHashMap

class PlayerStateManager {

    private val playerStates = ConcurrentHashMap<UUID, PlayerState>()

    fun loadPlayer(uuid: UUID) {
        this.playerStates[uuid] = PlayerState()
    }

    fun unloadPlayer(uuid: UUID) {
        this.playerStates.remove(uuid)
    }

    fun isOpeningCrate(uuid: UUID) = playerStates.getOrPut(uuid) { PlayerState() }.isOpeningCrate

    fun setOpeningCrate(uuid: UUID, bool: Boolean) {
        this.playerStates.getOrPut(uuid) { PlayerState(bool) }.isOpeningCrate = bool
    }

}