package com.medievallords.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Random;

/**
 * Created by WE on 2017-09-27.
 *
 */

public class LocationUtil {

    private static Random random = new Random();

    public static Location findSafeSpot(Location location, double radius) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();


        double randomX = (x - radius) + ((x + radius) - (x - radius)) * (random.nextDouble());
        double randomY = (y - radius) + ((y + radius) - (y - radius)) * (random.nextDouble());
        double randomZ = (z - radius) + ((z + radius) - (z - radius)) * (random.nextDouble());

        Location randomLocation = new Location(location.getWorld(), randomX, randomY, randomZ);
        if (randomLocation.getBlock() == null && randomLocation.clone().add(0, 1, 0) == null) {
            return randomLocation;
        }

        for (double yL = y + radius; yL > y - radius; yL--) {
            randomLocation.subtract(0, 1, 0);

            if (randomLocation.clone().add(0,1,0).getBlock() != null) {
                continue;
            } else {
                if (randomLocation.getBlock() != null) {
                    continue;
                } else {
                    if (randomLocation.clone().subtract(0,1,0).getBlock() != null) {
                        return randomLocation;
                    } else {
                        return findSafeSpot(location, radius);
                    }
                }
            }
        }

        return location;
    }

    public static String serializeLocation(Location l) {
        String s = "";
        s = s + "@w;" + l.getWorld().getName();
        s = s + ":@x;" + l.getX();
        s = s + ":@y;" + l.getY();
        s = s + ":@z;" + l.getZ();
        s = s + ":@p;" + l.getPitch();
        s = s + ":@ya;" + l.getYaw();
        return s;
    }

    public static Location deserializeLocation(String s) {
        Location l = new Location(Bukkit.getWorlds().get(0), 0.0D, 0.0D, 0.0D);
        String[] att = s.split(":");

        for (String attribute : att) {
            String[] split = attribute.split(";");
            if (split[0].equalsIgnoreCase("@w")) {
                l.setWorld(Bukkit.getWorld(split[1]));
            }

            if (split[0].equalsIgnoreCase("@x")) {
                l.setX(Double.parseDouble(split[1]));
            }

            if (split[0].equalsIgnoreCase("@y")) {
                l.setY(Double.parseDouble(split[1]));
            }

            if (split[0].equalsIgnoreCase("@z")) {
                l.setZ(Double.parseDouble(split[1]));
            }

            if (split[0].equalsIgnoreCase("@p")) {
                l.setPitch(Float.parseFloat(split[1]));
            }

            if (split[0].equalsIgnoreCase("@ya")) {
                l.setYaw(Float.parseFloat(split[1]));
            }
        }

        return l;
    }
}
