package com.medievallords.triggers;

import com.medievallords.mechanics.Mechanic;
import com.medievallords.mechanics.data.MechanicData;
import com.medievallords.utils.DungeonLineConfig;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.io.File;

@Getter
public class MobTrigger extends Trigger {

    private String mobType;
    private int state;
    private int deaths = 0;

    public MobTrigger(String name, Location location, DungeonLineConfig dlc, File file, ConfigurationSection cs) {
        super(name, location, dlc, file, cs);

        this.mobType = dlc.getString("mob", "ZOMBIE");
        this.state = dlc.getInt("state", 1);
    }

    public void trigger(Entity entity, Location location) {
        if (state == 1 && deaths >= 1) {
            return;
        }

        MechanicData data = new MechanicData(entity, location);
        for (Mechanic mechanic : mechanics) {
            mechanic.runMechanic(data);
        }

        deaths++;
    }
}
