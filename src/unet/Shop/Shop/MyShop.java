package unet.Shop.Shop;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;

import static unet.Shop.Config.*;
import static unet.Shop.Main.*;

public class MyShop {

    private static HashMap<String, MyInventory> shopInventories = new HashMap<>();
    public static HashMap<Inventory, String> openShops = new HashMap<>();

    public MyShop(){
        try{
            File shops = new File(plugin.getDataFolder()+File.separator+"shops");

            for(File shop : shops.listFiles()){
                FileConfiguration config = YamlConfiguration.loadConfiguration(shop);

                HashMap<Integer, ItemParser> items = new HashMap<>();
                if(config.contains("items")){
                    for(String key : config.getConfigurationSection("items").getKeys(false)){
                        int type = config.getInt("items."+key+".type");

                        ItemParser item = new ItemParser(Material.valueOf(config.getString("items."+key+".material")), type, config.getInt("items."+key+".amount"));

                        if(config.contains("items."+key+".potion-type")){
                            boolean potionExtended = config.contains("items."+key+".potion-extended") ? config.getBoolean("items."+key+".potion-extended") : false;
                            boolean potionUpgraded = config.contains("items."+key+".potion-upgraded") ? config.getBoolean("items."+key+".potion-upgraded") : false;
                            item.setPotion(config.getString("items."+key+".potion-type"), potionExtended, potionUpgraded);
                        }

                        if(type == 0){
                            item.setLink(config.getString("items."+key+".name"), config.getString("items."+key+".link"));
                        }else{
                            item.setPrices(config.getDouble("items."+key+".buy"), config.getDouble("items."+key+".sell"));
                        }
                        items.put(Integer.parseInt(key), item);
                    }
                }

                MyInventory myInventory = new MyInventory(config.getString("name"), config.getString("link"), config.getInt("size"), items);
                shopInventories.put(myInventory.getLink(), myInventory);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void openMainInventory(Player player){
        if(shopInventories.containsKey(getMainInventoryName())){
            Inventory inventory = shopInventories.get(getMainInventoryName()).getInventory(player);
            openShops.put(inventory, getMainInventoryName());
            player.openInventory(inventory);
        }
    }

    public static void openInventory(String name, Player player){
        if(shopInventories.containsKey(name)){
            Inventory inventory = shopInventories.get(name).getInventory(player);
            openShops.put(inventory, name);
            player.openInventory(inventory);
            return;
        }else{
            player.sendMessage("§cThe shop specified doesn't exist.");
        }
    }

    public static void closeShop(Inventory inventory){
        if(openShops.containsKey(inventory)){
            openShops.remove(inventory);
        }
    }

    public static boolean isShop(Inventory inventory){
        return openShops.containsKey(inventory);
    }

    public static MyInventory getShop(Inventory inventory){
        if(openShops.containsKey(inventory)){
            if(shopInventories.containsKey(openShops.get(inventory))){
                return shopInventories.get(openShops.get(inventory));
            }
        }
        return null;
    }
}
