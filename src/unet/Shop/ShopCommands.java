package unet.Shop;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static unet.Shop.Auction.MyAuction.*;
import static unet.Shop.Currency.CurrencyHandler.*;
import static unet.Shop.Reclaims.MyReclaims.*;
import static unet.Shop.Shop.MyShop.*;

public class ShopCommands implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            switch(command.getName()){
                case "shop":
                    return shop(((Player) commandSender));

                case "money":
                    return money(((Player) commandSender));

                case "setmoney":
                    return setMoney(((Player) commandSender), args);

                case "pay":
                    return pay(((Player) commandSender), args);

                case "auction":
                    return auction(((Player) commandSender), args);

                case "reclaim":
                    return reclaim(((Player) commandSender));
            }
        }

        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args){
        if(commandSender instanceof Player){
            if(args.length > 0){
                String cmd = command.getName().toLowerCase();
                ArrayList<String> tabComplete = new ArrayList<>();

                if(args.length > 0){
                    switch(cmd){
                        case "setmoney":
                            if(args.length == 2){
                                tabComplete.add("AMOUNT_OF_MONEY");
                            }else if(args.length == 1){
                                for(Player player : Bukkit.getOnlinePlayers()){
                                    tabComplete.add(player.getName());
                                }
                            }
                            break;

                        case "pay":
                            if(args.length == 2){
                                tabComplete.add("AMOUNT_OF_MONEY");
                            }else if(args.length == 1){
                                for(Player player : Bukkit.getOnlinePlayers()){
                                    tabComplete.add(player.getName());
                                }
                            }
                            break;

                        case "auction":
                            if(args.length == 2){
                                tabComplete.add("AMOUNT_OF_ITEM");

                            }else if(args.length == 3){
                                tabComplete.add("PRICE");

                            }else if(args.length == 1){
                                tabComplete.add("offer");
                            }
                            break;
                    }

                }else{
                    tabComplete.add("shop");
                    tabComplete.add("money");
                    tabComplete.add("setmoney");
                    tabComplete.add("pay");
                    tabComplete.add("auction");
                    tabComplete.add("reclaim");
                }

                return tabComplete;
            }
        }

        return null;
    }

    private boolean shop(Player player){
        if(player.hasPermission("shop") || player.isOp()){
            openMainInventory(player);
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean money(Player player){
        if(player.hasPermission("money") || player.isOp()){
            DecimalFormat df2 = new DecimalFormat("#,##0.00");
            double money = getPlayersMoney(player.getUniqueId());
            if(money == 0){
                player.sendMessage("§7Your balance is: §c$"+df2.format(money)+"§7.");
            }else{
                player.sendMessage("§7Your balance is: §a$"+df2.format(money)+"§7.");
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean setMoney(Player player, String[] args){
        if(player.hasPermission("setmoney") || player.isOp()){
            if(args.length > 1){
                if(player.isOp()){
                    Player receiver = Bukkit.getPlayer(args[0]);
                    if(receiver != null && player.isOnline()){
                        Double amount = Double.parseDouble(args[1]);
                        setPlayersMoney(receiver.getUniqueId(), amount);

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        player.sendMessage("§7You have set §a"+receiver.getName()+"§7's money to §a$"+df2.format(amount)+"§7.");
                        receiver.sendMessage("§a"+player.getName()+"§7 has set your money to §a$"+df2.format(amount)+"§7.");

                    }else{
                        player.sendMessage("§cThe player specified is ether not online or doesn't exist.");
                    }
                }else{
                    player.sendMessage("§cOnly server admins can set players money.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify an player and an amount.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean pay(Player player, String[] args){
        if(player.hasPermission("pay") || player.isOp()){
            if(args.length > 1){
                Player receiver = Bukkit.getPlayer(args[0]);
                if(receiver != null && player.isOnline()){
                    Double amount = Double.parseDouble(args[1]);
                    double money = getPlayersMoney(player.getUniqueId());
                    if(money >= amount){
                        setPlayersMoney(player.getUniqueId(), money-amount);
                        setPlayersMoney(receiver.getUniqueId(), getPlayersMoney(receiver.getUniqueId())+amount);

                        DecimalFormat df2 = new DecimalFormat("#,##0.00");
                        player.sendMessage("§7You have payed §a"+df2.format(amount)+"§7 to §a"+receiver.getName()+"§7.");
                        receiver.sendMessage("§a"+player.getName()+" has payed you §a$"+df2.format(amount)+"§7.");

                    }else{
                        player.sendMessage("§cYou don't have enough money to pay that amount.");
                    }
                }else{
                    player.sendMessage("§cThe player specified is ether not online or doesn't exist.");
                }
                return true;
            }else{
                player.sendMessage("§cPlease specify an player and an amount.");
            }
            return false;
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
            return true;
        }
    }

    private boolean auction(Player player, String[] args){
        if(player.hasPermission("auction") || player.isOp()){
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("offer")){
                    int amount = Integer.parseInt(args[1]);
                    double price = Double.parseDouble(args[2]);

                    offerItem(player, player.getInventory().getItemInMainHand(), amount, price);
                }
            }else{
                openAuction(player, 0);
            }
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }

    private boolean reclaim(Player player){
        if(player.hasPermission("reclaim") || player.isOp()){
            openReclaim(player);
        }else{
            player.sendMessage("§cYou don't have permission to perform this command.");
        }
        return true;
    }
}
