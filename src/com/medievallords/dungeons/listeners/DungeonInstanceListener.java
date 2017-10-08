package com.medievallords.dungeons.listeners;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.Dungeon;
import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.dungeons.instance.DungeonInstance;
import com.medievallords.player.DPlayer;
import com.medievallords.utils.Cooldowns;
import com.medievallords.utils.MessageManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Created by WE on 2017-09-28.
 *
 */

public class DungeonInstanceListener implements Listener {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.PHYSICAL || action == Action.LEFT_CLICK_AIR || action == Action.RIGHT_CLICK_AIR) {
            return;
        }

        Block block = event.getClickedBlock();

        Player player = event.getPlayer();

        DPlayer dPlayer = dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();

        Dungeon dungeon = instance.getDungeon();

        if (dungeon.getLocations().containsKey("ready")) {
            Location ready = instance.getDungeon().getLocations().get("ready");
            ready.setWorld(instance.getWorld());
            if (block.getLocation().equals(ready)) {
                instance.readyUp(dPlayer);
                return;
            }
        }

        if (dungeon.getLocations().containsKey("exit")) {
            Location ready = instance.getDungeon().getLocations().get("exit");
            ready.setWorld(instance.getWorld());
            if (block.getLocation().equals(ready)) {
                if (Cooldowns.getCooldown(player.getUniqueId(), "combat") > 0) {
                    MessageManager.sendMessage(player, "&cYou are in combat");
                    return;
                }

                instance.onLeave(dPlayer);
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        Cooldowns.tryCooldown(player.getUniqueId(), "combat", 15000);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        DPlayer dPlayer = dungeonHandler.getDPlayer(player.getUniqueId());
        if (dPlayer == null) {
            return;
        }

        DungeonInstance instance = dPlayer.getInstance();
        Location spawn = instance.getDungeon().getLocations().get("spawn");
        spawn.setWorld(instance.getWorld());
        player.teleport(spawn);
    }
}
