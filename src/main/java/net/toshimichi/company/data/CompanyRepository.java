package net.toshimichi.company.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.toshimichi.company.utils.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class CompanyRepository {

    private final Map<UUID, Company> entities = new HashMap<>();
    private final Set<Company> saveQueue = new HashSet<>();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    private final Path saveDir;

    public Company findByUniqueId(UUID uniqueId) {
        return entities.get(uniqueId);
    }

    public List<Company> findAll() {
        return List.copyOf(entities.values());
    }

    public Company findByName(String name) {
        return entities.values()
                .stream()
                .filter(company -> company.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public Company findByMember(UUID uniqueId) {
        for (Company company : entities.values()) {
            List<UUID> members = company.getMembers()
                    .stream()
                    .map(Member::getUniqueId)
                    .toList();

            if (members.contains(uniqueId)) {
                return company;
            }
        }

        return null;
    }

    public void loadAll() throws IOException {
        entities.clear();

        if (!Files.isDirectory(saveDir)) return;
        try (Stream<Path> stream = Files.list(saveDir)) {
            List<Path> children = stream.toList();
            for (Path child : children) {
                JsonObject obj = gson.fromJson(Files.readString(child), JsonObject.class);

                // migration
                if (!obj.has("storePurchaseLogs")) {
                    obj.add("storePurchaseLogs", gson.toJsonTree(List.of()));
                }

                Company company = gson.fromJson(obj, Company.class);
                entities.put(company.getUniqueId(), company);
            }
        }
    }

    private void addIfNotExists(Company company) {
        entities.put(company.getUniqueId(), company);
    }

    public void save(Company company) throws IOException {
        addIfNotExists(company);
        if (!Files.isDirectory(saveDir)) {
            Files.createDirectories(saveDir);
        }

        Path path = saveDir.resolve(company.getUniqueId() + ".json");
        Files.writeString(path, gson.toJson(company));
    }

    public void saveLater(Company company) {
        addIfNotExists(company);
        saveQueue.add(company);
    }

    public void flushSaveQueue() throws IOException {
        for (Company company : saveQueue) {
            save(company);
        }

        saveQueue.clear();
    }

    public void saveAll() throws IOException {
        for (Company company : entities.values()) {
            save(company);
        }
    }

    public void delete(Company company) throws IOException {
        entities.remove(company.getUniqueId());
        saveQueue.remove(company);

        Path path = saveDir.resolve(company.getUniqueId() + ".json");
        Files.deleteIfExists(path);
    }
}
