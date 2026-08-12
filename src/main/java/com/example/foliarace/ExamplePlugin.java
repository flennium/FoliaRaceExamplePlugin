package com.example.foliarace;

import com.foliarace.core.observation.OperationCategory;
import com.foliarace.plugin.FoliaRaceObservations;
import com.foliarace.plugin.ObservationReceipt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class ExamplePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        World world = Bukkit.getWorld(Objects.requireNonNullElse(
                getConfig().getString("world"), "world"));
        if (world == null) {
            getLogger().warning("Configured world is not loaded; example check was skipped");
            return;
        }

        Location location = new Location(
                world,
                getConfig().getDouble("x", 0.0),
                getConfig().getDouble("y", 80.0),
                getConfig().getDouble("z", 0.0));

        Bukkit.getRegionScheduler().run(this, location, task -> runRegionChecks(location));
        Bukkit.getGlobalRegionScheduler().run(this, task -> runGlobalChecks());

        if (getConfig().getBoolean("run-unsafe-examples", false)) {
            Bukkit.getAsyncScheduler().runDelayed(this, task -> runAsyncExamples(location), 2, TimeUnit.SECONDS);
        }
    }

    private void runRegionChecks(Location location) {
        ObservationReceipt receipt = FoliaRaceObservations.observeLocationAccess(
                this, location, OperationCategory.BLOCK_ACCESS);

        String blockType = location.getBlock().getType().name();
        getLogger().info("Observed block=" + blockType
                + " accepted=" + receipt.accepted()
                + " ownershipCheckAvailable=" + receipt.ownershipCheckAvailable());

        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 8, 8, 8);
        entities.stream().findFirst().ifPresent(this::runEntityChecks);
    }

    private void runEntityChecks(Entity entity) {
        ObservationReceipt entityReceipt = FoliaRaceObservations.observeEntityAccess(
                this, entity, OperationCategory.ENTITY_ACCESS);
        getLogger().info("Observed entity=" + entity.getType()
                + " accepted=" + entityReceipt.accepted());

        if (entity instanceof org.bukkit.entity.Player player) {
            Inventory inventory = player.getInventory();
            ObservationReceipt inventoryReceipt = FoliaRaceObservations.observeInventoryAccess(this, inventory);
            String item = player.getInventory().getItemInMainHand().getType().name();
            getLogger().info("Observed player inventory item=" + item
                    + " accepted=" + inventoryReceipt.accepted());
        }
    }

    private void runGlobalChecks() {
        ObservationReceipt globalReceipt = FoliaRaceObservations.observeGlobalAccess(
                this, OperationCategory.SERVER_GLOBAL_ACCESS);
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        Inventory globalInventory = Bukkit.createInventory(null, 9);
        ObservationReceipt inventoryReceipt = FoliaRaceObservations.observeInventoryAccess(this, globalInventory);
        int inventorySize = globalInventory.getSize();

        getLogger().info("Observed onlinePlayers=" + onlinePlayers
                + " inventorySize=" + inventorySize
                + " globalAccepted=" + globalReceipt.accepted()
                + " inventoryAccepted=" + inventoryReceipt.accepted());
    }

    private void runAsyncExamples(Location location) {
        ObservationReceipt receipt = FoliaRaceObservations.observeLocationAccess(
                this, location, OperationCategory.WORLD_GLOBAL_ACCESS);

        // This block intentionally runs outside a region context when enabled in config.
        String worldName = Objects.requireNonNull(location.getWorld()).getName();
        getLogger().warning("Unsafe async example observed world=" + worldName
                + " accepted=" + receipt.accepted());
    }
}
