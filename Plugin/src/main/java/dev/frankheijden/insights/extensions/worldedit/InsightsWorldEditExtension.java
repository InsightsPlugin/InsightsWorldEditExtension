package dev.frankheijden.insights.extensions.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

public class InsightsWorldEditExtension extends JavaPlugin {

    private Settings settings;
    private WorldEditPlugin worldEditPlugin;
    private Class<?> worldEditExtentClass;

    @Override
    public void onEnable() {
        super.onEnable();

        reloadConfig();
        worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
        boolean fawe = Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");

        // Hook into WorldEdit
        worldEditPlugin.getWorldEdit().getEventBus().register(this);
        getLogger().info("Successfully hooked into " + (fawe ? "FastAsyncWorldEdit" : "WorldEdit") + "!");

        // Figure out the class
        String worldEditPackage;
        try {
            Class.forName("com.sk89q.worldedit.math.BlockVector3");
            worldEditPackage = "worldedit7";
        } catch (ClassNotFoundException ex) {
            worldEditPackage = "worldedit6";
        }

        try {
            worldEditExtentClass = Class.forName("dev.frankheijden.insights.extensions.worldedit." + worldEditPackage + ".WorldEditExtent");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Unhook from WorldEdit
        worldEditPlugin.getWorldEdit().getEventBus().unregister(this);
    }

    public Settings getSettings() {
        return settings;
    }

    @Override
    public void reloadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try (InputStream in = getResource("config.yml")) {
            settings = Settings.load(new File(getDataFolder(), "config.yml"), in).exceptionally(getLogger());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) return false;
        reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "InsightsWorldEditExtension has been reloaded.");
        return false;
    }

    @Subscribe(priority = EventHandler.Priority.VERY_LATE)
    public void handleEditSession(EditSessionEvent event) {
        Actor actor = event.getActor();
        if (actor != null && actor.isPlayer()) {
            Player player = Bukkit.getPlayer(actor.getName());
            event.setExtent(createExtent(Bukkit.getWorld(event.getWorld().getName()), player, event.getExtent(), event.getStage()));
        }
    }

    private Extent createExtent(World world, Player player, Extent extent, EditSession.Stage stage) {
        InsightsExtentDelegate delegate = new InsightsExtentDelegate(this, world, player);
        try {
            Constructor<?> constructor = worldEditExtentClass.getDeclaredConstructors()[0];
            return (Extent) constructor.newInstance(extent, player, stage, delegate);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
