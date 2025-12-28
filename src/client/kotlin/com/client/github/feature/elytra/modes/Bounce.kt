package com.client.github.feature.elytra.modes

import kotlin.math.*

import net.minecraft.util.math.Vec3d
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket

import com.client.github.feature.elytra.ElytraFlightMode

object Bounce : ElytraFlightMode("Bounce") {
    val MAX_SPEED = 0.4

    override fun tick(movementVector: Vec3d) {
        /**
         * Calculate xz velocity
         */

        val player = mc?.player ?: return
        val velocity = player.getVelocity()

        val velSqrd = velocity.x * velocity.x + velocity.z * velocity.z
        val vel = sqrt(velSqrd)

        val elytraAngle = atan2(movementVector.z, movementVector.x)
        val maxSpeed = max(MAX_SPEED, vel)

        /**
         * Project movement vector onto xz plane
         */

        val newXZ = Vec3d(
            cos(elytraAngle) * maxSpeed,
            velocity.y,
            sin(elytraAngle) * maxSpeed
        )

        /**
         * Check whether we're about to jump
         */

        mc.options.jumpKey.setPressed(true)
        mc.options.sprintKey.setPressed(true)

        if (!player.isFallFlying() && !player.isOnGround()) {
            player.startFallFlying()
            mc?.networkHandler?.sendPacket(ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING))
        }

        /**
         * Set projected velocity
         */

        player.setVelocity(newXZ)
    }

    override fun tick() {
        val movementVector = getMovementVector()

        tick(movementVector ?: return)
    }
}
