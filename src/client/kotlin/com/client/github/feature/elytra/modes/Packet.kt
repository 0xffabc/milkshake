package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d

import com.client.github.feature.elytra.ElytraFlightMode

object Packet : ElytraFlightMode("Packet") {
    override fun tick(movementVector: Vec3d) {
        mc?.player?.setVelocity(movementVector)
    }

    override fun tick() {
        val movementVector = getMovementVector()

        mc?.player?.setVelocity(movementVector)
    }
}
