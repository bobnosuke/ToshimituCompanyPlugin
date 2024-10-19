package net.toshimichi.company.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

public interface Widget {

    ItemStack getItemStack();

    void onClick(ClickType type, InventoryAction action);
}
