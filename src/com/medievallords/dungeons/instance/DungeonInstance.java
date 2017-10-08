package com.medievallords.dungeons.instance;

import com.medievallords.Dungeons;
import com.medievallords.dungeons.Dungeon;
import com.medievallords.dungeons.DungeonHandler;
import com.medievallords.player.DPlayer;
import com.medievallords.triggers.Trigger;
import com.medievallords.utils.MessageManager;
import com.medievallords.utils.WorldLoader;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.spawning.spawners.MythicSpawner;
import io.lumine.xikage.mythicmobs.spawning.spawners.SpawnerManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
    private SpawnerManager spawnerManager = MythicMobs.inst().getSpawnerManager();

    private int id;

    private List<Trigger> triggers = new ArrayList<>();

    public List<DPlayer> players = new ArrayList<>();

    private List<MythicSpawner> spawners = new ArrayList<>();

    private World world;
    private Dungeon dungeon;
    private DungeonStage stage;

    private HashMap<String, Integer> maxSpawns = new HashMap<>();

    public DungeonInstance(int id, World world, Dungeon dungeon, List<Player> players, List<MythicSpawner> spawnersToClone, List<Trigger> triggersToClone) {
        this.id = id;
        this.world = world;
        this.dungeon = dungeon;
        for (Player player : players) {
            DPlayer dPlayer = new DPlayer(player, this);
            this.players.add(dPlayer);
        }

        for (MythicSpawner spawner : spawnersToClone) {
            try {
                MythicSpawner newSpawned = spawner.clone();
                newSpawned.setName("instanceSpawner_" + spawner.getName() + "_" + id);
                double x = newSpawned.getBlockX(), y = newSpawned.getBlockY(), z = newSpawned.getBlockZ();
                newSpawned.setLocation(BukkitAdapter.adapt(new Location(world, x, y, z)));
                spawners.add(newSpawned);
                spawnerManager.listSpawners.add(newSpawned);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        for (Trigger trigger : triggersToClone) {
            String type = trigger.getCs().getString("Type");

            Bukkit.broadcastMessage(type);

            Location copyLoc = trigger.getLocation().clone();
            copyLoc.setWorld(world);
            Trigger cloned = Trigger.getTrigger("dungeonInstanceTrigger_" + trigger.getName() + "_" + id, type, copyLoc, trigger.getDlc(), trigger.getFile(), trigger.getCs());
            cloned.getMechanics().addAll(trigger.getMechanics());

            if (cloned == null) {
                Bukkit.broadcastMessage("lol., null");
            }

            this.triggers.add(cloned);
            Trigger.triggers.add(cloned);
        }
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
            spawnerManager.listSpawners.removeAll(spawners);
            Trigger.triggers.removeAll(triggers);
            dungeonHandler.getInstances().remove(this);
            for (int i = 0; i < world.getPlayers().size(); i++) {
                Player player = world.getPlayers().get(i);
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
            }

            Bukkit.unloadWorld(world, false);
            WorldLoader.deleteWorld(world.getWorldFolder());
            Bukkit.getWorlds().remove(world);
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
            sendAll("&aYou need &d&l" + size + "&a players to start the dungeon. &c" + ready + " &7&l/&c " + size);
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

}
