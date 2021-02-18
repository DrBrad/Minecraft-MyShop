package unet.Shop.Auction;

import org.bukkit.entity.Player;

public class MyInventory {

    private Player player;
    private int page;

    public MyInventory(Player player, int page){
        this.player = player;
        this.page = page;
    }

    public Player getPlayer(){
        return player;
    }

    public void setPage(int page){
        this.page = page;
    }

    public int getPage(){
        return page;
    }
}
