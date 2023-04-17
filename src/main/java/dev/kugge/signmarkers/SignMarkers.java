package dev.kugge.signmarkers;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.gson.MarkerGson;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import dev.kugge.signmarkers.watcher.SignDestroyWatcher;import dev.kugge.signmarkers.watcher.SignWatcher;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class SignMarkers extends JavaPlugin {

    public static SignMarkers instance;
    public static Logger logger;
    public static Map<World, MarkerSet> markerSet = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        createFiles();
        for (World world : Bukkit.getWorlds()) {
            loadWorldMarkerSet(world);
            registerWorld(world);
        }
        Bukkit.getPluginManager().registerEvents(new SignWatcher(), this);
        Bukkit.getPluginManager().registerEvents(new SignDestroyWatcher(), this);
    }

    @Override
    public void onDisable() {
        for (World world : Bukkit.getWorlds()) saveWorldMarkerSet(world);
    }

    private void createFiles() {
        for (World world : Bukkit.getWorlds()) {
            String name = "marker-set-" + world.getName() + ".json";
            File file = new File(this.getDataFolder(), name);
            try {
                File folder = this.getDataFolder();
                if (!folder.exists()) folder.mkdirs();
                if (!file.exists()) file.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveWorldMarkerSet(World world) {
        String name = "marker-set-" + world.getName() + ".json";
        File file = new File(this.getDataFolder(), name);
        try (FileWriter writer = new FileWriter(file)) {
            MarkerGson.INSTANCE.toJson(markerSet.get(world), writer);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadWorldMarkerSet(World world) {
        String name = "marker-set-" + world.getName() + ".json";
        File file = new File(this.getDataFolder(), name);
        try (FileReader reader = new FileReader(file)) {
            MarkerSet set = MarkerGson.INSTANCE.fromJson(reader, MarkerSet.class);
            if (set != null) markerSet.put(world, set);
        } catch (FileNotFoundException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void registerWorld(World world) {
        BlueMapAPI.onEnable(api ->
            api.getWorld(world).ifPresent(blueWorld -> {
                for (BlueMapMap map : blueWorld.getMaps()) {
                    String label = "sign-markers-" + world.getName();
                    MarkerSet set = markerSet.get(world);
                    if (set == null) set = MarkerSet.builder().label(label).build();
                    map.getMarkerSets().put(label, set);
                    markerSet.put(world, set);
                }
            })
        );
    }
}
