package net.beholderface.oneironaut.neoforge;

import net.beholderface.oneironaut.OneironautClient;
import net.beholderface.oneironaut.platform.OneironautClientHooks;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * NeoForge client loading entrypoint.
 */
public class OneironautClientNeoForge {
    public static void init(FMLClientSetupEvent event) {
        OneironautClientHooks.install(new NeoForgeClientHooks());
        // Entity ids are reused between sessions, so the client-side decorative-wisp mirror must not
        // survive a disconnect.
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut loggedOut) ->
                DecorativeWispFlags.clearClientSide());
        OneironautClient.init();
    }
}
