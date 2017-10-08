package com.medievallords.triggers;

import com.medievallords.mechanics.Mechanic;
import com.medievallords.mechanics.data.MechanicData;
import com.medievallords.utils.DungeonLineConfig;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class InteractTrigger extends Trigger {

    // 1 = ONCE
    // 2 ONCE PER PERSON
    // 3 UNLIMITED

    private int state;
    private int interacts = 0;
    private List<UUID> playersInteracted = new ArrayList<>();

    public InteractTrigger(String name, Location location, DungeonLineConfig dlc, File file, ConfigurationSection cs) {
        super(name, location, dlc, file, cs);

        this.state = dlc.getInt("state", 1);
    }

    public void trigger(Entity entity) {
        if (state == 1 && interacts >= 1) {
            return;
        } else if (state == 2 && playersInteracted.contains(entity.getUniqueId())) {
            return;
        }

        MechanicData data = new MechanicData(entity, getLocation());
        for (Mechanic mechanic : mechanics) {
            mechanic.runMechanic(data);
        }

        playersInteracted.add(entity.getUniqueId());
        interacts++;
    }
}
