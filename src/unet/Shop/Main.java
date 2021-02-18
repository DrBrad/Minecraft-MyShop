package unet.Shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import unet.Shop.Auction.MyAuction;
import unet.Shop.Currency.CurrencyHandler;
import unet.Shop.Shop.MyShop;

import static unet.Shop.Auction.MyAuction.*;
import static unet.Shop.Reclaims.MyReclaims.*;
import static unet.Shop.Shop.MyShop.*;

public class Main extends JavaPlugin {

    public static Plugin plugin;
    //TRADE

    @Override
    public void onEnable(){
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new MyEventHandler(), this);
        getCommand("shop").setExecutor(new ShopCommands());
        getCommand("money").setExecutor(new ShopCommands());
        getCommand("setmoney").setExecutor(new ShopCommands());
        getCommand("pay").setExecutor(new ShopCommands());
        getCommand("auction").setExecutor(new ShopCommands());
        getCommand("reclaim").setExecutor(new ShopCommands());

        new Config();
        new CurrencyHandler();
        new MyShop();
        new MyAuction();
    }

    @Override
    public void onDisable(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(isShop(player.getInventory()) || isAuction(player.getInventory()) || isReclaim(player.getInventory())){
                player.closeInventory();
            }
            if(isShop(player.getOpenInventory().getTopInventory()) ||
                    isAuction(player.getOpenInventory().getTopInventory()) ||
                    isReclaim(player.getOpenInventory().getTopInventory())){
                player.closeInventory();
            }
        }
    }
}
