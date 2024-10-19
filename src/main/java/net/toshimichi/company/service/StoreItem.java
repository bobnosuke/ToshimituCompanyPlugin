package net.toshimichi.company.service;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Data
public class StoreItem implements ConfigurationSerializable {

    private final ItemStack itemStack;
    private final int level;
    private final double price;

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("item", itemStack);
        map.put("level", level);
        map.put("price", price);
        return map;
    }

    public static StoreItem deserialize(Map<String, Object> map) {
        return new StoreItem((ItemStack) map.get("item"), (int) map.get("level"), (double) map.get("price"));
    }
}
