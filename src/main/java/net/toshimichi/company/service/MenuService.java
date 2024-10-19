package net.toshimichi.company.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.toshimichi.company.menu.Menu;
import net.toshimichi.company.menu.Widget;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class MenuService implements Service, Listener {

    private final Plugin plugin;

    private final List<OpenMenu> openMenus = new ArrayList<>();

    public OpenMenu getOpenMenu(UUID uniqueId) {
        return openMenus.stream()
                .filter(c -> c.getUniqueId().equals(uniqueId))
                .findAny()
                .orElse(null);
    }

    public void openMenu(Player player, Menu menu) {
        Widget[] widgets = menu.createWidgets();
        Inventory inventory = Bukkit.createInventory(null, widgets.length, menu.getTitle());
        for (int i = 0; i < widgets.length; i++) {
            Widget widget = widgets[i];
            if (widget == null) continue;
            inventory.setItem(i, widget.getItemStack());
        }

        player.openInventory(inventory);
        openMenus.add(new OpenMenu(player.getUniqueId(), menu, widgets, inventory));
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        OpenMenu openMenu = getOpenMenu(e.getWhoClicked().getUniqueId());
        if (openMenu == null) return;
        e.setCancelled(true);
        if (e.getInventory() != openMenu.getInventory()) return;

        int slot = e.getSlot();
        if (slot < 0) return;
        if (slot >= openMenu.getWidgets().length) return;

        Widget widget = openMenu.getWidgets()[slot];
        if (widget == null) return;

        widget.onClick(e.getClick(), e.getAction());

        // update inventory if the same menu is still open
        if (e.getInventory() == openMenu.getInventory()) {
            openMenu.getInventory().clear();
            Widget[] newWidgets = openMenu.getMenu().createWidgets();
            for (int i = 0; i < newWidgets.length; i++) {
                Widget newWidget = newWidgets[i];
                if (newWidget == null) continue;
                openMenu.getInventory().setItem(i, newWidget.getItemStack());
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent e) {
        if (getOpenMenu(e.getWhoClicked().getUniqueId()) == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        OpenMenu openMenu = getOpenMenu(e.getPlayer().getUniqueId());
        if (openMenu == null) return;
        openMenus.remove(openMenu);
    }

    @Data
    private static class OpenMenu {

        private final UUID uniqueId;
        private final Menu menu;
        private final Widget[] widgets;
        private final Inventory inventory;
    }
}
