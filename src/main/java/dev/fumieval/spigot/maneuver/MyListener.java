package dev.fumieval.spigot.maneuver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Sound;
import java.util.HashSet;
import java.util.HashMap;

public class MyListener implements Listener
{
    private HashSet<Material> maneuverItems;
    private Plugin plugin;
    private HashMap<String, Vector> playerDirection;
    public MyListener(Plugin plugin, HashSet<Material> items)
    {
        this.plugin = plugin;
        maneuverItems = items;
        playerDirection = new HashMap<String, Vector>();
    }
    @EventHandler
    public void onSwapHands(PlayerSwapHandItemsEvent event)
    {
        ItemStack main = event.getMainHandItem();
        ItemStack off = event.getOffHandItem();
        Player player = event.getPlayer();
        Vector vel0 = playerDirection.get(player.getName());
        if (vel0 == null) return;
        if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) return;
        if (off != null && maneuverItems.contains(off.getType()) || main != null && maneuverItems.contains(main.getType())){
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 1);
            Vector acc = new Vector();
            if (player.isOnGround()){
                acc = vel0.setY(0.33);
            } else {
                acc = acc.setY(-player.getVelocity().getY() + 0.4);
                player.setFallDistance(player.getFallDistance() - 3);
            }
            player.setVelocity(player.getVelocity().add(acc));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 4));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1));
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        playerDirection.put(event.getPlayer().getName(), event.getTo().toVector().subtract(event.getFrom().toVector()).normalize());
    }
}