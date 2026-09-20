package net.beholderface.oneironaut;

import at.petrak.hexcasting.common.lib.HexDamageTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Helpers for building damage sources without leaning on Fabric API.
 * <p>
 * {@code DamageSources#create(RegistryKey)} is <b>private</b> in vanilla; Fabric API only makes it
 * reachable through its access widener. NeoForge has no such widener for that member, so the shared
 * code builds the source from the public {@link DamageSource} constructor instead. This behaves the
 * same: {@code create} is just {@code new DamageSource(registry.getEntry(key).orElseThrow())}.
 */
public final class OneironautDamage {
    private OneironautDamage() {
    }

    /** Hex Casting's overcast damage, attributed to nothing. */
    public static DamageSource overcast(Entity context) {
        Registry<DamageType> registry = context.getWorld().getRegistryManager().get(RegistryKeys.DAMAGE_TYPE);
        RegistryEntry<DamageType> entry = registry.getEntry(HexDamageTypes.OVERCAST)
                .orElseThrow(() -> new IllegalStateException("Damage type hexcasting:overcast is not registered"));
        return new DamageSource(entry);
    }
}
