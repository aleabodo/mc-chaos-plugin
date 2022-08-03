package mcchaosplugin.mcchaosplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class McChaosPlugin extends JavaPlugin implements Listener, EventExecutor {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Hello overworld");
        Bukkit.getPluginManager().registerEvent(PlayerRespawnEvent.class, this, EventPriority.NORMAL, this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onRespawn(PlayerRespawnEvent event) {
        event.getPlayer().getInventory().addItem(new ItemStack(Material.POTATO, 1));
    }

    //wofür ist das?
    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {

    }
}
