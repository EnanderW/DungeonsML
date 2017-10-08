package com.medievallords.mechanics.targeters;

import com.medievallords.mechanics.data.MechanicData;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PIRTarget extends Target {

    private double radius;

    public PIRTarget(String params) {
        super(params);
        this.radius = dlc.getDouble("radius", 10);
    }

    public List<Player> getPlayers(MechanicData data) {
        Location centre = data.getLocation();
        World world = centre.getWorld();

        List<Player> players = new ArrayList<>();
        for (Entity entity : world.getNearbyEntities(centre, radius, radius, radius)) {
            if (entity instanceof Player) {
                players.add((Player) entity);
            }
        }

        return players;
    }
}
