package com.client.github.milkshake.mixin

import net.minecraft.client.toast.SystemToast
import net.minecraft.client.sound.MusicTracker
import net.minecraft.util.Identifier
import net.minecraft.sound.MusicSound
import net.minecraft.client.sound.SoundInstance

import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

import com.client.github.utility.Toast
import com.client.github.feature.Module

@Mixin(MusicTracker::class)
abstract class MusicTrackerMixin {
    fun mapResource(resource: String): String {
        return when (resource.substringAfterLast("/")) {
            "calm1.ogg" -> "C418 - Minecraft"
            "calm2.ogg" -> "C418 - Clark"
            "calm3.ogg" -> "C418 - Sweden"
            "hal1.ogg" -> "C418 - Subwoofer Lullaby"
            "hal2.ogg" -> "C418 - Living Mice"
            "hal3.ogg" -> "C418 - Haggstrom"
            "hal4.ogg" -> "C418 - Danny"
            "nuance1.ogg" -> "C418 - Key"
            "nuance2.ogg" -> "C418 - Oxygene"
            "piano1.ogg" -> "C418 - Dry Hands"
            "piano2.ogg" -> "C418 - Wet Hands"
            "piano3.ogg" -> "C418 - Mice on Venus"
            "a_familiar_room.ogg" -> "Aaron Cherof - A Familiar Room"
            "an_ordinary_day.ogg" -> "Aaron Cherof - An Ordinary Day"
            "ancestry.ogg" -> "Lena Raine - Ancestry"
            "bromeliad.ogg" -> "Aaron Cherof - Bromeliad"
            "comforting_memories.ogg" -> "Lena Raine - Comforting Memories"
            "crescent_dunes.ogg" -> "Kumi Tanioka - Crescent Dunes"
            "echo_in_the_wind.ogg" -> "Kumi Tanioka - Echo in the Wind"
            "floating_dream.ogg" -> "Kumi Tanioka - Floating Dream"
            "infinite_amethyst.ogg" -> "Lena Raine - Infinite Amethyst"
            "left_to_bloom.ogg" -> "Lena Raine - Left to Bloom"
            "one_more_day.ogg" -> "Lena Raine - One More Day"
            "stand_tall.ogg" -> "Lena Raine - Stand Tall"
            "wending.ogg" -> "Lena Raine - Wending"
            "creative1.ogg" -> "C418 - Mutation"
            "creative2.ogg" -> "C418 - Warmth"
            "creative3.ogg" -> "C418 - Floating Trees"
            "creative4.ogg" -> "C418 - Aria Math"
            "creative5.ogg" -> "C418 - Kyoto"
            "creative6.ogg" -> "C418 - Ballad of the Cats"
            "nether1.ogg" -> "C418 - Concrete Halls"
            "nether2.ogg" -> "C418 - Biome Fest"
            "nether3.ogg" -> "C418 - Blind Spots"
            "nether4.ogg" -> "C418 - Dead Voxel"
            "chrysopoeia.ogg" -> "Lena Raine - Chrysopoeia"
            "rubedo.ogg" -> "Lena Raine - Rubedo"
            "so_below.ogg" -> "Lena Raine - So Below"
            "aerie.ogg" -> "Aaron Cherof - Aerie"
            "firebugs.ogg" -> "Lena Raine - Firebugs"
            "labyrinthine.ogg" -> "Aaron Cherof - Labyrinthine"
            "axolotl.ogg" -> "Lena Raine - Axolotl"
            "dragon_fish.ogg" -> "Lena Raine - Dragon Fish"
            "shuniji.ogg" -> "Lena Raine - Shuniji"
            "boss.ogg" -> "C418 - Boss"
            "end.ogg" -> "C418 - End"
            "credits.ogg" -> "C418 - Intro"
            "menu1.ogg" -> "C418 - Mutation"
            "menu2.ogg" -> "C418 - Moog City 2"
            "menu3.ogg" -> "C418 - Beginning 2"
            "menu4.ogg" -> "C418 - Floating Trees"
            "11.ogg" -> "C418 - Eleven"
            "13.ogg" -> "C418 - Thirteen"
            "5.ogg" -> "Samuel Åberg - Five"
            "blocks.ogg" -> "C418 - Blocks"
            "cat.ogg" -> "C418 - Cat"
            "chirp.ogg" -> "C418 - Chirp"
            "far.ogg" -> "C418 - Far"
            "mall.ogg" -> "C418 - Mall"
            "mellohi.ogg" -> "C418 - Mellohi"
            "otherside.ogg" -> "Lena Raine - Otherside"
            "pigstep.ogg" -> "Lena Raine - Pigstep"
            "relic.ogg" -> "Aaron Cherof - Relic"
            "stal.ogg" -> "C418 - Stal"
            "strad.ogg" -> "C418 - Strad"
            "wait.ogg" -> "C418 - Wait"
            "ward.ogg" -> "C418 - Ward"
            else -> resource
        }
    }

    @Unique
    private val mod = Module("Utility", "Music Tracker", true)

    @Unique
    private var lastMusic: Identifier? = null

    @Shadow
    private var field_5574: SoundInstance? = null

    @Inject(method = ["method_18669()V"], at = [At("HEAD")])
    private fun tick(ci: CallbackInfo) {
        if (mod.disabled()) return

        val location = field_5574?.getSound()?.getLocation() ?: return

        if (lastMusic != location) {
            lastMusic = location

            val mappedResource = mapResource(location.toString().replace("minecraft:music/", ""))

            Toast("Playing", "${mappedResource}", SystemToast.Type.TUTORIAL_HINT)
        }
    }
}
