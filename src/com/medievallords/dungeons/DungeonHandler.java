package com.medievallords.dungeons;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.instance.DungeonInstance;
import com.medievallords.player.DPlayer;
import com.medievallords.utils.LocationUtil;
import com.medievallords.utils.WorldLoader;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
public class DungeonHandler {

    private Dungeons main = Dungeons.getInstance();

    private List<Dungeon> dungeons = new ArrayList<>();

    private List<DungeonInstance> instances = new ArrayList<>();

    public void load() {
        ConfigurationSection cs = main.getDungeonsFileConfiguration().getConfigurationSection("Dungeons");
        if (cs == null) {
            main.getDungeonsFileConfiguration().createSection("Dungeons");
            main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
            return;
        }

        dungeons.clear();

        for (String key : cs.getKeys(false)) {
            HashMap<String, Location> locations = new HashMap<>();

            World world = null;
            if (cs.getString(key + ".World") != null) {
                world = Bukkit.getWorld(cs.getString(key + ".World"));
            }

            for (String location : cs.getStringList(key + ".Locations")) {
                String[] split = location.split(",");
                locations.put(split[0], LocationUtil.deserializeLocation(split[1]));
            }

            Dungeon dungeon = new Dungeon(key);
            dungeon.setWorld(world);
            dungeon.setLocations(locations);

            dungeons.add(dungeon);

            List<MythicSpawner> spawnersToAdd = new ArrayList<>();
            List<MythicSpawner> publicSpawners = MythicMobs.inst().getSpawnerManager().listSpawners;
            for (MythicSpawner spawner : publicSpawners) {
                if (spawner.getWorldName().equalsIgnoreCase(world.getName())) {
                    spawnersToAdd.add(spawner);
                }
            }

            dungeon.setSpawners(spawnersToAdd);
        }
    }

    public void save() {
        ConfigurationSection cs = main.getDungeonsFileConfiguration().getConfigurationSection("Dungeons");
        if (cs == null) {
            cs = main.getDungeonsFileConfiguration().createSection("Dungeons");
        }

        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);

            String name = dungeon.getName();
            HashMap<String, Location> locations = dungeon.getLocations();
            World world = dungeon.getWorld();
            List<String> ser = new ArrayList<>();
            for (String location : locations.keySet()) {
                ser.add(location + "," + LocationUtil.serializeLocation(locations.get(location)));
            }

            cs.createSection(name);
            cs.set(name + ".Locations", ser);

            if (world != null) {
                cs.set(name + ".World", world.getName());
            }
        }

        main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
    }

    public void createDungeon(String name) {
        Dungeon dungeon = new Dungeon(name);
        dungeons.add(dungeon);
        main.getDungeonsFileConfiguration().set("Dungeons", null);
        main.saveConfiguration(main.getDungeonsFileConfiguration(), main.getDungeonsFile(), "dungeons.yml");
        save();
    }

    public Dungeon getDungeon(String name) {
        for (int i = 0; i < dungeons.size(); i++) {
            Dungeon dungeon = dungeons.get(i);
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }

        return null;
    }

    public DPlayer getDPlayer(UUID uuid) {
        for (int i = 0; i < instances.size(); i++) {
            DungeonInstance instance = instances.get(i);
            for (int l = 0; l < instance.getPlayers().size(); l++) {
                DPlayer dPlayer = instance.getPlayers().get(l);
                if (dPlayer.getPlayer().getUniqueId().equals(uuid)) {
                    return dPlayer;
                }
            }
        }

        return null;
    }

    public DungeonInstance getInstance(int id) {
        for (int i = 0; i < instances.size(); i++) {
            DungeonInstance instance = instances.get(i);
            if (instance.getId() == id) {
                return instance;
            }
        }

        return null;
    }

    public void cancelAll() {
        for (DungeonInstance instance : instances) {
            World world = instance.getWorld();
            for (int i = world.getPlayers().size() -  1; i >= 0; i--) {
                Player player = world.getPlayers().get(i);
                player.teleport(new Location(Bukkit.getWorld("world"), -729.5, 104, 317.5));
            }

            MythicMobs.inst().getSpawnerManager().listSpawners.removeAll(instance.getSpawners());
            Bukkit.unloadWorld(world, false);
            WorldLoader.deleteWorld(world.getWorldFolder());
            Bukkit.getWorlds().remove(world);
        }
    }
}
