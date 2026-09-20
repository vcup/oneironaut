package net.beholderface.oneironaut.neoforge;

import net.beholderface.oneironaut.OneironautConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * NeoForge replacement for the Fabric {@code FabricOneironautConfig}.
 *
 * <p>Fabric loads its settings through Cloth Config / AutoConfig, which is not on the NeoForge
 * classpath. This class instead builds an ordinary {@link ForgeConfigSpec} holding exactly the same
 * settings, with the same names, defaults and bounds, and installs a spec-backed implementation of
 * {@link OneironautConfig.ServerConfigAccess} (plus the empty common/client accessors) so the shared
 * code keeps seeing the same contract.
 *
 * <p>Only {@link #setup()} touches the loader; it must be called from the mod constructor. No config
 * value is read there, so it is safe to call during mod construction — every accessor reads its
 * value live from the spec, at the time the shared code asks for it.
 */
public class NeoForgeOneironautConfig
{

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<Boolean> PLANE_SHIFT_OTHER_PLAYERS;
    private static final ForgeConfigSpec.ConfigValue<Boolean> PLANE_SHIFT_NONLIVING;
    private static final ForgeConfigSpec.ConfigValue<Integer> IDEA_LIFETIME;
    private static final ForgeConfigSpec.ConfigValue<Boolean> SWAP_REQUIRES_NOOSPHERE;
    private static final ForgeConfigSpec.ConfigValue<Boolean> SWAP_SWAPS_BES;
    private static final ForgeConfigSpec.ConfigValue<Boolean> IMPULSE_REDIRECTS_FIREBALL;
    private static final ForgeConfigSpec.ConfigValue<Boolean> INFUSION_ETERNAL_CHORUS;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_OVERWORLD_REFLECTION;
    private static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_NETHER_REFLECTION;
    private static final ForgeConfigSpec.ConfigValue<Double> STALE_IPHIAL_LENIENCE;

    /** Upper bound used by FabricOneironautConfig#validatePostLoad: one IRL week, in ticks. */
    private static final int IDEA_LIFETIME_MAX = 20 * 60 * 60 * 24 * 7;

    static
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Miscellaneous Oneironaut settings.");

        PLANE_SHIFT_OTHER_PLAYERS = builder
                .comment("Allow the Noetic Gateway to teleport other players.")
                .define("planeShiftOtherPlayers", OneironautConfig.ServerConfigAccess.DEFAULT_ALLOW_PLANESHIFT_OTHERS);

        PLANE_SHIFT_NONLIVING = builder
                .comment("Allow the Noetic Gateway to teleport nonliving entities.")
                .define("planeShiftNonliving", OneironautConfig.ServerConfigAccess.DEFAULT_ALLOW_PLANESHIT_NONLIVING);

        IDEA_LIFETIME = builder
                .comment("Idea Inscription expiration time, in ticks.")
                .defineInRange("ideaLifetime", OneironautConfig.ServerConfigAccess.DEFAULT_IDEA_LIFETIME, 1, IDEA_LIFETIME_MAX);

        SWAP_REQUIRES_NOOSPHERE = builder
                .comment("Require at least one end of a space swap to be in the Noosphere.")
                .define("swapRequiresNoosphere", OneironautConfig.ServerConfigAccess.DEFAULT_SWAP_NOOSPHERE);

        SWAP_SWAPS_BES = builder
                .comment("Let space swaps move block entities.")
                .define("swapSwapsBEs", OneironautConfig.ServerConfigAccess.DEFAULT_SWAP_BES);

        IMPULSE_REDIRECTS_FIREBALL = builder
                .comment("Let Impulse redirect fireballs and other explosive projectiles.")
                .define("impulseRedirectsFireball", OneironautConfig.ServerConfigAccess.DEFAULT_REDIRECT_FIREBALL);

        INFUSION_ETERNAL_CHORUS = builder
                .comment("Allow infusing Eternal Chorus into other items.")
                .define("infusionEternalChorus", OneironautConfig.ServerConfigAccess.DEFAULT_INFUSE_CHORUS);

        ALLOW_OVERWORLD_REFLECTION = builder
                .comment("Allow reflecting into the Overworld from the Noosphere.")
                .define("allowOverworldReflection", OneironautConfig.ServerConfigAccess.DEFAULT_OVERWORLD_REFLECTION);

        ALLOW_NETHER_REFLECTION = builder
                .comment("Allow reflecting into the Nether from the Noosphere.")
                .define("allowNetherReflection", OneironautConfig.ServerConfigAccess.DEFAULT_NETHER_REFLECTION);

        STALE_IPHIAL_LENIENCE = builder
                .comment("How much media a stale I Phial may still hold, as a fraction of its capacity.")
                .defineInRange("staleIPhialLenience", (double) OneironautConfig.ServerConfigAccess.DEFAULT_STALE_IPHIAL_LENIENCE, 0.0D, 1.0D);

        SPEC = builder.build();
    }

    private NeoForgeOneironautConfig()
    {
    }

    /**
     * Registers the server config file and installs the spec-backed accessors into
     * {@link OneironautConfig}. Call this once from the mod constructor.
     */
    public static void setup()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SPEC);

        // Neither of these interfaces has any members yet; the dummy implementations still have to go.
        OneironautConfig.setCommon(new CommonConfig());
        OneironautConfig.setClient(new ClientConfig());
        OneironautConfig.setServer(new ServerConfig());
    }

    private static class CommonConfig implements OneironautConfig.CommonConfigAccess
    {
    }

    private static class ClientConfig implements OneironautConfig.ClientConfigAccess
    {
    }

    /**
     * Reads the live values straight out of the registered spec, so config reloads are picked up
     * without any extra bookkeeping.
     */
    private static class ServerConfig implements OneironautConfig.ServerConfigAccess
    {

        @Override
        public boolean getPlaneShiftOtherPlayers()
        {
            return PLANE_SHIFT_OTHER_PLAYERS.get();
        }

        @Override
        public boolean getPlaneShiftNonliving()
        {
            return PLANE_SHIFT_NONLIVING.get();
        }

        @Override
        public int getIdeaLifetime()
        {
            return IDEA_LIFETIME.get();
        }

        @Override
        public boolean getSwapRequiresNoosphere()
        {
            return SWAP_REQUIRES_NOOSPHERE.get();
        }

        @Override
        public boolean getSwapSwapsBEs()
        {
            return SWAP_SWAPS_BES.get();
        }

        @Override
        public boolean getImpulseRedirectsFireball()
        {
            return IMPULSE_REDIRECTS_FIREBALL.get();
        }

        @Override
        public boolean getInfusionEternalChorus()
        {
            return INFUSION_ETERNAL_CHORUS.get();
        }

        @Override
        public boolean getAllowOverworldReflection()
        {
            return ALLOW_OVERWORLD_REFLECTION.get();
        }

        @Override
        public boolean getAllowNetherReflection()
        {
            return ALLOW_NETHER_REFLECTION.get();
        }

        @Override
        public float getStaleIPhialLenience()
        {
            return STALE_IPHIAL_LENIENCE.get().floatValue();
        }
    }
}
