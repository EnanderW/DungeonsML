package com.medievallords.triggers;

import com.medievallords.mechanics.Mechanic;
import com.medievallords.utils.DungeonLineConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Trigger {

    public static List<Trigger> triggers = new ArrayList<>();

    private String name;
    private File file;
    private ConfigurationSection cs;
    private DungeonLineConfig dlc;

    protected List<Mechanic> mechanics = new ArrayList<>();
    private Location location;

    public Trigger(String name, Location location, DungeonLineConfig dlc, File file, ConfigurationSection cs) {
        this.dlc = dlc;
        this.name = name;
        this.location = location;
        this.file = file;
        this.cs = cs;
    }

    public static List<Trigger> getTriggers(Class clazz) {
        List<Trigger> returns = new ArrayList<>();
        for (int i = 0; i < triggers.size(); i++) {
            Trigger trigger = triggers.get(i);
            if (trigger.getClass().getName().equalsIgnoreCase(clazz.getName())) {
                returns.add(trigger);
            }
        }

        return returns;
    }

    public static Trigger getTrigger(String name, String type, Location location, DungeonLineConfig dlc, File file, ConfigurationSection cs) {
        switch (type.toLowerCase()) {
            case "distance":
                return new DistanceTrigger(name, location, dlc, file, cs);
            case "interact":
                return new InteractTrigger(name, location, dlc, file, cs);
            case "mobdeath":
                return new MobTrigger(name, location, dlc, file, cs);
        }

        return null;
    }
}
