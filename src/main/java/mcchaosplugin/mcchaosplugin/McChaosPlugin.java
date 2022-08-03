package mcchaosplugin.mcchaosplugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class McChaosPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Hello overworld");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
