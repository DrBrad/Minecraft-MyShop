package unet.Shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static unet.Shop.Main.plugin;

public class Config {

    private static double startingWealth = 1500;
    private static String mainInventoryName = "main";
    private static long auctionTime = 3600000;

    public Config(){
        File configFile = new File(plugin.getDataFolder()+File.separator+"config.yml");
        try{
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

            if(configFile.exists()){
                mainInventoryName = config.getString("main-inventory");
                startingWealth = config.getDouble("starting-wealth");
                auctionTime = config.getLong("auction-time");

            }else{
                config.set("main-inventory", "main");
                config.set("starting-wealth", 1500);
                config.set("auction-time", 3600000);

                config.save(configFile);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        File shops = new File(plugin.getDataFolder()+File.separator+"shops");
        if(!shops.exists()){
            shops.mkdirs();

            copy("/main.yml", shops.getPath()+File.separator+"main.yml");
            copy("/brewing.yml", shops.getPath()+File.separator+"brewing.yml");
            copy("/building.yml", shops.getPath()+File.separator+"building.yml");
            copy("/building1.yml", shops.getPath()+File.separator+"building1.yml");
            copy("/dye.yml", shops.getPath()+File.separator+"dye.yml");
            copy("/food.yml", shops.getPath()+File.separator+"food.yml");
            copy("/food1.yml", shops.getPath()+File.separator+"food1.yml");
            copy("/misc.yml", shops.getPath()+File.separator+"misc.yml");
            copy("/mobs.yml", shops.getPath()+File.separator+"mobs.yml");
            copy("/ores.yml", shops.getPath()+File.separator+"ores.yml");
            copy("/potions.yml", shops.getPath()+File.separator+"potions.yml");
            copy("/raid.yml", shops.getPath()+File.separator+"raid.yml");
            copy("/spawner.yml", shops.getPath()+File.separator+"spawner.yml");
        }
    }

    public static String getMainInventoryName(){
        return mainInventoryName;
    }

    public static double getStartingWealth(){
        return startingWealth;
    }

    public static long getMaxAuctionTime(){
        return auctionTime;
    }

    private void copy(String res, String to){
        try{
            InputStream is = getClass().getResourceAsStream(res);
            OutputStream os = new FileOutputStream(to);
            byte[] buffer = new byte[4096];
            int length;
            while((length = is.read(buffer)) > 0){
                os.write(buffer, 0, length);
            }
            os.close();
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
