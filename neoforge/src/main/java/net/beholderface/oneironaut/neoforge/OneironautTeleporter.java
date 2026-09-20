package net.beholderface.oneironaut.neoforge;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * Places a teleported entity exactly where the shared code asked for.
 * <p>
 * Fabric's {@code FabricDimensions.teleport} takes a {@link TeleportTarget} directly; Forge and
 * NeoForge route dimension changes through {@link ITeleporter} instead, so this adapts one to the
 * other. Portals are not involved, so {@code isVanilla} is always false.
 */
public class OneironautTeleporter implements ITeleporter {
    private final TeleportTarget target;

    public OneironautTeleporter(TeleportTarget target) {
        this.target = target;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destinationWorld,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity moved = repositionEntity.apply(false);
        moved.refreshPositionAndAngles(target.position.x, target.position.y, target.position.z,
                target.yaw, target.pitch);
        moved.setVelocity(target.velocity);
        moved.velocityModified = true;
        return moved;
    }
}
