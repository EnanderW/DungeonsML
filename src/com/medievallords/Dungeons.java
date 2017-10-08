package com.medievallords;

import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.dungeons.DungeonQueuer;
import com.medievallords.dungeons.commands.DungeonCreateCommand;
import com.medievallords.dungeons.commands.DungeonEditCommand;
import com.medievallords.dungeons.commands.DungeonSetCommand;
import com.medievallords.dungeons.commands.TestJoinCommand;
import com.medievallords.dungeons.listeners.DungeonInstanceListener;
import com.medievallords.mechanics.Mechanic;
import com.medievallords.triggers.Trigger;
import com.medievallords.triggers.listeners.TriggerInteractListener;
import com.medievallords.utils.CommandFramework;
import com.medievallords.utils.DungeonLineConfig;
import com.medievallords.utils.LocationUtil;
import com.medievallords.utils.WorldLoader;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
    private File triggerFile;


    private CommandFramework commandFramework;

    private DungeonHandler dungeonHandler;
    private DungeonQueuer dungeonQueuer;

    @Override
    public void onEnable() {
        instance = this;
        registerConfigurations();

        this.commandFramework = new CommandFramework(this);
        this.dungeonHandler = new DungeonHandler();
        dungeonHandler.load();
        this.dungeonQueuer = new DungeonQueuer();
        loadTriggers();
        applyMechanics();

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
        //saveResource("triggers.yml", false);

        this.dungeonsFile = new File(getDataFolder(), "dungeons.yml");
        this.dungeonsFileConfiguration = YamlConfiguration.loadConfiguration(dungeonsFile);

        this.triggerFile = new File(getDataFolder(), "triggers");
        if (!triggerFile.exists()) {
            triggerFile.mkdirs();
        }
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new DungeonInstanceListener(), this);
        pm.registerEvents(new TestJoinCommand(), this);
        pm.registerEvents(new TriggerInteractListener(), this);
    }

    public void registerCommands() {
        new DungeonCreateCommand();
        new DungeonSetCommand();
        new DungeonEditCommand();
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

        for (File file : Bukkit.getWorldContainer().listFiles()) {
            if (file.getName().startsWith("dungeonInstance_")) {
                WorldLoader.deleteWorld(file);
            }
        }
    }

    private void loadTriggers() {
        File[] files = triggerFile.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (!file.getName().endsWith(".yml")) {
                return;
            }

            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            for (String key : fileConfiguration.getKeys(false)) {
                String locationString = fileConfiguration.getString(key + ".Location");
                String type = fileConfiguration.getString(key + ".Type");

                List<String> data = fileConfiguration.contains(key + ".Data") ? fileConfiguration.getStringList(key + ".Data") : new ArrayList<>();
                if (locationString != null && type != null) {
                    Location location = LocationUtil.deserializeLocation(locationString);
                    Trigger trigger = Trigger.getTrigger(key, type, location, new DungeonLineConfig(data), file, fileConfiguration.getConfigurationSection(key));
                    if (trigger != null) {
                        Trigger.triggers.add(trigger);
                    }
                } else {
                    Bukkit.getLogger().log(Level.WARNING, "[Trigger] Could not load trigger with name: " + key);
                }
            }
        }
    }

    private void applyMechanics() {
        for (Trigger trigger : Trigger.triggers) {
            ConfigurationSection cs = trigger.getCs().getConfigurationSection("Mechanics");
            if (cs == null) {
                continue;
            }

            for (String mechanicName : cs.getKeys(false)) {
                if (cs.getStringList(mechanicName) == null) {
                } else {
                    List<String> data = cs.getStringList(mechanicName);
                    Mechanic mechanic = Mechanic.getMechanic(mechanicName, data);
                    if (mechanic != null) {
                        trigger.getMechanics().add(mechanic);
                    }
                }
            }
        }
    }

    public static Dungeons getInstance() {
        return instance;
    }
}
