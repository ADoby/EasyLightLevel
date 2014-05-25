package easylightlevel;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.dsh105.holoapi.HoloAPI;
import com.dsh105.holoapi.api.Hologram;
import com.dsh105.holoapi.api.HologramFactory;

import easylightlevel.EasyLightLevel.INFO_STYLE_ENUM;

public class HologramText {
	
	Hologram hologram;
	
	private String getText(int infoType, int lightLevel){
		String value = "";
		
		INFO_STYLE_ENUM infoStyle = null;
		
		try{
			infoStyle = INFO_STYLE_ENUM.values()[infoType];
		}catch(IndexOutOfBoundsException e){
			return String.valueOf(lightLevel);
		}
		
		switch(infoStyle){
			case NUMBER:
				value = String.valueOf(lightLevel);
				break;
			case TEXT:
				if(lightLevel <= 7){
					value = "Unsafe";
				}else if(lightLevel >= 8){
					value = "Safe";
				}
				break;
			case NUMBERTEXT:
				if(lightLevel <= 7){
					value = String.valueOf(lightLevel) + ": Unsafe";
				}else if(lightLevel >= 8){
					value = String.valueOf(lightLevel) + ": Safe";
				}
				break;
			case TEXTNUMBER:
				if(lightLevel <= 7){
					value = "Unsafe: " + String.valueOf(lightLevel);
				}else if(lightLevel >= 8){
					value = "Safe: " + String.valueOf(lightLevel);
				}
				break;
			case BLOCK:
				value = String.valueOf(lightLevel);
				break;
			case BLOCKWOOL:
				value = String.valueOf(lightLevel);
				break;
			default:
				value = String.valueOf(lightLevel);
				break;
		}		
		
		return value;
	}
	
	public HologramText(JavaPlugin plugin, Block block, int infoType, int seconds){
		
		BlockState state = block.getState();
		Location position = block.getLocation();
		
		position.add(new Vector(0.5,-0.4,0.5));
		
		int lightLevel = state.getLightLevel();
		
		ChatColor color = ChatColor.WHITE;
		String text = getText(infoType, lightLevel);
		
		if(lightLevel <= 7){
			color = ChatColor.RED;
		}else if(lightLevel >= 8){
			color = ChatColor.GREEN;
		}
		
		hologram = new HologramFactory(plugin)
		.withLocation(position)
		.withText(color + text)
		.withSimplicity(true)
		.build();
		
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                // Remove the hologram that was created above
                HoloAPI.getManager().stopTracking(hologram);
                
                HoloAPI.getManager().clearFromFile(hologram);
            }
        }, 20 * seconds);
	}
}
