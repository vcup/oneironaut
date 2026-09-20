package net.beholderface.oneironaut.neoforge;

import net.beholderface.oneironaut.neoforge.net.DecorativeWispPacket;
import net.beholderface.oneironaut.neoforge.net.NeoForgePacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Storage for Oneironaut's "this wisp is purely decorative" marker on NeoForge.
 * <p>
 * Fabric keeps this in a Cardinal Components entity component that syncs itself. Cardinal Components'
 * Fabric build is the only one the hexisle pack ships (it runs under Sinytra Connector), so a native
 * NeoForge mod cannot use it; the flag is kept here instead and mirrored to clients with
 * {@link DecorativeWispPacket}.
 * <p>
 * The server side is authoritative and lives in the entity's persistent data so it survives a reload.
 * The client side only needs entity ids, because the read side is a mixin that has the entity itself.
 */
public final class DecorativeWispFlags {
    private static final String NBT_KEY = "oneironaut:decorative";
    private static final Set<Integer> CLIENT_SIDE = ConcurrentHashMap.newKeySet();

    private DecorativeWispFlags() {
    }

    public static boolean isDecorative(Entity wisp) {
        if (wisp.getWorld() != null && wisp.getWorld().isClient()) {
            return CLIENT_SIDE.contains(wisp.getId());
        }
        return wisp.getPersistentData().getBoolean(NBT_KEY);
    }

    public static void setDecorative(Entity wisp, boolean decorative) {
        wisp.getPersistentData().putBoolean(NBT_KEY, decorative);
        if (wisp.getWorld() instanceof ServerWorld serverWorld) {
            NeoForgePacketHandler.sendNear(wisp.getPos(), 256.0, serverWorld,
                    new DecorativeWispPacket(wisp.getId(), decorative));
        } else {
            setClientSide(wisp.getId(), decorative);
        }
    }

    /** Called from {@link DecorativeWispPacket}'s client handler. */
    public static void setClientSide(int entityId, boolean decorative) {
        if (decorative) {
            CLIENT_SIDE.add(entityId);
        } else {
            CLIENT_SIDE.remove(entityId);
        }
    }

    /**
     * Drops the client-side mirror. Entity ids are handed out per session from a low counter, so
     * without this an ordinary wisp in the next world could reuse an id that is still in the set and
     * be treated as decorative. Called when the client disconnects.
     */
    public static void clearClientSide() {
        CLIENT_SIDE.clear();
    }
}
