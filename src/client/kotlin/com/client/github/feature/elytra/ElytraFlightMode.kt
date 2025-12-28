package com.client.github.feature.elytra

import com.client.github.feature.Module

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.Vec3d

abstract class ElytraFlightMode(val name: String) {
    val mod = Module("Elytra", "Elytra flight: $name")
    val mc = MinecraftClient.getInstance()

    fun getMovementVector(): Vec3d? {
        var movementVec = Vec3d.ZERO

        val camera = mc?.gameRenderer?.getCamera() ?: return null

        val pitch = camera.getPitch()
        val yaw = camera.getYaw()

        val straight = Vec3d.fromPolar(pitch, yaw)
        val gay = Vec3d.fromPolar(0f, yaw + 90f)

        if (mc?.options?.forwardKey!!.isPressed()) movementVec = movementVec.add(straight)
        if (mc?.options?.backKey!!.isPressed()) movementVec = movementVec.subtract(straight)
        if (mc?.options?.leftKey!!.isPressed()) movementVec = movementVec.subtract(gay)
        if (mc?.options?.rightKey!!.isPressed()) movementVec = movementVec.add(gay)

        movementVec = movementVec.multiply(1.0, 0.0, 1.0)

        if (mc?.options?.jumpKey!!.isPressed()) movementVec = movementVec.add(0.0, 1.0, 0.0)
        else if (mc?.options?.sneakKey!!.isPressed()) movementVec = movementVec.add(0.0, -1.0, 0.0)

        return movementVec
    }

    abstract fun tick(movementVector: Vec3d)
    abstract fun tick()
}
