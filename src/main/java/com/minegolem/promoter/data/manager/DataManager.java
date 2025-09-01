package com.minegolem.promoter.data.manager;

import com.minegolem.promoter.Promoter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.framework.qual.RequiresQualifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DataManager {

    private final Promoter plugin;
    private final File file;
    private final boolean copyDefaultsFromJar;

    private final YamlConfiguration config = new YamlConfiguration();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DataManager(Promoter plugin, File file, boolean copyDefaultsFromJar) {
        this.plugin = plugin;
        this.file = file;
        this.copyDefaultsFromJar = copyDefaultsFromJar;
    }

    public static Builder of(Promoter plugin, String relativePath) {
        return new Builder(plugin, relativePath);
    }

    public static final class Builder {
        private final Promoter plugin;
        private final String relativePath;
        private boolean copyDefaultsFromJar = false;

        private Builder(Promoter plugin, String relativePath) {
            this.plugin = plugin;
            this.relativePath = relativePath;
        }

        public Builder copyDefaultsFromJar(boolean copyDefaultsFromJar) {
            this.copyDefaultsFromJar = copyDefaultsFromJar;
            return this;
        }

        public DataManager build() {
            File dataFolder = plugin.getDataFolder();
            File target = new File(dataFolder, relativePath);

            return new DataManager(plugin, target, copyDefaultsFromJar);
        }
    }

    public DataManager loadOrCreate() {
        lock.writeLock().lock();
        try {
            ensureFileExists();
            loadInternal();
            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void save() throws IOException {
        lock.writeLock().lock();
        try {
            config.save(file);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveSilently() {
        try {
            save();
        } catch (IOException ex) {
            plugin.getLogger().warning("Impossibile salvare YML '" + file.getName() + "': " + ex.getMessage());
        }
    }

    public CompletableFuture<Void> saveAsync() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                save();
                future.complete(null);
            } catch (IOException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public boolean contains(String path) {
        lock.readLock().lock();
        try {
            return config.contains(path);
        } finally {
            lock.readLock().unlock();
        }
    }

    public Object get(String path) {
        lock.readLock().lock();
        try {
            return config.get(path);
        } finally {
            lock.readLock().unlock();
        }
    }

    public <T> T get(String path, T def) {
        lock.readLock().lock();
        try {
            Object val = config.get(path, def);
            @SuppressWarnings("unchecked")
            T cast = (T) val;
            return cast;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getString(String path, String def) {
        lock.readLock().lock();
        try {
            return config.getString(path, def);
        } finally {
            lock.readLock().unlock();
        }
    }


    public int getInt(String path, int def) {
        lock.readLock().lock();
        try {
            return config.getInt(path, def);
        } finally {
            lock.readLock().unlock();
        }
    }


    public boolean getBoolean(String path, boolean def) {
        lock.readLock().lock();
        try {
            return config.getBoolean(path, def);
        } finally {
            lock.readLock().unlock();
        }
    }


    public double getDouble(String path, double def) {
        lock.readLock().lock();
        try {
            return config.getDouble(path, def);
        } finally {
            lock.readLock().unlock();
        }
    }


    public long getLong(String path, long def) {
        lock.readLock().lock();
        try {
            return config.getLong(path, def);
        } finally {
            lock.readLock().unlock();
        }
    }


    public List<String> getStringList(String path) {
        lock.readLock().lock();
        try {
            return config.getStringList(path);
        } finally {
            lock.readLock().unlock();
        }
    }


    public ConfigurationSection getSection(String path) {
        lock.readLock().lock();
        try {
            return config.getConfigurationSection(path);
        } finally {
            lock.readLock().unlock();
        }
    }


    public Set<String> getKeys(boolean deep) {
        lock.readLock().lock();
        try {
            return config.getKeys(deep);
        } finally {
            lock.readLock().unlock();
        }
    }


    public Set<String> getKeys(String sectionPath, boolean deep) {
        lock.readLock().lock();
        try {
            ConfigurationSection sec = sectionPath == null || sectionPath.isEmpty() ? config : config.getConfigurationSection(sectionPath);
            return sec != null ? sec.getKeys(deep) : java.util.Collections.emptySet();
        } finally {
            lock.readLock().unlock();
        }
    }

    public DataManager set(String path, Object value) {
        lock.writeLock().lock();
        try {
            config.set(path, value);
            return this;
        } finally {
            lock.writeLock().unlock();
        }
    }


    /** Incrementa un double (o imposta def se il path non esiste). */
    public double increment(String path, double delta, double defIfMissing) {
        lock.writeLock().lock();
        try {
            double curr = config.contains(path) ? config.getDouble(path) : defIfMissing;
            double next = curr + delta;
            config.set(path, next);
            return next;
        } finally {
            lock.writeLock().unlock();
        }
    }


    /** Decrementa un double (non va sotto min). Ritorna il nuovo valore. */
    public double decrementFloor(String path, double delta, double min) {
        lock.writeLock().lock();
        try {
            double curr = config.getDouble(path, 0.0);
            double next = Math.max(min, curr - delta);
            config.set(path, next);
            return next;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void ensureFileExists() {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                plugin.getLogger().warning("Impossibile creare la cartella: " + parent.getAbsolutePath());
            }
        }


        if (!file.exists()) {
            if (copyDefaultsFromJar && copyFromJarIfPresent()) {
                return; // copiato dal jar, fine
            }
            try {
                if (file.createNewFile()) {
                    try (Writer w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                        w.write("# Generated by " + plugin.getName() + "\n");
                    }
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Impossibile creare il file YML: " + file.getAbsolutePath());
            }
        }


    }

    private boolean copyFromJarIfPresent() {
// Path della risorsa nel jar: relativo alla root delle risorse, uguale al percorso relativo del file
        String resourcePath = relativizeToDataFolder(file, plugin.getDataFolder());
        if (resourcePath == null) return false;


        try (InputStream in = plugin.getResource(resourcePath)) {
            if (in == null) return false; // non esiste nel jar
            try (OutputStream out = new FileOutputStream(file)) {
                in.transferTo(out);
                return true;
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Copia dal jar fallita per '" + resourcePath + "': " + e.getMessage());
            return false;
        }
    }


    private static String relativizeToDataFolder(File target, File dataFolder) {
        try {
            String base = dataFolder.getCanonicalPath();
            String child = target.getCanonicalPath();
            if (child.startsWith(base)) {
                String rel = child.substring(base.length());
                if (rel.startsWith(File.separator)) rel = rel.substring(1);
                return rel.replace(File.separatorChar, '/'); // stile risorsa
            }
        } catch (IOException ignored) {}
        return null;
    }


    private void loadInternal() {
        try {
            config.load(file);
        } catch (FileNotFoundException e) {
// non dovrebbe accadere perché ensureFileExists() lo crea, ma gestiamo comunque
            ensureFileExists();
            try {
                config.load(file);
            } catch (IOException | InvalidConfigurationException ex) {
                plugin.getLogger().severe("Caricamento YML fallito: " + ex.getMessage());
            }
        } catch (IOException | InvalidConfigurationException e) {
            plugin.getLogger().severe("Caricamento YML fallito: " + e.getMessage());
        }
    }

}
