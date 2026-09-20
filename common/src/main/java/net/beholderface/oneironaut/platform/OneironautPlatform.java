package net.beholderface.oneironaut.platform;

import at.petrak.hexcasting.common.msgs.IMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

/**
 * The single seam between Oneironaut's shared code and the loader it is running on.
 * <p>
 * Every platform entrypoint must call {@link #install} before {@link #loader()} can be used, i.e.
 * before {@code Oneironaut.initEarly()} and {@code Oneironaut.initLate()} run.
 * <p>
 * The Fabric implementation simply forwards to Hex Casting's Fabric xplat layer. The NeoForge one
 * cannot: Hex Casting's Forge {@code ForgePacketHandler} only registers Hex's own message classes on
 * its {@code SimpleChannel}, so sending one of Oneironaut's {@link IMessage}s through
 * {@code IXplatAbstractions#sendPacketNear} throws "Unregistered message class" there. NeoForge
 * therefore gets its own channel, and every send site goes through this class instead.
 */
public final class OneironautPlatform {
    private OneironautPlatform() {
    }

    /** Services that must be installed on both the client and the dedicated server. */
    public interface Loader {
        /** Sends one of Oneironaut's own {@link IMessage}s to a single player. */
        void sendToPlayer(ServerPlayerEntity player, IMessage message);

        /** Sends one of Oneironaut's own {@link IMessage}s to everyone near a point. */
        void sendNear(Vec3d pos, double radius, ServerWorld world, IMessage message);

        /** Moves an entity into another dimension, returning the entity that ends up there. */
        Entity teleport(Entity entity, ServerWorld destination, TeleportTarget target);

        /** Clears Hex Casting's brainswept flag on a mob and syncs the change to clients. */
        void clearBrainsweep(MobEntity mob);

        /** Whether this wisp carries Oneironaut's decorative marker. */
        boolean isDecorativeWisp(Entity wisp);

        /** Sets Oneironaut's decorative marker on a wisp. */
        void setDecorativeWisp(Entity wisp, boolean decorative);
    }

    private static volatile Loader loader;

    public static void install(Loader loader) {
        OneironautPlatform.loader = loader;
    }

    private static Loader loader() {
        Loader current = loader;
        if (current == null) {
            throw new IllegalStateException("Oneironaut's loader services were never installed; the "
                    + "platform entrypoint must call OneironautPlatform.install(...) during mod construction");
        }
        return current;
    }

    public static void sendToPlayer(ServerPlayerEntity player, IMessage message) {
        loader().sendToPlayer(player, message);
    }

    public static void sendNear(Vec3d pos, double radius, ServerWorld world, IMessage message) {
        loader().sendNear(pos, radius, world, message);
    }

    public static Entity teleport(Entity entity, ServerWorld destination, TeleportTarget target) {
        return loader().teleport(entity, destination, target);
    }

    public static void clearBrainsweep(MobEntity mob) {
        loader().clearBrainsweep(mob);
    }

    public static boolean isDecorativeWisp(Entity wisp) {
        return loader().isDecorativeWisp(wisp);
    }

    public static void setDecorativeWisp(Entity wisp, boolean decorative) {
        loader().setDecorativeWisp(wisp, decorative);
    }
}
