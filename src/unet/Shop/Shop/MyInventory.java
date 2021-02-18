package unet.Shop.Shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.HashMap;

import static unet.Shop.Currency.CurrencyHandler.*;
import static unet.Shop.Shop.MyShop.*;

public class MyInventory {

    private HashMap<Integer, ItemParser> items;
    private String name, link;
    private int size;

    public MyInventory(String name, String link, int size, HashMap<Integer, ItemParser> items){
        this.name = name;
        this.link = link;
        this.items = items;
        this.size = size;
    }

    public String getName(){
        return name;
    }

    public String getLink(){
        return link;
    }

    public Inventory getInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, size, "§l§8"+name);

        for(Integer slot : items.keySet()){
            inventory.setItem(slot, items.get(slot).getItem(player));
        }

        return inventory;
    }

    public void onShiftLeftClick(int slot, Player player){
        if(items.containsKey(slot)){
            ItemParser item = items.get(slot);

            //CATEGORY
            if(item.getType() == 0){
                openInventory(item.getLink(), player);

            }else{
                ItemStack tmp = item.getTradeable();
                double money = getPlayersMoney(player.getUniqueId());
                double cost = item.getBuy()*tmp.getMaxStackSize();

                if(money >= cost){
                    tmp.setAmount(tmp.getMaxStackSize());

                    if(player.getInventory().addItem(tmp).isEmpty()){
                        setPlayersMoney(player.getUniqueId(), money-cost);

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        player.sendMessage("§7You have purchased §a"+tmp.getMaxStackSize()+" "+tmp.getType().name()+"§7 your new balance is §a$"+df2.format(money-cost)+"§7.");

                    }else{
                        player.sendMessage("§cYour inventory is full, please remove some items before you buy.");
                    }
                }else{
                    player.sendMessage("§cYou don't have enough money to purchase!");
                }
            }
        }
    }

    public void onLeftClick(int slot, Player player){
        if(items.containsKey(slot)){
            ItemParser item = items.get(slot);

            //CATEGORY
            if(item.getType() == 0){
                openInventory(item.getLink(), player);

            }else{
                double money = getPlayersMoney(player.getUniqueId());
                double cost = item.getBuy();

                if(money >= cost){
                    ItemStack tmp = item.getTradeable();
                    tmp.setAmount(1);

                    if(player.getInventory().addItem(tmp).isEmpty()){
                        setPlayersMoney(player.getUniqueId(), money-cost);

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        player.sendMessage("§7You have purchased §a1 "+tmp.getType().name()+"§7 your new balance is §a$"+df2.format(money-cost)+"§7.");

                    }else{
                        player.sendMessage("§cYour inventory is full, please remove some items before you buy.");
                    }
                }else{
                    player.sendMessage("§cYou don't have enough money to purchase!");
                }
            }
        }
    }

    public void onShiftRightClick(int slot, Player player){
        if(items.containsKey(slot)){
            ItemParser item = items.get(slot);

            //CATEGORY
            if(item.getType() == 0){
                openInventory(item.getLink(), player);

            }else{
                ItemStack tmp = item.getTradeable();
                tmp.setAmount(1);


                if(player.getInventory().containsAtLeast(tmp, 1)){
                    player.getInventory().removeItem(tmp);

                    double money = getPlayersMoney(player.getUniqueId());

                    setPlayersMoney(player.getUniqueId(), money+item.getSell());
                    DecimalFormat df2 = new DecimalFormat("#,##0.00");
                    player.sendMessage("§7You have sold §a1 "+tmp.getType().name()+"§7 your new balance is §a$"+df2.format(money+item.getSell())+"§7.");

                }else{
                    player.sendMessage("§cYou don't have enough of this item to sell!");
                }
            }
        }
    }

    public void onRightClick(int slot, Player player){
        if(items.containsKey(slot)){
            ItemParser item = items.get(slot);

            //CATEGORY
            if(item.getType() == 0){
                openInventory(item.getLink(), player);

            }else{
                ItemStack tmp = item.getTradeable();
                tmp.setAmount(tmp.getMaxStackSize());

                if(player.getInventory().containsAtLeast(tmp, tmp.getMaxStackSize())){
                    player.getInventory().removeItem(tmp);

                    double money = getPlayersMoney(player.getUniqueId());

                    setPlayersMoney(player.getUniqueId(), money+(item.getSell()*tmp.getMaxStackSize()));
                    DecimalFormat df2 = new DecimalFormat("#,##0.00");
                    player.sendMessage("§7You have sold §a1 "+tmp.getType().name()+"§7 your new balance is §a$"+df2.format(money+(item.getSell()*tmp.getMaxStackSize()))+"§7.");

                }else{
                    player.sendMessage("§cYou don't have enough of this item to sell!");
                }
            }
        }
    }
}
