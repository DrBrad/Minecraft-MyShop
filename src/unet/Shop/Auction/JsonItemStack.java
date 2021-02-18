package unet.Shop.Auction;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonItemStack {

    public static JSONObject toJSON(ItemStack item, int amount){
        JSONObject jitem = new JSONObject();
        jitem.put("type", item.getType().name());
        jitem.put("amount", amount);

        ItemMeta itemMeta = item.getItemMeta();

        //GET LORE
        if(itemMeta.hasLore()){
            jitem.put("lore", new JSONArray());

            for(String lore : itemMeta.getLore()){
                jitem.getJSONArray("lore").put(lore);
            }
        }

        //GET DISPLAY NAME
        if(itemMeta.hasDisplayName()){
            jitem.put("name", itemMeta.getDisplayName());
        }

        //GET ENCHANTMENTS
        if(itemMeta.hasEnchants()){
            Map<Enchantment, Integer> enchantments = item.getEnchantments();
            jitem.put("enchantments", new JSONObject());

            for(Enchantment enchantment : enchantments.keySet()){
                int level = (enchantments.get(enchantment) == null) ? 1 : enchantments.get(enchantment);
                jitem.getJSONObject("enchantments").put(enchantment.getKey().getKey(), level);
            }
        }

        //GET POTION DATA
        if(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION){
            PotionMeta pmeta = (PotionMeta) itemMeta;

            if(pmeta.hasCustomEffects()){
                jitem.put("custom-effects", new JSONArray());

                for(PotionEffect effect : pmeta.getCustomEffects()){
                    JSONObject jeffect = new JSONObject();
                    jeffect.put("type", effect.getType().getName());
                    jeffect.put("duration", effect.getDuration());
                    jeffect.put("amplifier", effect.getAmplifier());
                    jitem.getJSONArray("custom-effects").put(jeffect);
                }
            }

            if(pmeta.getBasePotionData() != null){
                JSONObject jeffect = new JSONObject();
                jeffect.put("type", pmeta.getBasePotionData().getType().name());
                jeffect.put("duration", pmeta.getBasePotionData().isExtended());
                jeffect.put("amplifier", pmeta.getBasePotionData().isUpgraded());
                jitem.put("potion-effects", jeffect);
            }

            if(pmeta.hasColor()){
                jitem.put("potion-color", pmeta.getColor().asRGB());
            }
        }

        if(item.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta emeta = (EnchantmentStorageMeta) itemMeta;
            Map<Enchantment, Integer> enchantments = emeta.getStoredEnchants();

            jitem.put("stored-enchantments", new JSONObject());

            for(Enchantment enchantment : enchantments.keySet()){
                int level = (enchantments.get(enchantment) == null) ? 1 : enchantments.get(enchantment);
                jitem.getJSONObject("stored-enchantments").put(enchantment.getKey().getKey(), level);
            }
        }

        //GET DAMAGE
        Damageable damageable = (Damageable) itemMeta;
        if(damageable.getDamage() != 0){
            jitem.put("damage", damageable.getDamage());
        }

        return jitem;
    }

    public static ItemStack fromJSON(JSONObject jitem){
        ItemStack item = new ItemStack(Material.valueOf(jitem.getString("type")), jitem.getInt("amount"));
        ItemMeta itemMeta = item.getItemMeta();

        //SET LORE
        if(jitem.has("lore")){
            List<String> lore = new ArrayList<>();

            JSONArray jlore = jitem.getJSONArray("lore");
            for(int i = 0; i < jlore.length(); i++){
                lore.add(jlore.getString(i));
            }

            itemMeta.setLore(lore);
        }

        //SET DISPLAY NAME
        if(jitem.has("name")){
            itemMeta.setDisplayName(jitem.getString("name"));
        }

        //SET DAMAGE
        if(jitem.has("damage")){
            ((Damageable) itemMeta).setDamage(jitem.getInt("damage"));
            item.setItemMeta(itemMeta);
        }

        //SET ENCHANTMENTS
        if(jitem.has("enchantments")){
            Iterator<String> keys = jitem.getJSONObject("enchantments").keys();
            while(keys.hasNext()){
                String key = keys.next();
                item.addEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(key)), jitem.getJSONObject("enchantments").getInt(key));
            }
        }

        //SET POTION DATA
        if(item.getType() == Material.POTION || item.getType() == Material.SPLASH_POTION || item.getType() == Material.LINGERING_POTION){
            PotionMeta pmeta = (PotionMeta) itemMeta;

            if(jitem.has("custom-effects")){
                pmeta.clearCustomEffects();
                JSONArray customEffects = jitem.getJSONArray("custom-effects");

                for(int i = 0; i < customEffects.length(); i++){
                    pmeta.addCustomEffect(new PotionEffect(PotionEffectType.getByName(customEffects.getJSONObject(i).getString("type")),
                            customEffects.getJSONObject(i).getInt("duration"),
                            customEffects.getJSONObject(i).getInt("amplifier")), true);
                }
            }

            if(jitem.has("potion-effects")){
                pmeta.setBasePotionData(new PotionData(PotionType.valueOf(jitem.getJSONObject("potion-effects").getString("type")),
                        jitem.getJSONObject("potion-effects").getBoolean("duration"),
                        jitem.getJSONObject("potion-effects").getBoolean("amplifier")));
            }

            if(jitem.has("potion-color")){
                pmeta.setColor(Color.fromRGB(jitem.getInt("potion-color")));
            }

            item.setItemMeta(pmeta);
        }

        if(jitem.has("stored-enchantments")){
            EnchantmentStorageMeta emeta = (EnchantmentStorageMeta) itemMeta;

            Iterator<String> keys = jitem.getJSONObject("stored-enchantments").keys();
            while(keys.hasNext()){
                String key = keys.next();
                emeta.addStoredEnchant(Enchantment.getByKey(NamespacedKey.minecraft(key)), jitem.getJSONObject("stored-enchantments").getInt(key), true);
            }

            item.setItemMeta(emeta);

        }

        return item;
    }
}
