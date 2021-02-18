package unet.Shop.Auction;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

import static unet.Shop.Config.*;
import static unet.Shop.Currency.CurrencyHandler.*;
import static unet.Shop.Main.plugin;
import static unet.Shop.Reclaims.MyReclaims.*;

public class MyAuction {

    private static JSONArray items = new JSONArray();
    private static HashMap<Inventory, MyInventory> openAuctions = new HashMap<>();

    public MyAuction(){
        try{
            File auctionFile = new File(plugin.getDataFolder()+File.separator+"auction.json");

            if(auctionFile.exists()){
                items = new JSONArray(new JSONTokener(new FileInputStream(auctionFile)));
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
            @Override
            public void run(){
                long now = new Date().getTime();

                if(items.length() > 0){
                    boolean removed = false;
                    for(int i = items.length()-1; i > -1; i--){
                        if((items.getJSONObject(i).getLong("time")+getMaxAuctionTime())-now <= 0){
                            removeItemForReclaim(i);
                            removed = true;
                        }
                    }

                    if(removed){
                        write();
                    }
                }

                if(openAuctions.size() > 0){
                    for(Inventory inventory : openAuctions.keySet()){
                        MyInventory myInventory = openAuctions.get(inventory);

                        int page = myInventory.getPage();
                        for(int i = (page*36); i < (page*36)+36; i++){
                            if(items.length() > i){
                                ItemParser item = new ItemParser(
                                        JsonItemStack.fromJSON(items.getJSONObject(i).getJSONObject("item")),
                                        items.getJSONObject(i).getDouble("price"),
                                        items.getJSONObject(i).getLong("time"));
                                inventory.setItem(i-(page*36), item.getItem(myInventory.getPlayer()));

                            }else{
                                break;
                            }
                        }
                    }
                }
            }
        }, 40, 40);
    }

    public static void openAuction(Player player, int page){
        Inventory inv = Bukkit.createInventory(null, 54, "§l§8Auction");

        page = (page < 0) ? 0 : page;

        //GET LIST OF ITEMS
        if(items.length() > page*36){
            for(int i = (page*36); i < (page*36)+36; i++){
                if(items.length() > i){
                    ItemParser item = new ItemParser(
                            JsonItemStack.fromJSON(items.getJSONObject(i).getJSONObject("item")),
                            items.getJSONObject(i).getDouble("price"),
                            items.getJSONObject(i).getLong("time"));
                    inv.setItem(i-(page*36), item.getItem(player));

                }else{
                    break;
                }
            }

            double pages = (double) items.length()/36;
            for(int i = 0; i < pages; i++){
                if(i < 9){
                    ItemStack item = new ItemStack(Material.PAPER, i+1);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName("§6§lPage "+(i+1));
                    itemMeta.setLore(Arrays.asList("§7Click to view this category"));
                    item.setItemMeta(itemMeta);
                    inv.setItem(45+i, item);

                }else{
                    break;
                }
            }
        }

        openAuctions.put(inv, new MyInventory(player, page));
        player.openInventory(inv);
    }

    public static boolean isAuction(Inventory inventory){
        return openAuctions.containsKey(inventory);
    }

    public static void closeAuction(Inventory inventory){
        if(openAuctions.containsKey(inventory)){
            openAuctions.remove(inventory);
        }
    }

    public static void onAuctionClick(int slot, Inventory inventory){
        MyInventory myInventory = openAuctions.get(inventory);

        if(slot < 36){
            int slotPage = slot+(myInventory.getPage()*36);
            if(items.length() > slotPage){
                ItemStack item = JsonItemStack.fromJSON(items.getJSONObject(slotPage).getJSONObject("item"));

                int space = 0;
                for(int i = 0; i <= 35; i++){
                    ItemStack content = myInventory.getPlayer().getInventory().getItem(i);
                    if(content == null){
                        space = item.getMaxStackSize();
                        break;

                    }else if(content.getType() == item.getType()){
                        space += item.getMaxStackSize()-content.getAmount();

                        if(space >= item.getMaxStackSize()){
                            break;
                        }
                    }
                }

                if(space >= item.getAmount()){
                    double money = getPlayersMoney(myInventory.getPlayer().getUniqueId());
                    double cost = items.getJSONObject(slotPage).getDouble("price");

                    if(money >= cost){
                        myInventory.getPlayer().getInventory().addItem(item);
                        setPlayersMoney(myInventory.getPlayer().getUniqueId(), money-cost);

                        UUID owner = UUID.fromString(items.getJSONObject(slotPage).getString("owner"));
                        setPlayersMoney(owner, getPlayersMoney(owner)+cost);

                        OfflinePlayer ownerPlayer = Bukkit.getOfflinePlayer(owner);

                        if(ownerPlayer != null && ownerPlayer.isOnline()){
                            myInventory.getPlayer().sendMessage("§a"+myInventory.getPlayer().getName()+"§7 has purchased §a"+item.getType().name()+"§7 from you in the auction.");
                        }

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        myInventory.getPlayer().sendMessage("§7You have purchased §a"+item.getAmount()+" "+item.getType().name()+"§7 your new balance is §a$"+df2.format(money-cost)+"§7.");

                        removeItem(slotPage);

                    }else{
                        myInventory.getPlayer().sendMessage("§cYour inventory is full, please remove some items before you buy.");
                    }
                }else{
                    myInventory.getPlayer().sendMessage("§cYou don't have enough money to purchase!");
                }
            }
        }else if(slot > 44 && items.length()/36 >= slot-45){
            int page = slot-45;

            if(items.length() > page*36 && page != myInventory.getPage()){
                myInventory.setPage(page);
                inventory.clear();

                for(int i = (page*36); i < (page*36)+36; i++){
                    if(items.length() > i){
                        ItemParser item = new ItemParser(
                                JsonItemStack.fromJSON(items.getJSONObject(i).getJSONObject("item")),
                                items.getJSONObject(i).getDouble("price"),
                                items.getJSONObject(i).getLong("time"));
                        inventory.setItem(i-(page*36), item.getItem(myInventory.getPlayer()));

                    }else{
                        break;
                    }
                }

                double pages = (double) items.length()/36;
                for(int i = 0; i < pages; i++){
                    if(i < 9){
                        ItemStack item = new ItemStack(Material.PAPER, i+1);
                        ItemMeta itemMeta = item.getItemMeta();
                        itemMeta.setDisplayName("§6§lPage "+(i+1));
                        itemMeta.setLore(Arrays.asList("§7Click to view this category"));
                        item.setItemMeta(itemMeta);
                        inventory.setItem(45+i, item);

                    }else{
                        break;
                    }
                }
            }
        }
    }

    private static void removeItem(int slotItem){
        if(items.length() > slotItem){
            items.remove(slotItem);
            write();

            try{
                for(Inventory inventory : openAuctions.keySet()){
                    MyInventory myInventory = openAuctions.get(inventory);

                    int cpage = myInventory.getPage();

                    if(items.length()-1 < cpage*36){
                        cpage--;
                    }

                    cpage = (cpage < 0) ? 0 : cpage;

                    myInventory.setPage(cpage);
                    inventory.clear();

                    for(int i = (cpage*36); i < (cpage*36)+36; i++){
                        if(items.length() > i){
                            ItemParser item = new ItemParser(
                                    JsonItemStack.fromJSON(items.getJSONObject(i).getJSONObject("item")),
                                    items.getJSONObject(i).getDouble("price"),
                                    items.getJSONObject(i).getLong("time"));
                            inventory.setItem(i-(cpage*36), item.getItem(myInventory.getPlayer()));

                        }else{
                            break;
                        }
                    }

                    double pages = (double) items.length()/36;
                    for(int i = 0; i < pages; i++){
                        if(i < 9){
                            ItemStack item = new ItemStack(Material.PAPER, i+1);
                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName("§6§lPage "+(i+1));
                            itemMeta.setLore(Arrays.asList("§7Click to view this category"));
                            item.setItemMeta(itemMeta);
                            inventory.setItem(45+i, item);

                        }else{
                            break;
                        }
                    }
                }
            }catch(Exception e){
            }
        }
    }

    private static void removeItemForReclaim(int location){
        if(items.length() > location){
            writeAddReclaim(UUID.fromString(items.getJSONObject(location).getString("owner")), items.getJSONObject(location).getJSONObject("item"));
            items.remove(location);

            try{
                for(Inventory inventory : openAuctions.keySet()){
                    MyInventory myInventory = openAuctions.get(inventory);

                    int cpage = myInventory.getPage();

                    if(items.length()-1 < cpage*36){
                        cpage--;
                    }

                    cpage = (cpage < 0) ? 0 : cpage;

                    myInventory.setPage(cpage);
                    inventory.clear();

                    for(int i = (cpage*36); i < (cpage*36)+36; i++){
                        if(items.length() > i){
                            ItemParser item = new ItemParser(
                                    JsonItemStack.fromJSON(items.getJSONObject(i).getJSONObject("item")),
                                    items.getJSONObject(i).getDouble("price"),
                                    items.getJSONObject(i).getLong("time"));
                            inventory.setItem(i-(cpage*36), item.getItem(myInventory.getPlayer()));

                        }else{
                            break;
                        }
                    }

                    double pages = (double) items.length()/36;
                    for(int i = 0; i < pages; i++){
                        if(i < 9){
                            ItemStack item = new ItemStack(Material.PAPER, i+1);
                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setDisplayName("§6§lPage "+(i+1));
                            itemMeta.setLore(Arrays.asList("§7Click to view this category"));
                            item.setItemMeta(itemMeta);
                            inventory.setItem(45+i, item);

                        }else{
                            break;
                        }
                    }
                }
            }catch(Exception e){
            }
        }
    }

    public static void offerItem(Player player, ItemStack item, int amount, double price){
        if(items.length() < 325){
            if(getTotalReclaims(player)+getTotalAuctioned(player) < 11){
                if(amount <= item.getMaxStackSize()){
                    if(player.getInventory().containsAtLeast(item, amount)){
                        JSONObject jitem = new JSONObject();
                        jitem.put("price", price);
                        jitem.put("item", JsonItemStack.toJSON(item, amount));
                        jitem.put("time", new Date().getTime());
                        jitem.put("owner", player.getUniqueId().toString());
                        items.put(jitem);

                        ItemStack tmp = new ItemStack(item.getType(), amount);
                        tmp.setItemMeta(item.getItemMeta());
                        player.getInventory().removeItem(tmp);

                        write();

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        Bukkit.broadcastMessage("§6"+player.getName()+"§7 has auctioned §a"+amount+" "+item.getType().name()+"§7 for only §a$"+df2.format(price)+"§7!");

                    }else{
                        player.sendMessage("§cYou don't have the amount of the item you specified.");
                    }
                }else{
                    player.sendMessage("§cYou cant offer more than the max stack size of an item.");
                }
            }else{
                player.sendMessage("§cYou can only have 10 items in the auction and/or reclaim at a time.");
            }
        }else{
            player.sendMessage("§cThe auction is currently full, please wait until its not.");
        }
    }

    private static int getTotalAuctioned(Player player){
        int total = 0;
        if(items.length() > 0){
            for(int i = 0; i < items.length(); i++){
                if(items.getJSONObject(i).getString("owner").equals(player.getUniqueId().toString())){
                    total++;
                }
            }
        }
        return total;
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    if(!plugin.getDataFolder().exists()){
                        plugin.getDataFolder().mkdirs();
                    }

                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"auction.json"));
                    out.write(items.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
