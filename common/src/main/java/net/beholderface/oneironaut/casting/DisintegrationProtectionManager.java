package net.beholderface.oneironaut.casting;

import at.petrak.hexcasting.api.utils.NBTHelper;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.casting.conceptmodification.ConceptModifier;
import net.beholderface.oneironaut.casting.conceptmodification.ConceptModifierManager;
import net.beholderface.oneironaut.registry.OneironautMiscRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DisintegrationProtectionManager extends PersistentState {

    public static final String ID = Oneironaut.MOD_ID + "_disintegration";
    private final Map<UUID, DisintegrationProtectionEntry> entries = new HashMap<>();
    public static final String TAG_ENTRIES = "entries";

    public DisintegrationProtectionManager(){

    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList list = new NbtList();
        for (DisintegrationProtectionEntry entry : entries.values()){
            list.add(entry.serialize());
        }
        //Oneironaut.LOGGER.info(list.asString());
        NBTHelper.putList(nbt, TAG_ENTRIES, list);
        return nbt;
    }

    public static DisintegrationProtectionManager createFromNbt(NbtCompound nbt){
        DisintegrationProtectionManager manager = new DisintegrationProtectionManager();
        NbtList list = NBTHelper.getList(nbt, TAG_ENTRIES, NbtList.COMPOUND_TYPE);
        if (list == null){
            throw new IllegalStateException("NbtCompound supplied to DisintegrationProtectionManager#createFromNbt did not contain the proper list");
        }
        manager.entries.clear();
        for (NbtElement element : list){
            if (element instanceof NbtCompound compound){
                DisintegrationProtectionEntry entry = DisintegrationProtectionEntry.deserialize(compound);
                manager.entries.put(entry.getUuid(), entry);
            }
        }
        Oneironaut.LOGGER.info("Reconstructed protection map with {} entries", manager.entries.size());
        return manager;
    }

    public static DisintegrationProtectionManager getServerState(MinecraftServer server){
        PersistentStateManager stateManager = Oneironaut.getDeepNoosphere().getPersistentStateManager();
        DisintegrationProtectionManager manager = stateManager.getOrCreate(DisintegrationProtectionManager::createFromNbt,
                DisintegrationProtectionManager::new, ID);
        manager.markDirty();
        return manager;
    }

    public void cleanEntries(){
        var iterator = this.entries.values().iterator();
        DisintegrationProtectionEntry entry;
        int removed = 0;
        while (iterator.hasNext()){
            entry = iterator.next();
            if (entry.isBroken()){
                iterator.remove();
                removed++;
            }
        }
        Oneironaut.LOGGER.info("Disintegration manager found and removed {} dead entries", removed);
    }

    public void removeEntry(DisintegrationProtectionEntry entry){
        this.entries.remove(entry.getUuid());
        this.markDirty();
    }

    public void addEntry(DisintegrationProtectionEntry entry){
        entries.put(entry.getUuid(), entry);
        this.markDirty();
    }

    @Nullable
    public DisintegrationProtectionEntry getProtectionEntry(Vec3d pos){
        var iterator = this.entries.values().iterator();
        DisintegrationProtectionEntry entry;
        while (iterator.hasNext()){
            entry = iterator.next();
            if (!entry.isBroken()){
                //Oneironaut.LOGGER.info("Entry not broken");
                if (entry.canProtect(pos)){
                    //Oneironaut.LOGGER.info("Can protect {}", pos.toString());
                    return entry;
                }/* else {
                    //Oneironaut.LOGGER.info("Cannot protect {}", pos.toString());
                }*/
            } else {
                //Oneironaut.LOGGER.info("Entry broken");
                iterator.remove();
                this.markDirty();
            }
        }
        return null;
    }

    public static class DisintegrationProtectionEntry {
        private final Box bounds;
        private long hits = 0;
        private long durability = 1;
        private final UUID uuid;

        public DisintegrationProtectionEntry(BlockPos cornerA, BlockPos cornerB, long durability){
            this.bounds = new Box(cornerA, cornerB.add(1,1,1));
            this.hits = 0;
            this.durability = durability;
            this.uuid = UUID.randomUUID();
        }

        private DisintegrationProtectionEntry(Box bounds, long hits, long durability, @Nullable UUID uuid){
            this.bounds = bounds;
            this.hits = hits;
            this.durability = durability;
            this.uuid = uuid != null ? uuid : UUID.randomUUID();
        }

        public DisintegrationProtectionEntry indestructible(Box bounds){
            return new DisintegrationProtectionEntry(bounds, -1L, -1L, MiscAPIKt.toUUID(MiscAPIKt.toBlockPos(bounds.getCenter())));
        }

        public Box getBounds(){
            return bounds;
        }

        public boolean canProtect(Vec3d pos){
            return !this.isBroken() && MiscAPIKt.containsPermissive(this.bounds, pos);
        }

        public boolean canProtect(Vec3i pos){
            return canProtect(Vec3d.of(pos));
        }

        public long getHits() {
            return hits != -1 ? hits : Long.MIN_VALUE;
        }

        public long getDurability() {
            return durability != -1 ? durability : Long.MAX_VALUE;
        }

        public UUID getUuid() {
            return uuid;
        }

        private void addHits(long addedHits){
            if (this.hits != -1){
                this.hits += addedHits;
            }
        }

        public boolean hit(long addedHits, Vec3d pos, ServerWorld world){
            boolean startedBroken = this.isBroken();
            this.addHits(addedHits);
            //Oneironaut.LOGGER.info("Entry {} hit for {} points, for a total of {}", this.uuid.toString(), addedHits, this.getHits());
            boolean newlyBroken = (this.isBroken() && !startedBroken);
            if (world != null && world.getServer() != null){
                PlaySoundS2CPacket hitSoundMessage = getHitMessage(pos, world, newlyBroken);
                // ServerWorld#sendToPlayerIfNearby is private in vanilla and was only reachable through
                // Fabric API's access widener, so its body is replicated here rather than approximated
                // with PlayerManager#sendToAround, which would measure from the player's exact
                // coordinates instead of vanilla's block position (a difference of up to ~1.7 blocks at
                // the 32 block cutoff). Radius 32 and the "self world only" check are vanilla's, for a
                // non-persistent (bl == false) message.
                for (ServerPlayerEntity player : world.getPlayers()){
                    if (player.getBlockPos().isWithinDistance(new Vec3d(pos.x, pos.y, pos.z), 32.0)){
                        player.networkHandler.sendPacket(hitSoundMessage);
                    }
                }
            }
            return newlyBroken;
        }

        private static @NotNull PlaySoundS2CPacket getHitMessage(Vec3d pos, ServerWorld world, boolean newlyBroken) {
            if (newlyBroken){
                return new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.BLOCK_GLASS_BREAK), SoundCategory.BLOCKS,
                        pos.x, pos.y, pos.z, 0.5f, 0.2f, world.getSeed());
            } else {
                return new PlaySoundS2CPacket(RegistryEntry.of(SoundEvents.BLOCK_GLASS_HIT), SoundCategory.BLOCKS,
                        pos.x, pos.y, pos.z, 1f, 0.5f, world.getSeed());
            }
        }

        public boolean hit(Vec3d pos, ServerWorld world){
            return this.hit(1, pos, world);
        }

        public boolean isBroken(){
            return this.getHits() >= this.getDurability();
        }

        public static final String TAG_HITS = "hits";
        public static final String TAG_DURABILITY = "durability";
        public static final String TAG_CORNER_1 = "corner1";
        public static final String TAG_CORNER_2 = "corner2";
        public static final String TAG_UUID = "uuid";
        public NbtCompound serialize(){
            NbtCompound nbt = new NbtCompound();
            nbt.putLong(TAG_HITS, this.hits);
            nbt.putLong(TAG_DURABILITY, this.durability);
            nbt.putUuid(TAG_UUID, this.getUuid());
            NBTHelper.putCompound(nbt, TAG_CORNER_1, NbtHelper.fromBlockPos(new BlockPos((int) this.bounds.minX, (int) this.bounds.minY, (int) this.bounds.minZ)));
            NBTHelper.putCompound(nbt, TAG_CORNER_2, NbtHelper.fromBlockPos(new BlockPos((int) this.bounds.maxX, (int) this.bounds.maxY, (int) this.bounds.maxZ)));
            return nbt;
        }

        public static DisintegrationProtectionEntry deserialize(NbtCompound compound){
            long hits = compound.getLong(TAG_HITS);
            NbtCompound compoundA = NBTHelper.getCompound(compound, TAG_CORNER_1);
            NbtCompound compoundB = NBTHelper.getCompound(compound, TAG_CORNER_2);
            if (compoundA == null || compoundB == null){
                throw new IllegalStateException("NbtCompound supplied to DisintegrationProtectionEntry#deserialize did not contain one or both corner tags.");
            }
            BlockPos cornerA = NbtHelper.toBlockPos(compoundA);
            BlockPos cornerB = NbtHelper.toBlockPos(compoundB);
            long durability = compound.getLong(TAG_DURABILITY);
            UUID uuid = compound.getUuid(TAG_UUID);
            return new DisintegrationProtectionEntry(new Box(cornerA, cornerB), hits, durability, uuid);
        }
    }

    //moved here to maybe fix effect registration order issue
    private static DisintegrationProtectionManager.DisintegrationProtectionEntry latestFoundEntry = null;
    public static void handleDisintegrationTick(LivingEntity entity){
        DisintegrationProtectionManager.DisintegrationProtectionEntry entry = latestFoundEntry;
        Vec3d pos = entity.getEyePos();
        if (!entity.getWorld().isClient && !(entity instanceof PlayerEntity player && (player.isCreative() || player.isSpectator()))){
            DisintegrationProtectionManager manager = DisintegrationProtectionManager.getServerState(((ServerWorld)entity.getWorld()).getServer());
            if (entry == null || !entry.canProtect(pos)){
                entry = manager.getProtectionEntry(pos);
            }
            boolean conceptProtected = false;
            if (entity instanceof ServerPlayerEntity player){
                conceptProtected = ConceptModifierManager.getServerState(player.server).hasModifierType(player, ConceptModifier.ModifierType.ANTIEROSION);
            }
            if ((entry != null && !entry.isBroken()) || conceptProtected){
                StatusEffectInstance instance = entity.getStatusEffect(OneironautMiscRegistry.DISINTEGRATION_PROTECTION.get());
                if (instance != null){
                    if (instance.getDuration() <= 40){
                        instance.duration = 100;
                    }
                } else {
                    entity.addStatusEffect(new StatusEffectInstance(OneironautMiscRegistry.DISINTEGRATION_PROTECTION.get(), 100, 0, true, true));
                }
                if (entry != null && !entry.isBroken()){
                    boolean hit = entry.hit(2, pos,(ServerWorld) entity.getWorld());
                    if (!hit){
                        latestFoundEntry = entry;
                    } else {
                        latestFoundEntry = null;
                        manager.removeEntry(entry);
                    }
                }
            }
        }
        Oneironaut.processDisintegration(entity);
    }
}
