package it.escanortargaryen.roadsideshop.managers;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LibbyManager {
    private BukkitLibraryManager bukkitLibraryManager;

    private String mojangMappedArtifactId = "commandapi-paper-shade";
    private String artifactId = "commandapi-spigot-shade";
    private String version = "11.2.0";

    private String checksum = "Kx6c7DPWPyFkrmlEYq1hj6VeQm4BbBBMbthKI1pHLes=";
    private String checksumMappedArtifactId = "F6KljlYn01+rt6JIKJPOKmWI++d46LgeOd8e6dw6gIM=";

    public static final String MINECRAFT_VERSION = getMinecraftVersion();

    public static final int MAJOR_VERSION;

    public static final int VERSION;

    public static final int MINOR_VERSION;

    private static final boolean IS_PAPER = classExists("io.papermc.paper.advancement.AdvancementDisplay");


    static {
        var splitted = MINECRAFT_VERSION.split("\\.");

        MAJOR_VERSION = Integer.parseInt(splitted[0]);
        VERSION = Integer.parseInt(splitted[1]);

        if (splitted.length > 2) {
            MINOR_VERSION = Integer.parseInt(splitted[2]);
        } else {
            MINOR_VERSION = 0;
        }
    }

    private static String getMinecraftVersion() {
        try {
            // Paper-only method
            return (String) Bukkit.class.getDeclaredMethod("getMinecraftVersion").invoke(null);
        } catch (ReflectiveOperationException e) {
            return Bukkit.getBukkitVersion().split("-")[0];
        }
    }


    public static boolean isMojangMapped() {
        // Load the Mojang mapped CommandAPI on Paper 1.20.6+ as a workaround for https://github.com/PaperMC/Paper/issues/10713
        return IS_PAPER && (MAJOR_VERSION >= 26 || VERSION > 20 || (VERSION == 20 && MINOR_VERSION >= 6));
    }

    public static boolean classExists(@NotNull String className) {
        Objects.requireNonNull(className, "ClassName cannot be null.");
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public String getArtifactId() {
        String artifactId = isMojangMapped() ? this.mojangMappedArtifactId : this.artifactId;
        return artifactId;
    }

    public String getChecksum() {
        String checksum = isMojangMapped() ? this.checksumMappedArtifactId : this.checksum;
        return checksum;
    }

    public LibbyManager(Plugin plugin) {
        bukkitLibraryManager = new BukkitLibraryManager(plugin);
        // Download correct version of CommandAPI
        bukkitLibraryManager.addMavenCentral();
        Library commandAPILibrary = Library.builder()
                .groupId("dev{}jorel")
                .artifactId(getArtifactId())
                .version(version)
                .checksum(getChecksum())
                .relocate("dev{}jorel{}commandapi", "dev.jorel.commandapi") // Should be changed by shading
                .build();
        System.out.println(commandAPILibrary);
        try {
            bukkitLibraryManager.loadLibrary(commandAPILibrary);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[RoadsideShops] Can't load library " + commandAPILibrary.toString() + '!');
            e.printStackTrace();

        }
    }

}
