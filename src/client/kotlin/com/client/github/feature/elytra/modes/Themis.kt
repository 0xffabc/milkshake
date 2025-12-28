package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d

import com.client.github.feature.elytra.ElytraFlightMode
import com.client.github.feature.elytra.ElytraTiming

object Themis : ElytraFlightMode("Themis") {
    public enum class State {
        ACCELERATING,
        DECELERATING,
        IDLE
    }

    val themisYLevelMax = 250.0
    val themisYLevelMin = 80.0
    val themisFlightYVel = 10.0

    var state: State = State.IDLE

    override fun tick(movementVector: Vec3d) {
        val player = mc?.player ?: return

        when (state) {
            State.ACCELERATING -> {
                ElytraTiming.quit()

                player.setVelocity(movementVector.add(0.0, themisFlightYVel, 0.0))

                if (player.getY() >= themisYLevelMax) state = State.DECELERATING
            }
            State.DECELERATING -> {
                ElytraTiming.enter()

                if (player.getY() <= themisYLevelMin) state = State.ACCELERATING
            }
            State.IDLE -> {
                state = State.ACCELERATING
            }
        }
    }

    override fun tick() {
        val movementVector = getMovementVector()

        tick(movementVector ?: return)
    }
}
