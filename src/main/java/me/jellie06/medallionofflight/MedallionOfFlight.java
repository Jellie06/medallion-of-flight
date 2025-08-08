package me.jellie06.medallionofflight;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MedallionOfFlight extends JavaPlugin implements Listener {

    private ItemStack medallion;
    private NamespacedKey key;

    @Override
    public void onEnable() {
        getLogger().info("MedallionOfFlight enabled!");
        key = new NamespacedKey(this, "medallion_of_flight");
        createMedallion();
        registerRecipe();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void createMedallion() {
        medallion = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = medallion.getItemMeta();

        if (meta != null) {
            // Modern Adventure API for name
            meta.displayName(Component.text("Medallion of Flight")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false));

            // Modern lore
            meta.lore(List.of(
                    Component.text("A mystical medallion that grants")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("the power of flight.")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));

            // Make it glow without showing enchantments
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            // Add persistent tag
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");

            medallion.setItemMeta(meta);
        }
    }

    private void registerRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(this, "medallion_of_flight"),
                medallion
        );
        recipe.shape("FGF", "GEG", "FGF");
    recipe.setIngredient('F', Material.DIAMOND_BLOCK);
        recipe.setIngredient('G', Material.NETHER_STAR);
        recipe.setIngredient('E', Material.ELYTRA);

        Bukkit.addRecipe(recipe);
    }

    private boolean hasMedallion(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && "true".equals(meta.getPersistentDataContainer()
                        .get(key, PersistentDataType.STRING))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateFlight(Player player) {
        if (hasMedallion(player)) {
            player.setAllowFlight(true);
        } else {
            switch (player.getGameMode()) {
                case CREATIVE, SPECTATOR -> {} // Keep flight
                default -> {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        updateFlight(event.getPlayer());
    }

    @EventHandler
    public void onInventoryChange(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            Bukkit.getScheduler().runTaskLater(this, () -> updateFlight(player), 1L);
        }
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            Player player = (Player) event.getEntity();
            Bukkit.getScheduler().runTaskLater(this, () -> updateFlight(player), 1L);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> updateFlight(event.getPlayer()), 1L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // No cleanup needed
    }
}