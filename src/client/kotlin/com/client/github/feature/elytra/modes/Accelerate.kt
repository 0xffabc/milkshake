package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d

import com.client.github.feature.elytra.ElytraFlightMode

object Accelerate : ElytraFlightMode("Accelerate") {
    val SPEED = 0.05

    override fun tick(movementVector: Vec3d) {
        mc?.player?.addVelocity(movementVector.multiply(SPEED))
    }

    override fun tick() {
        val movementVector = getMovementVector()

        mc?.player?.addVelocity(movementVector?.multiply(SPEED) ?: return)
    }
}
