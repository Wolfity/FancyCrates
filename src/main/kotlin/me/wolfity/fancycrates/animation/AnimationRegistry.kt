package me.wolfity.fancycrates.animation

import me.wolfity.fancycrates.animation.impl.*
import me.wolfity.fancycrates.crate.CrateConfig

class AnimationRegistry {

    private val animations: MutableMap<String, (CrateConfig) -> BaseCrateAnimation> = mutableMapOf()

    init {
        register("astral_genesis") { crateConfig -> AstralGenesisAnimation(crateConfig) }
        register("celestial_rift") { crateConfig -> CelestialRiftAnimation(crateConfig) }
        register("chrono_shift") { crateConfig -> ChronoShiftAnimation(crateConfig) }
        register("crystal_boom") { crateConfig -> CrystalBoomAnimation(crateConfig) }
        register("galactic_drop") { crateConfig -> GalacticDropAnimation(crateConfig) }
        register("infinity_collapse") { crateConfig -> InfinityCollapseAnimation(crateConfig) }
        register("quantum_pulse") { crateConfig -> QuantumPulseAnimation(crateConfig) }
        register("temporal_rift") { crateConfig -> TemporalRiftAnimation(crateConfig) }
        register("celestial_nexus") { crateConfig -> CelestialNexusAnimation(crateConfig) }
        register("spin") { crateConfig -> SpinAnimation(crateConfig) }
        register("celestial_rite") { crateConfig -> CelestialRiteAnimation(crateConfig) }
    }

    private fun register(id: String, factory: (CrateConfig) -> BaseCrateAnimation) {
        animations[id.lowercase()] = factory
    }

    fun get(id: String, crateConfig: CrateConfig): BaseCrateAnimation? {
        return animations[id.lowercase()]?.invoke(crateConfig)
    }

    fun getAll() = animations.keys
}
