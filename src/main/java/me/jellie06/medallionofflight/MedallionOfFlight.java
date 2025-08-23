package me.jellie06.medallionofflight;

import me.jellie06.medallionofflight.json.JsonItem;
import me.jellie06.medallionofflight.json.JsonOperations;
import me.jellie06.medallionofflight.json.JsonStruct;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

import java.io.File;
import java.util.List;

public class MedallionOfFlight extends JavaPlugin implements Listener {

    private ItemStack medallion;
    private NamespacedKey key;

    public static MedallionOfFlight plugin;

    public JsonStruct recipe;
    File Json_File;

    @Override
    public void onEnable() {
        plugin = this;


        Json_File = new File(getDataFolder(), "recipe.json");


        recipe = JsonOperations.jsonInit(Json_File);






        getLogger().info(ChatColor.BLUE+"Enabled MedallionOfFlight version " + this.getPluginMeta().getVersion() + ", initial plugin created by " + ChatColor.GREEN + "doopaderp" + ChatColor.BLUE+", updated to 1.21 by " + ChatColor.LIGHT_PURPLE + "Jellie06" + ChatColor.BLUE  + " and JSON support added by " + ChatColor.GOLD + "BlueNightFury46");
        key = new NamespacedKey(this, "medallion_of_flight");
        createMedallion();
        registerRecipe();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

        JsonOperations.jsonSave(recipe, Json_File);

    }

    private void createMedallion() {
        medallion = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = medallion.getItemMeta();

        if (meta != null) {
            meta.displayName(Component.text("Medallion of Flight")
                    .color(NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false));

            meta.lore(List.of(
                    Component.text("A mystical medallion that grants")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("the power of flight.")
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));

            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "true");

            medallion.setItemMeta(meta);
        }
    }

    //Abstracted away for readability
    private int arraysize(int n){
        return (n-1);
    }

    private void registerRecipe() {
        ShapedRecipe medallion_recipe = new ShapedRecipe(
                new NamespacedKey(this, "medallion_of_flight"),
                medallion
        );

        if(recipe.recipe_shape.size()>=arraysize(3)){
            medallion_recipe.shape(recipe.recipe_shape.get(arraysize(1)), recipe.recipe_shape.get(arraysize(2)), recipe.recipe_shape.get(arraysize(3)));

            for(JsonItem item : recipe.items){
                ItemStack recipe_item = new ItemStack(Material.getMaterial(item.item_name), item.item_count);
                medallion_recipe.setIngredient(item.key, recipe_item);
            }

            Bukkit.addRecipe(medallion_recipe);


        } else {


            this.getLogger().warning("The provided recipe shape failed to load! Loading the default config");

            medallion_recipe.shape("FGF", "GEG", "FGF");
            medallion_recipe.setIngredient('F', Material.DIAMOND_BLOCK);
            medallion_recipe.setIngredient('G', Material.NETHER_STAR);
            medallion_recipe.setIngredient('E', Material.ELYTRA);

            Bukkit.addRecipe(medallion_recipe);

        }
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
                case CREATIVE, SPECTATOR -> {} 
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
        
    }
}
