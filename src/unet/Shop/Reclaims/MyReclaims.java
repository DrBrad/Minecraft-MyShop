package unet.Shop.Reclaims;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import unet.Shop.Auction.JsonItemStack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.UUID;

import static unet.Shop.Main.plugin;

public class MyReclaims {

    private static HashMap<Inventory, MyInventory> openReclaims = new HashMap<>();

    public static void openReclaim(Player player){
        MyInventory myInventory = new MyInventory(player);
        openReclaims.put(myInventory.getInventory(), myInventory);
    }

    public static boolean isReclaim(Inventory inventory){
        return openReclaims.containsKey(inventory);
    }

    public static void closeReclaim(Inventory inventory){
        if(openReclaims.containsKey(inventory)){
            openReclaims.remove(inventory);
        }
    }

    public static void onReclaimClick(int slot, Inventory inventory){
        MyInventory myInventory = openReclaims.get(inventory);
        myInventory.onRemoved(slot);
    }

    public static int getTotalReclaims(Player player){
        try{
            File auctionReclaim = new File(plugin.getDataFolder()+File.separator+"reclaim");
            if(!auctionReclaim.exists()){
                auctionReclaim.mkdirs();
            }

            File reclaimPlayer = new File(auctionReclaim.getPath()+File.separator+player.getUniqueId().toString()+".json");

            if(reclaimPlayer.exists()){
                JSONArray items = new JSONArray(new JSONTokener(new FileInputStream(reclaimPlayer)));
                return items.length();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    public static void writeAddReclaim(UUID uuid, JSONObject jitem){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    File auctionReclaim = new File(plugin.getDataFolder()+File.separator+"reclaim");
                    if(!auctionReclaim.exists()){
                        auctionReclaim.mkdirs();
                    }

                    File reclaimPlayer = new File(auctionReclaim.getPath()+File.separator+uuid.toString()+".json");

                    JSONArray json = new JSONArray();

                    if(reclaimPlayer.exists()){
                        json = new JSONArray(new JSONTokener(new FileInputStream(reclaimPlayer)));
                    }

                    json.put(jitem);

                    FileWriter out = new FileWriter(reclaimPlayer);
                    out.write(json.toString());
                    out.flush();
                    out.close();

                    OfflinePlayer owner = Bukkit.getOfflinePlayer(uuid);
                    if(owner != null && owner.isOnline()){
                        owner.getPlayer().sendMessage("§cItem wasn't sold, it was added to your reclaim.");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
