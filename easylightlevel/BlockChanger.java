package easylightlevel;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import easylightlevel.EasyLightLevel.INFO_STYLE_ENUM;

public class BlockChanger implements Listener {

	private Block block = null;
	private Material matBefore = null;
	private byte dataBefore = 0;
	
	private BukkitTask runningTask;
	
	@SuppressWarnings("deprecation")
	public BlockChanger(JavaPlugin plugin, Block block, int infoType, int seconds){
		
		BlockState state = block.getState();
		Location position = block.getLocation();
		
		position.add(new Vector(0.5,-0.4,0.5));
		
		int lightLevel = state.getLightLevel();
		
		this.block = block;
		matBefore = block.getType();
		dataBefore = block.getData();
		
		INFO_STYLE_ENUM infoStyle = null;
		Material newMat = null;
		byte newData = 0;
		
		try{
			infoStyle = INFO_STYLE_ENUM.values()[infoType];
		}catch(IndexOutOfBoundsException e){
			return;
		}
		
		switch(infoStyle){
			case NUMBER:
				break;
			case TEXT:
				break;
			case NUMBERTEXT:
				break;
			case TEXTNUMBER:
				break;
			case BLOCK:
				if(lightLevel <= 7){
					newMat = Material.REDSTONE_BLOCK;
				}else if(lightLevel >= 8){
					newMat = Material.EMERALD_BLOCK;
				}
				break;
			case BLOCKWOOL:
				if(lightLevel <= 7){
					newMat = Material.WOOL;
					newData = DyeColor.RED.getData();
				}else if(lightLevel >= 8){
					newMat = Material.WOOL;
					newData = DyeColor.GREEN.getData();
				}
				break;
			default:
				break;
		}
		
		if(newMat == null){
			return;
		}
		
		block.setType(newMat);
		block.setData(newData);
		
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
		runningTask = plugin.getServer().getScheduler().runTaskLater(plugin, new SwitchBackTask(block, matBefore, dataBefore), 20 * seconds);
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_BLOCK){
			Block block = e.getClickedBlock();
			
			if(block == this.block){
				block.setType(matBefore);
				block.setData(dataBefore);
				runningTask.cancel();
			}			
		}
	}
	
}
