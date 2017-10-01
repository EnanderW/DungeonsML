package com.medievallords.dungeons.instance;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.Dungeon;
import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.player.DPlayer;
import com.medievallords.spawners.Spawner;
import com.medievallords.utils.MessageManager;
import com.medievallords.utils.WorldLoader;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WE on 2017-09-27.
 *
 */

@Getter
public class DungeonInstance {

    private DungeonHandler dungeonHandler = Dungeons.getInstance().getDungeonHandler();

    private int id;

    private int taskId;

    public List<DPlayer> players = new ArrayList<>();

    private World world;
    private Dungeon dungeon;
    private DungeonStage stage;

    private HashMap<String, Integer> maxSpawns = new HashMap<>();

    public DungeonInstance(int id, World world, Dungeon dungeon, List<Player> players) {
        this.id = id;
        this.world = world;
        this.dungeon = dungeon;
        for (Player player : players) {
            DPlayer dPlayer = new DPlayer(player, this);
            this.players.add(dPlayer);
        }

        startTask();
    }

    public void prepare() {
        Location lobby = dungeon.getLocations().get("lobby");
        lobby.setWorld(world);
        for (int i = 0; i < players.size(); i++) {
            DPlayer player = players.get(i);
            player.getPlayer().teleport(lobby);
        }
    }

    private void start() {
        Location spawn = dungeon.getLocations().get("spawn");
        spawn.setWorld(world);
        for (int i = 0; i < players.size(); i++) {
            DPlayer player = players.get(i);
            player.getPlayer().teleport(spawn);
        }
    }

    public void onLeave(DPlayer dPlayer) {
        if (dPlayer.isInCombat()) {
            MessageManager.sendMessage(dPlayer.getPlayer(), "&cYou cannot leave while you are in combat");
            return;
        }

        players.remove(dPlayer);
        if (dungeon.getLocations().containsKey("complete")) {
            Location complete = dungeon.getLocations().get("complete");
            complete.setWorld(world);
            dPlayer.getPlayer().teleport(complete);
        } else {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + dPlayer.getPlayer().getName());
        }

        if (players.isEmpty()) {
            cancelTask();
            dungeonHandler.getInstances().remove(this);
            for (int i = 0; i < world.getPlayers().size(); i++) {
                Player player = world.getPlayers().get(i);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
            }

            Bukkit.unloadWorld(world, false);
            WorldLoader.deleteWorld(world.getWorldFolder());
        }
    }

    public void readyUp(DPlayer player) {
        player.setReady(true);
        int ready = 0;
        for (int i = 0; i < players.size(); i++) {
            DPlayer dPlayer = players.get(i);
            if (dPlayer.isReady()) {
                ready++;
            }
        }

        if (ready >= players.size()) {
            start();
        } else {
            int size = players.size();
            sendAll("&aYou need &d" + size + "&a to start the dungeon. &e" + ready + " &7/&e" + size);
        }
    }

    public void sendAll(String message) {
        for (int i = 0; i < players.size(); i++) {
            DPlayer dPlayer = players.get(i);
            MessageManager.sendMessage(dPlayer.getPlayer(), message);
        }
    }

    public DPlayer getPlayer(Player player) {
        for (int i = 0; i < players.size(); i++) {
            DPlayer dPlayer = players.get(i);
            if (dPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return dPlayer;
            }
        }

        return null;
    }

    public void startTask() {
        this.taskId = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(Dungeons.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Spawner spawner : dungeon.getSpawners()) {
                    if (spawner.isPlayerNearby(world)) {
                        if (!maxSpawns.containsKey(spawner.getName())) {
                            maxSpawns.put(spawner.getName(), 1);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawner.spawn(world);
                                }
                            }.runTask(Dungeons.getInstance());
                        } else {
                            int amount = maxSpawns.get(spawner.getName());
                            if (amount >= spawner.getMaxSpawns()) {
                                return;
                            }

                            amount++;
                            maxSpawns.put(spawner.getName(), amount);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    spawner.spawn(world);
                                }
                            }.runTask(Dungeons.getInstance());
                        }
                    }
                }
            }
        },0, 20);
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
