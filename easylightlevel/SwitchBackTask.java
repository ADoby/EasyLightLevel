package easylightlevel;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class SwitchBackTask implements Runnable{

	private Block block;
	private Material matBefore;
	private byte dataBefore;
    
	public SwitchBackTask(Block block, Material matBefore, byte dataBefore){
		this.block = block;
		this.matBefore = matBefore;
		this.dataBefore = dataBefore;
	}
	
	@SuppressWarnings("deprecation")
	public void run() {
    	//Reset things here
    	block.setType(matBefore);
    	block.setData(dataBefore);
    }
	
}
