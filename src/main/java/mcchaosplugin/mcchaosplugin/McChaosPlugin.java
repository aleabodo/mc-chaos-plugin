package mcchaosplugin.mcchaosplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class McChaosPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private static boolean rainingTNT = true;
    private static Vector shipPosition = new Vector(0,60,0);
    public static World world = null;
    public static McChaosPlugin plugin;

    private static int tntSpawnPeriod = 0;
    private static int tntRelativeSpawnHeight = 0;
    private static int tntSpawnPlayerDistance = 0;
    public static double accelerationConstant = 0;

    public static void intiConfigVars() {
        //changing tntSpawnPeriod has no effect if the job is already scheduled
        tntSpawnPeriod = plugin.getConfig().getInt("TNT_period");
        tntRelativeSpawnHeight = plugin.getConfig().getInt("TNT_relative_spawn_height");
        tntSpawnPlayerDistance = plugin.getConfig().getInt("TNT_spawn_player_distance");
        accelerationConstant = plugin.getConfig().getDouble("acceleration_constant");
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Hello overworld");
        world = getServer().getWorld("world");
        plugin = this;
        //Bukkit.getPluginManager().registerEvent(PlayerRespawnEvent.class, this, EventPriority.NORMAL, this, this);
        Bukkit.getPluginManager().registerEvents(this, this);

        getConfig().options().copyDefaults(true);
        saveConfig();
        intiConfigVars();

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Vector spawnLocation = new Vector();
            Vector spawnVelocity = new Vector();
            if(McChaosPlugin.rainingTNT == true) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    double px = p.getLocation().getX();
                    double py = p.getLocation().getY();
                    double pz = p.getLocation().getZ();
                    Vector diff = new Vector(); //vector from player to ship
                    diff.setX(shipPosition.getX() - px);
                    diff.setY(0);
                    diff.setZ(shipPosition.getZ() - pz);
                    diff.normalize();
                    diff.multiply(tntSpawnPlayerDistance);
                    spawnLocation.setX(px + diff.getX());
                    spawnLocation.setZ(pz + diff.getZ());
                    spawnLocation.setY(Math.max(getHighestBlock(spawnLocation.getBlockX(), spawnLocation.getBlockZ()), p.getLocation().getY()) + tntRelativeSpawnHeight);

                    double heightDiff = spawnLocation.getY() - p.getLocation().getY();
                    //x = 1/2 g t^2 => heightDiff = 0.5 * g * timeToImpact^2
                    //=> heightDiff = 0.5 * g * timeToImpact^2
                    //=> sqrt(2* heightDiff/g) = timeToImpact
                    //We assume g = 18 m/s^2 (https://www.youtube.com/watch?v=aE9_YAXao3I)
                    double timeToImpact = Math.sqrt(2.0 * heightDiff / accelerationConstant) * 20; //in ticks
                    double vx = -diff.getX() / timeToImpact;
                    double vz = -diff.getZ() / timeToImpact;

                    getServer().getScheduler().runTask(this, () -> {
                        TNTPrimed tnt = (TNTPrimed) world.spawnEntity(spawnLocation.toLocation(world), EntityType.PRIMED_TNT);
                        tnt.setVelocity(new Vector(vx, 0, vz));
                        tnt.setFuseTicks((int)timeToImpact + 20); //lies on ground for another 1 second
                    });
                }
            }
        }, 1 * 20, tntSpawnPeriod * 20);
    }

    public int getHighestBlock(int xCoord, int zCoord) {
        for (int i = 320; i > -64; i--) {
            if (world.getBlockAt(xCoord, i, zCoord).isEmpty() == false) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().getInventory().addItem(new ItemStack(Material.POTATO, 1));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        //if (!args[0].equals("reload")) return false;
        Player player = (Player) sender;
        if (true || player.hasPermission("promotion.reload")) {
            this.reloadConfig();
            this.saveConfig();
            intiConfigVars();
            player.sendMessage(ChatColor.GREEN + "[AutoPromotion] Config reloaded!");
            System.out.println("[AutoPromotion] Config reloaded!");
        }
        return true;
    }

    //wofür ist das?
    //@Override
    //public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
    //
    //}
}