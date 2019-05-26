package dev.fumieval.spigot.maneuver;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Material;
import java.util.HashSet;

public class App extends JavaPlugin {
    public App() {
        saveDefaultConfig();
    }
    @Override
    public void onEnable() {
        HashSet<Material> maneuver_items = new HashSet<Material>();

        for (String s : getConfig().getStringList("items")){
            Material m = Material.matchMaterial(s);
            if (m == null) {
                this.getLogger().warning("Unknown material: " + s);
            } else {
                maneuver_items.add(m);
            }
        }

        MyListener listener = new MyListener(this, maneuver_items);
        getServer().getPluginManager().registerEvents(listener, this);
    }
}