package easylightlevel;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import easylightlevel.EasyLightLevel;
import easylightlevel.IPermissionHandler;
import easylightlevel.MockPermissionHandler;
import easylightlevel.PermissionHandlerWrapper;

public class EasyLightLevel extends JavaPlugin implements CommandExecutor, Listener {

	public enum INFO_STYLE_ENUM{
		NUMBER,
		TEXT,
		NUMBERTEXT,
		TEXTNUMBER,
		BLOCK,
		BLOCKWOOL
	}
	
	private static EasyLightLevel plugin;
	private static IPermissionHandler permissions;
	
	private String DEFAULT_SECTION_STRING = "defaults";
	
	private int LEFT_CLICK_ITEM = 50;
	private String LEFT_CLICK_ITEM_STRING = "left_click_item";
	
	private int SHOW_AREA_RADIUS = 4;
	private String SHOW_AREA_RADIUS_STRING = "show_area_radius";
	
	private int SHOW_AREA_HEIGHT = 2;
	private String SHOW_AREA_HEIGHT_STRING = "show_area_height";
	
	private int SHOW_HOLOGRAM_SECONDS = 3;
	private String SHOW_HOLOGRAM_SECONDS_STRING = "show_hologram_seconds";
	
	private int SHOW_AREA_HOLOGRAM_SECONDS = 3;
	private String SHOW_AREA_HOLOGRAM_SECONDS_STRING = "show_area_hologram_seconds";
	
	private int INFO_STYLE = 0;
	private String INFO_STYLE_STRING = "info_style";
	
	public void onEnable() {
		plugin = this;
		
		getServer().getPluginManager().registerEvents(this, this);

		// First add callback thingy for my command
		getCommand("easylightlevel").setExecutor(plugin);

		// if some settings are missing, copy defaults
		getConfig().options().copyDefaults(true);

		// Save Config (if e.g. defaults where copied)
		saveConfig();
		
		LoadPlugin();
	}
	
	public void onDisable(){
		
	}
	
	public void LoadPlugin(){
		//Load Permission things
		RegisteredServiceProvider<Permission> permissionsPlugin = null;
		if (getServer().getPluginManager().isPluginEnabled("Vault")) {
			log("Vault detected. hooked.");

			permissionsPlugin = getServer().getServicesManager()
					.getRegistration(Permission.class);

			permissions = new PermissionHandlerWrapper(
					(Permission) permissionsPlugin.getProvider());
		} else {
			log("Vault not detected for permissions, defaulting to Bukkit Permissions");
			permissions = new MockPermissionHandler();
		}
		
		//Load Config here
		
		this.LEFT_CLICK_ITEM = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + LEFT_CLICK_ITEM_STRING);
		
		this.SHOW_AREA_RADIUS = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + SHOW_AREA_RADIUS_STRING);
		
		this.SHOW_AREA_HEIGHT = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + SHOW_AREA_HEIGHT_STRING);
		
		this.SHOW_HOLOGRAM_SECONDS = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + SHOW_HOLOGRAM_SECONDS_STRING);
		
		this.SHOW_AREA_HOLOGRAM_SECONDS = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + SHOW_AREA_HOLOGRAM_SECONDS_STRING);
		
		this.INFO_STYLE = getConfig().getInt(
				DEFAULT_SECTION_STRING + "." + INFO_STYLE_STRING);
		
		//Debug, because BlockChange is buggy will destroy world
		if(INFO_STYLE == 4 || INFO_STYLE == 5){
			INFO_STYLE = 0;
		}
		if(INFO_STYLE < 0 || INFO_STYLE > 3){
			INFO_STYLE = 0;
		}
	}
	
	public void Reload(){
		log("Reloading");
		onDisable();
		onEnable();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1){
			if(!(sender instanceof Player)){
				return true;
			}
			Player player = (Player) sender;
			
			if(!permissions.has(player, "easylightlevel.area")){
				msg(player, "&aYou don't have permissions to use EasyLightLevel");
				return true;
			}
			
			if(args[0].equalsIgnoreCase("showarea")){
				ShowLightLevelAround(player);
			}
		}else{
			ShowInfo(sender);
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_BLOCK){
			Player player = e.getPlayer();
			Block block = e.getClickedBlock();
			
			if(permissions.has(player, "easylightlevel.show")){
				ItemStack item = player.getItemInHand();
				if(item != null){
					if(item.getType().getId() == LEFT_CLICK_ITEM){
						ShowLightLevelAtBlock(block);
					}
				}
			}
		}
	}
	
	private void ShowInfo(Block block, Block blockabove, int seconds){
		INFO_STYLE_ENUM infoStyle = null;
		
		try{
			infoStyle = INFO_STYLE_ENUM.values()[INFO_STYLE];
		}catch(IndexOutOfBoundsException e){
			log("Check INFO_STYLE setting, " + INFO_STYLE + " is not a correct style number");
			return;
		}
		
		switch(infoStyle){
			case NUMBER:
				new HologramText(plugin, blockabove, INFO_STYLE, seconds);
				break;
			case TEXT:
				new HologramText(plugin, blockabove, INFO_STYLE, seconds);
				break;
			case NUMBERTEXT:
				new HologramText(plugin, blockabove, INFO_STYLE, seconds);
				break;
			case TEXTNUMBER:
				new HologramText(plugin, blockabove, INFO_STYLE, seconds);
				break;
			case BLOCK:
				new BlockChanger(plugin, block, INFO_STYLE, seconds);
				break;
			case BLOCKWOOL:
				new BlockChanger(plugin, block, INFO_STYLE, seconds);
				break;
			default:
				break;
		}
	}
	
	private void ShowLightLevelAtBlock(Block block){
		Location position = block.getLocation();
		
		position.add(new Vector(0,1,0));
		Block blockabove = block.getWorld().getBlockAt(position);
		
		if(block.getType().isSolid()){
			if(blockabove.getType() == Material.AIR){
				ShowInfo(block, blockabove, SHOW_HOLOGRAM_SECONDS);
			}
		}
	}
	
	private void ShowLightLevelAround(Player player){
		
		Location playerPosition = player.getLocation();
		
		int amount = 0;
		
		for(int x = -SHOW_AREA_RADIUS; x < SHOW_AREA_RADIUS; x++){
			for(int z = -SHOW_AREA_RADIUS; z < SHOW_AREA_RADIUS; z++){
				for(int y = -SHOW_AREA_HEIGHT; y < SHOW_AREA_HEIGHT; y++){
					Location position = playerPosition.clone();
					
					position.add(new Vector(x,y,z));
					
					Block block = player.getWorld().getBlockAt(position);
					
					position.add(new Vector(0,1,0));
					Block blockabove = player.getWorld().getBlockAt(position);
					
					if(block.getType().isSolid()){
						if(blockabove.getType() == Material.AIR){
							ShowInfo(block, blockabove, SHOW_AREA_HOLOGRAM_SECONDS);
							amount++;
						}
					}
				}
			}
		}
		
		msg(player, "&aJust created &6" + amount + "&a infos.");
	}
	
	private void ShowInfo(CommandSender sender){
		msg(sender, "&eEasyLightLevel v" + plugin.getDescription().getVersion()
				+ " help");
		msg(sender, "&6/eit i &b<name/id>");
	}
	
	private void log(String msg) {
		getServer().getLogger().info("[EasyLightLevel] " + msg);
	}
	
	@SuppressWarnings("unused")
	private boolean isInt(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException nFE) {
		}
		return false;
	}
	
	private void msg(CommandSender sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	private void msg(Player sender, String msg) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
}
