package unet.Shop.Auction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Date;

import static unet.Shop.Config.*;
import static unet.Shop.Currency.CurrencyHandler.*;

public class ItemParser {

    private ItemStack item;
    private double price;
    private long time;

    public ItemParser(ItemStack item, double price, long time){
        this.item = item;
        this.price = price;
        this.time = time;
    }

    public ItemStack getItem(Player player){
        long now = new Date().getTime();
        Date difference = new Date((time+getMaxAuctionTime())-now);

        DecimalFormat df2 = new DecimalFormat("#,##0.00");
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Arrays.asList("§7Auction ends in: §a"+timeConvert((int) difference.getTime()),
                "§7Money: §6$"+df2.format(getPlayersMoney(player.getUniqueId())),
                "§9◀ §a(Click)§7 to Buy: §a$"+df2.format(price)));
        item.setItemMeta(itemMeta);

        return item;
    }

    public String timeConvert(int gtime){
        int duration = gtime/1000;
        int hour = duration/3600;
        int mins = (duration/60)-(hour*60);
        int secs = duration-(hour*3600)-(mins*60);

        String time = "";

        if(hour > 0){
            time += hour+":";
        }

        if(mins > -1){
            time += ("0"+mins).toString().substring(("0"+mins).toString().length()-2)+':';
        }

        return time+("0"+secs).toString().substring(("0"+secs).toString().length()-2);
    }
}
