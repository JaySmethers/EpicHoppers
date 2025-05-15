package com.songoda.epichoppers.utils;

import com.songoda.core.SongodaPlugin;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.utils.TextUtils;
import com.songoda.third_party.com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class Methods {
    public static boolean isSimilarMaterial(ItemStack is1, ItemStack is2) {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ||
                is1.getDurability() == Short.MAX_VALUE || is2.getDurability() == Short.MAX_VALUE) {
            // Durability of Short.MAX_VALUE is used in recipes if the durability should be ignored
            return is1.getType() == is2.getType();
        } else {
            return is1.getType() == is2.getType() && (is1.getDurability() == -1 || is2.getDurability() == -1 || is1.getDurability() == is2.getDurability());
        }
    }

    public static boolean canMove(Inventory inventory, ItemStack item) {
        if (inventory.firstEmpty() != -1) {
            return true;
        }

        final ItemMeta itemMeta = item.getItemMeta();
        for (ItemStack stack : inventory) {
            final ItemMeta stackMeta;
            if (isSimilarMaterial(stack, item) && (stack.getAmount() + item.getAmount()) < stack.getMaxStackSize()
                    && ((itemMeta == null) == ((stackMeta = stack.getItemMeta()) == null))
                    && (itemMeta == null || Bukkit.getItemFactory().equals(itemMeta, stackMeta))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canMoveReserved(Inventory inventory, ItemStack item) {
        if (inventory.firstEmpty() != inventory.getSize() - 1) {
            return true;
        }

        final ItemMeta itemMeta = item.getItemMeta();
        final ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < 4; i++) {
            final ItemStack stack = contents[i];
            final ItemMeta stackMeta;
            if (isSimilarMaterial(stack, item) && (stack.getAmount() + item.getAmount()) < stack.getMaxStackSize()
                    && ((itemMeta == null) == ((stackMeta = stack.getItemMeta()) == null))
                    && (itemMeta == null || Bukkit.getItemFactory().equals(itemMeta, stackMeta))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canMoveReserved(ItemStack[] contents, ItemStack item) {
        final ItemMeta itemMeta = item.getItemMeta();
        for (int i = 0; i < contents.length - 2; i++) {
            final ItemStack stack = contents[i];
            if (stack == null || stack.getAmount() == 0) {
                return true;
            }
            final ItemMeta stackMeta;
            if (isSimilarMaterial(stack, item) && (stack.getAmount() + item.getAmount()) < stack.getMaxStackSize()
                    && ((itemMeta == null) == ((stackMeta = stack.getItemMeta()) == null))
                    && (itemMeta == null || Bukkit.getItemFactory().equals(itemMeta, stackMeta))) {
                return true;
            }
        }
        return false;
    }

    public static String formatName(int level) {
        String name = getPlugin().getLocale()
                .getMessage("general.nametag.nameformat")
                .processPlaceholder("level", level)
                .toText();


        return TextUtils.formatText(name);
    }

    public static void doParticles(Entity entity, Location location) {
        location.setX(location.getX() + .5);
        location.setY(location.getY() + .5);
        location.setZ(location.getZ() + .5);
        String configParticleType = getPlugin().getConfig().getString("Main.Upgrade Particle Type");
        if (configParticleType != null && !configParticleType.trim().isEmpty()) {
            Optional<XParticle> xParticleOpt = XParticle.of(configParticleType);
            if (xParticleOpt.isPresent()) {
                Particle bukkitParticle = xParticleOpt.get().get();
                entity.getWorld().spawnParticle(bukkitParticle, location, 200, .5, .5, .5, 0);
            } else {
                // Fallback particle
                getPlugin().getLogger().warning("Invalid Upgrade particle type in config: " + configParticleType+ ". Using fallback particle.");
                entity.getWorld().spawnParticle(XParticle.FLAME.get(), location, 200, .5, .5, .5, 0);
            }
        }
    }

    /**
     * @deprecated The class needs refactoring to not even need the plugin.
     * This is just a temporary workaround to get a Minecraft 1.20-beta build ready
     */
    @Deprecated
    private static SongodaPlugin getPlugin() {
        return (SongodaPlugin) Bukkit.getPluginManager().getPlugin("EpicHoppers");
    }
}
