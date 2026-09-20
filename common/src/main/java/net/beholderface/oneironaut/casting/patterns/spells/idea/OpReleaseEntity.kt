package net.beholderface.oneironaut.casting.patterns.spells.idea

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import net.beholderface.oneironaut.casting.idea.IdeaEntry
import net.beholderface.oneironaut.casting.idea.IdeaInscriptionManager
import net.beholderface.oneironaut.colorToClosestPigment
import net.beholderface.oneironaut.getIdeaKey
import net.beholderface.oneironaut.platform.OneironautPlatform
import net.minecraft.entity.Entity
import net.minecraft.util.DyeColor
import net.minecraft.util.Util
import ram.talia.hexal.common.entities.WanderingWisp

class OpReleaseEntity : ConstMediaAction {
    override val argc = 2
    override val mediaCost = MediaConstants.CRYSTAL_UNIT

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val key = args.getIdeaKey(0, argc, env)
        val position = args.getVec3(1, argc)
        env.assertVecInRange(position)
        val entry = IdeaInscriptionManager.getEntry(key, env.world)
        val wisp = if (entry == null || entry.type == IdeaEntry.EntryType.IOTA) {
            val out = WanderingWisp(env.world, position)
            if (entry?.type == IdeaEntry.EntryType.IOTA) {
                val iota = entry.payload as Iota
                out.setPigment(colorToClosestPigment(iota.type.color()))
            } else {
                out.setPigment(
                    FrozenPigment(
                        HexItems.DYE_PIGMENTS[DyeColor.values().random()]!!.defaultStack,
                        Util.NIL_UUID
                    )
                )
            }
            OneironautPlatform.setDecorativeWisp(out, true)
            env.world.spawnEntity(out)
            out
        } else {
            null
        }
        if (entry?.type == IdeaEntry.EntryType.ENTITY) {
            val entity = entry.payload as Entity
            //entity.setRemoved(null)
            entity.setPosition(position)
            env.world.spawnEntity(entity)
            IdeaInscriptionManager.eraseEntry(key)
            IdeaInscriptionManager.getServerState(env.world.server).markDirty()
            return entity.asActionResult
        } else if (wisp != null) {
            return wisp.asActionResult
        } else {
            return listOf(GarbageIota())
        }
    }
}