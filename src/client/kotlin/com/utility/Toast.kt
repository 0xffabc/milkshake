package com.client.github.utility

import net.minecraft.client.toast.SystemToast
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

class Toast(val title: String, val message: String, val type: SystemToast.Type) {
    init {
        val minecraft = MinecraftClient.getInstance()

        val titleText = Text.literal(title)
        val messageText = Text.literal(message)

        minecraft.toastManager.add(SystemToast.create(minecraft, type, titleText, messageText))
    }
}
