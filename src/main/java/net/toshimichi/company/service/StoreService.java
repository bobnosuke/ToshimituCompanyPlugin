package net.toshimichi.company.service;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class StoreService implements Service {

    private final Path path;
    private final Map<Integer, StoreItem> items = new HashMap<>();

    public void load() {
        items.clear();

        YamlConfiguration yaml = new YamlConfiguration();
        if (Files.exists(path)) {
            try {
                yaml.loadFromString(Files.readString(path));
            } catch (IOException | InvalidConfigurationException e) {
                throw new RuntimeException("Could not load store.yml", e);
            }
        }

        for (String key : yaml.getKeys(false)) {
            int id = Integer.parseInt(key);
            StoreItem item = (StoreItem) yaml.get(key);
            items.put(id, item);
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<Integer, StoreItem> entry : items.entrySet()) {
            yaml.set(entry.getKey().toString(), entry.getValue());
        }

        try {
            Files.writeString(path, yaml.saveToString());
        } catch (IOException e) {
            throw new RuntimeException("Could not save store.yml", e);
        }
    }

    public Map<Integer, StoreItem> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public void addItem(int id, ItemStack itemStack, int level, int price) {
        items.put(id, new StoreItem(itemStack, level, price));
        save();
    }

    public void removeItem(int id) {
        items.remove(id);
        save();
    }

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(StoreItem.class);
        load();
    }

    @Override
    public void onDisable() {
        ConfigurationSerialization.unregisterClass(StoreItem.class);
        save();
    }

}
