package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d

import com.client.github.feature.elytra.ElytraFlightMode
import com.client.github.feature.elytra.ElytraTiming

object Angle : ElytraFlightMode("Angle") {
    public enum class State {
        ACCELERATING,
        DECELERATING,
        GLIDING,
        IDLE
    }

    val yLevelMin = 80.0

    var state: State = State.IDLE

    private fun linearApproach(target: Float, speed: Float) {
        val player = mc?.player ?: return

        val currentPitch = player.getPitch()
        val delta = target - currentPitch
        val step = delta / speed

        player.setPitch(currentPitch + step)
    }

    override fun tick(movementVector: Vec3d) {
        adjustDirection(movementVector)

        val player = mc?.player ?: return
        val pitch = player.getPitch()

        when (state) {
            State.ACCELERATING -> {
                linearApproach(-49f, 10f)

                if (pitch <= -48) state = State.GLIDING
            }
            State.DECELERATING -> {
                player.setPitch(32.5f)

                if (player.getY() <= yLevelMin) state = State.ACCELERATING
            }
            State.GLIDING -> {
                player.setPitch(pitch + 0.5f)

                if (pitch > 32 && pitch < 33) {
                    state = State.IDLE
                }
            }
            State.IDLE -> {
                state = State.DECELERATING
            }
        }
    }

    override fun tick() {
        val movementVector = getRawMovementVector()

        tick(movementVector ?: return)
    }
}
