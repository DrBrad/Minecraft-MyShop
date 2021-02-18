package unet.Shop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import unet.Shop.Shop.MyInventory;

import static unet.Shop.Auction.MyAuction.*;
import static unet.Shop.Config.*;
import static unet.Shop.Currency.CurrencyHandler.*;
import static unet.Shop.Reclaims.MyReclaims.*;
import static unet.Shop.Shop.MyShop.*;

public class MyEventHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if(!hasPlayedBefore(event.getPlayer().getUniqueId())){
            setPlayersMoney(event.getPlayer().getUniqueId(), getStartingWealth());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(isShop(event.getClickedInventory())){
            event.setCancelled(true);

            MyInventory inventory = getShop(event.getClickedInventory());
            if(inventory != null){
                if(event.getClick().isLeftClick()){
                    if(event.getClick().isShiftClick()){
                        inventory.onShiftLeftClick(event.getSlot(), ((Player)event.getWhoClicked()));

                    }else{
                        inventory.onLeftClick(event.getSlot(), ((Player)event.getWhoClicked()));
                    }

                }else if(event.getClick().isRightClick()){
                    if(event.getClick().isShiftClick()){
                        inventory.onShiftRightClick(event.getSlot(), ((Player)event.getWhoClicked()));

                    }else{
                        inventory.onRightClick(event.getSlot(), ((Player)event.getWhoClicked()));
                    }
                }
            }else{
                ((Player)event.getWhoClicked()).sendMessage("§cThe shop specified doesn't exist.");
            }

        }else if(isAuction(event.getClickedInventory())){
            event.setCancelled(true);

            if(event.getClick().isLeftClick()){
                onAuctionClick(event.getSlot(), event.getClickedInventory());
            }

        }else if(isReclaim(event.getClickedInventory())){
            event.setCancelled(true);

            if(event.getClick().isLeftClick()){
                onReclaimClick(event.getSlot(), event.getClickedInventory());
            }

        }else if(isShop(event.getWhoClicked().getOpenInventory().getTopInventory()) ||
                isAuction(event.getWhoClicked().getOpenInventory().getTopInventory()) ||
                isReclaim(event.getWhoClicked().getOpenInventory().getTopInventory())){
            if(event.isShiftClick()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event){
        if(isShop(event.getSource()) || isShop(event.getDestination()) ||
                isAuction(event.getSource()) || isAuction(event.getDestination()) ||
                isReclaim(event.getSource()) || isReclaim(event.getDestination())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        if(isShop(event.getInventory()) || isAuction(event.getInventory()) || isReclaim(event.getInventory())){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(isShop(event.getInventory())){
            closeShop(event.getInventory());

        }else if(isAuction(event.getInventory())){
            closeAuction(event.getInventory());

        }else if(isReclaim(event.getInventory())){
            closeReclaim(event.getInventory());
        }
    }
}
