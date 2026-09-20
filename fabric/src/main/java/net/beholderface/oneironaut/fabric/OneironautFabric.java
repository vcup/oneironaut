package net.beholderface.oneironaut.fabric;

import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.fabricmc.api.ModInitializer;

/**
 * This is your loading entrypoint on fabric(-likes), in case you need to initialize
 * something platform-specific.
 * <br/>
 * Since quilt can load fabric mods, you develop for two platforms in one fell swoop.
 * Feel free to check out the <a href="https://github.com/architectury/architectury-templates">Architectury templates</a>
 * if you want to see how to add quilt-specific code.
 */
public class OneironautFabric implements ModInitializer {
    FabricOneironautConfig config = FabricOneironautConfig.setup();
    @Override
    public void onInitialize() {
        // Install the loader half of the platform seam before anything in common/ runs.
        OneironautPlatform.install(new FabricPlatform());
        Oneironaut.initEarly();
        Oneironaut.initLate();
    }
}
