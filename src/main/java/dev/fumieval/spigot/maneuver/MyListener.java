package dev.fumieval.spigot.maneuver;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import org.bukkit.Particle;
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
        if (vel0 == null || vel0.length() <= 0) return;
        if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) return;
        if (off != null && maneuverItems.contains(off.getType()) || main != null && maneuverItems.contains(main.getType())){
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, 1, 1);
            Vector acc = new Vector();
            if (player.isOnGround()){
                acc = vel0.normalize().setY(0.33);
            } else {
                acc = acc.setY(-player.getVelocity().getY() + 0.4);
                player.setFallDistance(player.getFallDistance() - 3);
            }
            player.setVelocity(player.getVelocity().add(acc));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 5));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1));
        }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        playerDirection.put(event.getPlayer().getName(), event.getTo().toVector().subtract(event.getFrom().toVector()));
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event){
        try {
            PotionEffect eff = LivingEntity.class.cast(event.getEntity()).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            if (eff != null && eff.getAmplifier() == 5)
            {
                LivingEntity damager = LivingEntity.class.cast(event.getDamager());
                Location loc = damager.getLocation().add(0, 1, 0);
                damager.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1, 1);
                damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5));
                damager.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 120);
                event.setCancelled(true);
            }
        } catch (ClassCastException e) {
        }
    }
}