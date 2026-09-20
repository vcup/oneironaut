# Oneironaut

An addon for Hex Casting centered around exploration and use of the noosphere.

## Building

Both platform jars are built from this repository with the Gradle wrapper (Gradle 8.12, Architectury
Loom 1.9) and a **JDK 17**: set `JAVA_HOME` to a JDK 17, since the build resolves a Java 17 toolchain.

```
gradlew.bat build                  # both platforms
gradlew.bat :neoforge:remapJar     # NeoForge 1.20.1 only
gradlew.bat :fabric:remapJar       # Fabric 1.20.1 only
```

The jars land in `fabric/build/libs/` and `neoforge/build/libs/`. The NeoForge module is built against
`net.neoforged:forge:1.20.1-47.1.106`; on Minecraft 1.20.1 NeoForge still uses the `net.minecraftforge.*`
package names and the `forge` mod id.
