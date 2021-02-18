package unet.Shop.Reclaims;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONTokener;
import unet.Shop.Auction.JsonItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import static unet.Shop.Main.plugin;

public class MyInventory {

    private Player player;
    private Inventory inventory;
    private JSONArray items;

    public MyInventory(Player player){
        this.player = player;
        inventory = Bukkit.createInventory(null, 36, "§l§8Reclaim");

        try{
            File auctionReclaim = new File(plugin.getDataFolder()+File.separator+"reclaim");
            if(!auctionReclaim.exists()){
                auctionReclaim.mkdirs();
            }

            File reclaimPlayer = new File(auctionReclaim.getPath()+File.separator+player.getUniqueId().toString()+".json");

            if(reclaimPlayer.exists()){
                items = new JSONArray(new JSONTokener(new FileInputStream(reclaimPlayer)));

                if(items.length() > 0){
                    for(int i = 0; i < items.length(); i++){
                        inventory.setItem(i, JsonItemStack.fromJSON(items.getJSONObject(i)));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        player.openInventory(inventory);
    }

    public Inventory getInventory(){
        return inventory;
    }

    public void onRemoved(int slot){
        ItemStack item = JsonItemStack.fromJSON(items.getJSONObject(slot));

        if(player.getInventory().addItem(item).isEmpty()){
            items.remove(slot);
            inventory.clear();

            if(items.length() > 0){
                for(int i = 0; i < items.length(); i++){
                    inventory.setItem(i, JsonItemStack.fromJSON(items.getJSONObject(i)));
                }
            }
            write();

        }else{
            player.sendMessage("§cYou can't reclaim items with a full inventory.");
        }
    }

    public void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    File auctionReclaim = new File(plugin.getDataFolder()+File.separator+"reclaim");
                    if(!auctionReclaim.exists()){
                        auctionReclaim.mkdirs();
                    }

                    FileWriter out = new FileWriter(new File(auctionReclaim.getPath()+File.separator+player.getUniqueId().toString()+".json"));
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
