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

public class HologramText {
	
	Hologram hologram;
	
	public HologramText(JavaPlugin plugin, Block block, int seconds){
		
		BlockState state = block.getState();
		Location position = block.getLocation();
		
		position.add(new Vector(0.5,-0.4,0.5));
		
		int lightLevel = state.getLightLevel();
		
		ChatColor color = ChatColor.WHITE;
		String text = "Normal"; 
		
		if(lightLevel <= 7){
			color = ChatColor.RED;
			text = "Unsave";
		}else if(lightLevel >= 8){
			color = ChatColor.GREEN;
			text = "Save";
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
