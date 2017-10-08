package com.medievallords.mechanics.targeters;


import com.medievallords.mechanics.data.MechanicData;
import org.bukkit.Location;

public class LocationTarget extends Target {

    public LocationTarget(String params) {
        super(params);
    }

    public Location getLocation(MechanicData data) {
        return data.getTrigger().getLocation();
    }
}
