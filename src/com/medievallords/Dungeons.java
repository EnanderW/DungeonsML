package com.medievallords;

import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.dungeons.commands.DungeonCreateCommand;
import com.medievallords.dungeons.commands.DungeonSetCommand;
import com.medievallords.dungeons.commands.TestJoinCommand;
import com.medievallords.dungeons.listeners.DungeonInstanceListener;
import com.medievallords.spawners.SpawnerListener;
import com.medievallords.spawners.commands.SpawnerCreateCommand;
import com.medievallords.spawners.commands.SpawnerNearbyCommand;
import com.medievallords.spawners.commands.SpawnerSetMobCommand;
import com.medievallords.utils.CommandFramework;
import com.medievallords.utils.WorldLoader;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
@Setter
public class Dungeons extends JavaPlugin {

    public static final String[] EXCLUDED_FILES = {"uid.dat", "data"};

    private static Dungeons instance;

    private File dungeonsFile;
    private FileConfiguration dungeonsFileConfiguration;


    private CommandFramework commandFramework;

    private DungeonHandler dungeonHandler;

    @Override
    public void onEnable() {
        instance = this;
        registerConfigurations();

        this.commandFramework = new CommandFramework(this);
        this.dungeonHandler = new DungeonHandler();
        dungeonHandler.load();

        registerCommands();
        registerListeners();

        removeAllInstanceWorlds();
    }

    @Override
    public void onDisable() {
        dungeonHandler.cancelAll();
    }

    public void registerConfigurations() {
        saveResource("dungeons.yml", false);

        this.dungeonsFile = new File(getDataFolder(), "dungeons.yml");
        this.dungeonsFileConfiguration = YamlConfiguration.loadConfiguration(dungeonsFile);
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new SpawnerListener(), this);
        pm.registerEvents(new DungeonInstanceListener(), this);
    }

    public void registerCommands() {
        new TestJoinCommand();
        new DungeonCreateCommand();
        new DungeonSetCommand();

        new SpawnerCreateCommand();
        new SpawnerNearbyCommand();
        new SpawnerSetMobCommand();
    }

    public void saveConfiguration(FileConfiguration fileConfiguration, File file, String s) {
        try {
            fileConfiguration.save(file);
            fileConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeAllInstanceWorlds() {
        for (int i = Bukkit.getWorlds().size() - 1; i >= 0; i--) {
            World world = Bukkit.getWorlds().get(i);
            if (world.getName().startsWith("dungeonInstance_")) {
                Bukkit.unloadWorld(world, false);
                WorldLoader.deleteWorld(world.getWorldFolder());
                Bukkit.getWorlds().remove(world);
            }
        }
    }

    public static Dungeons getInstance() {
        return instance;
    }
}
