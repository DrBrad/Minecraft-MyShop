package unet.Shop.Shop;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.*;

import java.text.DecimalFormat;
import java.util.Arrays;

import static unet.Shop.Currency.CurrencyHandler.*;

public class ItemParser {

    private int type;
    private double buy, sell;
    private String link;
    private PotionType potionType;
    private ItemStack item, tradeable;

    public ItemParser(Material material, int type, int amount){
        this.type = type;

        item = new ItemStack(material, amount);
        tradeable = new ItemStack(material);
    }

    public void setPrices(double buy, double sell){
        this.buy = buy;
        this.sell = sell;
    }

    public void setLink(String name, String link){
        this.link = link;

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§6§l"+name);
        itemMeta.setLore(Arrays.asList("§7Click to view this category"));
        item.setItemMeta(itemMeta);
    }

    public void setPotion(String potion, boolean extended, boolean upgraded){
        if(potion.equalsIgnoreCase("HASTE")){
            PotionMeta pmeta = (PotionMeta) item.getItemMeta();
            pmeta.clearCustomEffects();
            pmeta.setDisplayName("§r"+((item.getType() == Material.SPLASH_POTION) ? "Splash " : "")+"Potion of Haste");
            pmeta.setColor(Color.fromRGB(224, 138, 76));
            pmeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (extended) ? 9600 : 1800, (upgraded) ? 1 : 0), true);
            item.setItemMeta(pmeta);

            pmeta = (PotionMeta) tradeable.getItemMeta();
            pmeta.clearCustomEffects();
            pmeta.setDisplayName("§r"+((tradeable.getType() == Material.SPLASH_POTION) ? "Splash " : "")+"Potion of Haste");
            pmeta.setColor(Color.fromRGB(224, 138, 76));
            pmeta.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, (extended) ? 9600 : 1800, (upgraded) ? 1 : 0), true);
            tradeable.setItemMeta(pmeta);

        }else{
            potionType = PotionType.valueOf(potion);

            if(potionType != null){
                PotionMeta pmeta = (PotionMeta) item.getItemMeta();
                pmeta.clearCustomEffects();
                pmeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
                item.setItemMeta(pmeta);

                pmeta = (PotionMeta) tradeable.getItemMeta();
                pmeta.clearCustomEffects();
                pmeta.setBasePotionData(new PotionData(potionType, extended, upgraded));
                tradeable.setItemMeta(pmeta);
            }
        }
    }

    public ItemStack getItem(Player player){
        if(type == 1){
            DecimalFormat df2 = new DecimalFormat("#,##0.00");
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setLore(Arrays.asList("§7Money: §6$"+df2.format(getPlayersMoney(player.getUniqueId())),
                    "§9◀ §a(Shift LClick)§7 to Buy "+item.getMaxStackSize()+": §a$"+df2.format(buy*item.getMaxStackSize()),
                    "§9◀ §a(LClick)§7 to Buy 1: §a$"+df2.format(buy),
                    "§9▶ §c(Shift RClick)§7 to Sell 1: §c$"+df2.format(sell),
                    "§9▶ §c(RClick)§7 to Sell "+item.getMaxStackSize()+": §c$"+df2.format(sell*item.getMaxStackSize())));
            item.setItemMeta(itemMeta);
        }

        return item;
    }

    public ItemStack getTradeable(){
        return tradeable;
    }

    public int getType(){
        return type;
    }

    public String getLink(){
        return link;
    }

    public double getBuy(){
        return buy;
    }

    public double getSell(){
        return sell;
    }
}
