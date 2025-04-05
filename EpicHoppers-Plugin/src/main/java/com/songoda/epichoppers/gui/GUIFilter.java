package com.songoda.epichoppers.gui;

import com.songoda.core.SongodaPlugin;
import com.songoda.core.gui.CustomizableGui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.core.utils.TextUtils;
import com.songoda.epichoppers.EpicHoppersApi;
import com.songoda.epichoppers.hopper.Hopper;
import com.songoda.epichoppers.hopper.ItemType;
import com.songoda.epichoppers.player.SyncType;
import com.songoda.epichoppers.settings.Settings;
import com.songoda.epichoppers.utils.DataHelper;
import com.songoda.epichoppers.utils.Methods;
import com.songoda.epichoppers.hopper.Filter;
import com.songoda.epichoppers.hopper.HopperImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIFilter extends CustomizableGui {
    private static final List<GUIFilter> OPEN_INVENTORIES = new ArrayList<>();

    private final SongodaPlugin plugin;
    private final HopperImpl hopper;

    public GUIFilter(SongodaPlugin plugin, Hopper hopper, Player player) {
        super(plugin, "filter");
        this.plugin = plugin;
        this.hopper = (HopperImpl) hopper;

        setRows(6);
        setTitle(TextUtils.formatText(Methods.formatName(hopper.getLevel().getLevel()) + " &8-&f Filter"));
        setDefaultItem(null);
        setAcceptsItems(true);

        setOnOpen((event) -> GUIFilter.OPEN_INVENTORIES.add(this));

        setOnClose((event) -> {
            GUIFilter.OPEN_INVENTORIES.remove(this);
            hopper.setActivePlayer(null);
            compile();
        });

        Filter filter = hopper.getFilter();

        ItemStack glass1 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_1.getMaterial());
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial());

        mirrorFill("mirrorfill_1", 0, 6, true, false, glass2);
        mirrorFill("mirrorfill_2", 0, 7, true, false, glass2);
        mirrorFill("mirrorfill_3", 0, 8, true, false, glass2);
        mirrorFill("mirrorfill_4", 1, 6, true, false, glass2);
        mirrorFill("mirrorfill_5", 1, 8, true, false, glass2);
        mirrorFill("mirrorfill_6", 2, 6, true, false, glass2);
        mirrorFill("mirrorfill_7", 2, 7, true, false, glass1);
        mirrorFill("mirrorfill_8", 2, 8, true, false, glass2);

        ItemStack it = XMaterial.WHITE_STAINED_GLASS_PANE.parseItem();
        ItemMeta itm = it.getItemMeta();
        itm.setDisplayName(plugin.getLocale().getMessage("interface.filter.whitelist").toText());
        it.setItemMeta(itm);

        setButton("back", 8, GuiUtils.createButtonItem(XMaterial.ARROW.parseItem(),
                        plugin.getLocale().getMessage("general.nametag.back").getMessage()),
                (event) -> {
                    if (this.hopper.prepareForOpeningOverviewGui(event.player)) {
                        this.guiManager.showGUI(event.player, new GUIOverview(plugin, hopper, event.player));
                    }
                    compile();
                });

        int[] whiteSlots = {0, 1, 45, 46};
        for (int nu : whiteSlots) {
            setItem(nu, it);
        }

        int[] awhite = {9, 10, 18, 19, 27, 28, 36, 37};
        int num = 0;
        for (ItemStack m : filter.getWhiteList()) {
            if (num >= filter.getWhiteList().size()) {
                break;
            }
            setItem(awhite[num], new ItemStack(m));
            num++;
        }

        it = XMaterial.BLACK_STAINED_GLASS_PANE.parseItem();
        itm = it.getItemMeta();
        itm.setDisplayName(plugin.getLocale().getMessage("interface.filter.blacklist").toText());
        it.setItemMeta(itm);

        int[] blackSlots = {2, 3, 47, 48};
        for (int nu : blackSlots) {
            setItem("blacklist", nu, it);
        }

        int[] ablack = {11, 12, 20, 21, 29, 30, 38, 39};
        num = 0;
        for (ItemStack m : filter.getBlackList()) {
            if (num >= filter.getBlackList().size()) {
                break;
            }

            setItem(ablack[num], new ItemStack(m));
            num++;
        }

        it = XMaterial.BARRIER.parseItem();
        itm = it.getItemMeta();
        itm.setDisplayName(plugin.getLocale().getMessage("interface.filter.void").toText());
        it.setItemMeta(itm);

        int[] avoid = {4, 5, 49, 50};
        for (int nu : avoid) {
            setItem("void", nu, it);
        }

        int[] voidSlots = {13, 14, 22, 23, 31, 32, 40, 41};
        num = 0;
        for (ItemStack m : filter.getVoidList()) {
            if (num >= filter.getVoidList().size()) {
                break;
            }
            setItem(voidSlots[num], new ItemStack(m));
            num++;
        }

        ItemStack itemInfo = XMaterial.PAPER.parseItem();
        ItemMeta itemMetaInfo = itemInfo.getItemMeta();
        itemMetaInfo.setDisplayName(plugin.getLocale().getMessage("interface.filter.infotitle").toText());
        ArrayList<String> loreInfo = new ArrayList<>();
        String[] parts = plugin.getLocale().getMessage("interface.filter.infolore").toText().split("\\|");
        for (String line : parts) {
            loreInfo.add(TextUtils.formatText(line));
        }
        itemMetaInfo.setLore(loreInfo);
        itemInfo.setItemMeta(itemMetaInfo);

        setItem("info", 16, itemInfo);


        ItemStack hook = XMaterial.TRIPWIRE_HOOK.parseItem();
        ItemMeta hookMeta = hook.getItemMeta();
        hookMeta.setDisplayName(plugin.getLocale().getMessage("interface.hopper.rejectsync").toText());
        ArrayList<String> loreHook = new ArrayList<>();
        parts = plugin.getLocale().getMessage("interface.hopper.synclore")
                .processPlaceholder("amount", filter.getEndPoint() != null ? 1 : 0)
                .toText().split("\\|");
        for (String line : parts) {
            loreHook.add(TextUtils.formatText(line));
        }
        hookMeta.setLore(loreHook);
        hook.setItemMeta(hookMeta);
        setButton("reject", 43, hook,
                (event) -> {
                    if (event.clickType == ClickType.RIGHT) {
                        plugin.getLocale().getMessage("event.hopper.desync").sendPrefixedMessage(player);
                        hopper.getFilter().setEndPoint(null);
                    } else {
                        EpicHoppersApi.getApi().getPlayerDataManager().getPlayerData(player).setSyncType(SyncType.FILTERED);
                        plugin.getLocale().getMessage("event.hopper.syncnext").sendPrefixedMessage(player);
                        hopper.timeout(player);
                    }
                    player.closeInventory();
                });

        setUnlockedRange(9, 14);
        setUnlockedRange(18, 23);
        setUnlockedRange(27, 32);
        setUnlockedRange(36, 41);
    }

    private void compile() {
        ItemStack[] items = this.inventory.getContents();

        Filter filter = this.hopper.getFilter();

        List<ItemStack> owhite = new ArrayList<>();
        List<ItemStack> oblack = new ArrayList<>();
        List<ItemStack> ovoid = new ArrayList<>();

        int[] awhite = {9, 10, 18, 19, 27, 28, 36, 37};
        int[] ablack = {11, 12, 20, 21, 29, 30, 38, 39};
        int[] avoid = {13, 14, 22, 23, 31, 32, 40, 41};

        for (int i = 0; i < items.length; i++) {
            for (int aa : awhite) {
                if (aa != i) {
                    continue;
                }
                if (items[i] != null && items[i].getType() != Material.AIR) {
                    ItemStack item = items[i];
                    if (item.getAmount() != 1) {
                        item.setAmount(item.getAmount() - 1);
                        Bukkit.getPlayer(this.hopper.getLastPlayerOpened()).getInventory().addItem(item);
                        item.setAmount(1);
                    }
                    owhite.add(item);
                }
            }
            for (int aa : ablack) {
                if (aa != i) {
                    continue;
                }
                if (items[i] != null && items[i].getType() != Material.AIR) {
                    ItemStack item = items[i];
                    if (item.getAmount() != 1) {
                        item.setAmount(item.getAmount() - 1);
                        Bukkit.getPlayer(this.hopper.getLastPlayerOpened()).getInventory().addItem(item);
                        item.setAmount(1);
                    }
                    oblack.add(item);
                }
            }
            for (int aa : avoid) {
                if (aa != i) {
                    continue;
                }
                if (items[i] != null && items[i].getType() != Material.AIR) {
                    ItemStack item = items[i];
                    if (item.getAmount() != 1) {
                        item.setAmount(item.getAmount() - 1);
                        Bukkit.getPlayer(this.hopper.getLastPlayerOpened()).getInventory().addItem(item);
                        item.setAmount(1);
                    }
                    ovoid.add(item);
                }
            }
        }
        filter.setWhiteList(owhite);
        filter.setBlackList(oblack);
        filter.setVoidList(ovoid);
        DataHelper.updateItems(this.hopper, ItemType.WHITELIST, owhite);
        DataHelper.updateItems(this.hopper, ItemType.BLACKLIST, oblack);
        DataHelper.updateItems(this.hopper, ItemType.VOID, ovoid);
    }

    public static void compileOpenGuiFilter(HopperImpl hopper) {
        for (GUIFilter guiFilter : OPEN_INVENTORIES) {
            if (guiFilter.hopper == hopper) {
                guiFilter.compile();
            }
        }
    }
}
