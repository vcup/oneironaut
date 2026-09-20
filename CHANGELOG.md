# Changelog

## 0.5.0 — NeoForge 1.20.1

Oneironaut now builds and runs on **NeoForge 47.1.x for Minecraft 1.20.1** as well as on Fabric, from the
same shared code. Fabric behaviour is unchanged except where noted.

- Added a NeoForge 1.20.1 platform module. Both loaders are built from `common/` through one platform
  seam (packets, teleporting an entity, clearing Hex Casting's brainsweep flag, block render layers,
  dimension sky/fog effects, fluids).
- Removed the unfinished legacy `forge/` module, which was not part of the build.
- Thought slurry now works on NeoForge. A modded fluid block's cached fluid state is filled while the
  block is being constructed, at which point the shared fluid registry is still empty, so the block ended
  up with the *empty* fluid: it neither flowed nor drew anything. The still/flowing singletons are used
  directly, the block state cache is initialised for our own states, and a source reports level 8 so the
  fluid engine treats it as a source instead of a draining flow.
- The fluid's tint is now opaque. Vanilla — and therefore Fabric — ignores the alpha of a fluid tint, but
  Forge/NeoForge feeds it into the fluid vertices, so the old `0x8621C2` was drawn fully transparent
  there. The value now lives in one place, `ThoughtSlurryAppearance`, for both loaders, and the alpha is
  part of it for both; Fabric's rendering is expected to be unchanged, though that has not been checked on
  a Fabric runtime.
- Fixed two client crashes on NeoForge: reading a rift residue's tooltip before any server had started,
  and rendering a shifting pseudoamethyst through a client reference that was never assigned.
- The inactive slipway's particles now start on NeoForge too.
- The optional Create crushing recipe was only gated on Fabric's `fabric:load_conditions`, so on Forge it
  loaded without Create and then failed with "Invalid or unsupported recipe type 'create:crushing'". It now
  carries Forge's `conditions` alongside the Fabric key, so each loader gates it its own way.

## 

- Initial version.
