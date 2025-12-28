package com.client.github.feature.elytra.modes

import net.minecraft.util.math.Vec3d
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.FireworkRocketItem
import net.minecraft.util.Hand
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket

import com.client.github.feature.elytra.ElytraFlightMode

object Firework : ElytraFlightMode("Firework") {
    private var tickCooldown = 0

    override fun tick(movementVector: Vec3d) {
        val player = mc.player ?: return
        val offhand = player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT)
        val item = offhand.getItem()
        val vel = player.getVelocity().length()

        if (
            item is FireworkRocketItem &&
            player.isFallFlying() &&
            vel < 1.67 &&
            tickCooldown <= 0
        ) {
            mc.networkHandler?.sendPacket(
                PlayerInteractItemC2SPacket(Hand.OFF_HAND, 0)
            )

            player.swingHand(Hand.OFF_HAND)

            item.use(player.world, player, Hand.OFF_HAND)

            tickCooldown = 5
        }

        tickCooldown--
    }

    override fun tick() {
        val movementVector = getMovementVector()

        tick(movementVector ?: return)
    }
}
