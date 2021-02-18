package unet.Shop.Currency;

import org.bukkit.Bukkit;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.UUID;

import static unet.Shop.Main.*;

public class CurrencyHandler {

    private static JSONObject monies = new JSONObject();

    public CurrencyHandler(){
        if(plugin.getDataFolder().exists()){
            try{
                File playersFile = new File(plugin.getDataFolder()+File.separator+"wealth.json");
                if(playersFile.exists()){
                    monies = new JSONObject(new JSONTokener(new FileInputStream(playersFile)));
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public static double getPlayersMoney(UUID uuid){
        if(monies.has(uuid.toString())){
            return monies.getDouble(uuid.toString());
        }
        return 0;
    }

    public static void setPlayersMoney(UUID uuid, double money){
        monies.put(uuid.toString(), money);
        write();
    }

    public static boolean hasPlayedBefore(UUID uuid){
        return monies.has(uuid.toString());
    }

    private static void write(){
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
            @Override
            public void run(){
                try{
                    if(!plugin.getDataFolder().exists()){
                        plugin.getDataFolder().mkdirs();
                    }

                    FileWriter out = new FileWriter(new File(plugin.getDataFolder()+File.separator+"wealth.json"));
                    out.write(monies.toString());
                    out.flush();
                    out.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
