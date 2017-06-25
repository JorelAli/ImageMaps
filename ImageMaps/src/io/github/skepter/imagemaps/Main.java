package io.github.skepter.imagemaps;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	private static HashMap<String, Integer> maps;
	
	@Override
	public void onEnable() {
		getCommand("map").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
		
		maps = new HashMap<String, Integer>();
		for(Block block : locations.get()) {
			blocks.put(block, showParticles(block.getLocation()));
		}

	}
	
	@Override
	public void onDisable() {
	}
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		
	}
	
	public static Main getInstance() {
		return JavaPlugin.getPlugin(Main.class);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("map")) {
			if(sender instanceof Player && args.length == 1) {
				Player player = (Player) sender;
				Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {

					@Override
					public void run() {
						
						try {
							URL url = new URL(args[0]);
							BufferedImage image = ImageIO.read(url);  
							
							
							try {
								Bukkit.getLogger().info(new ImageHashing(image).getHash());
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							
							ItemStack is = new ItemStack(Material.MAP);
							MapView map = Bukkit.createMap(player.getWorld());
							map.getRenderers().clear();
							
							MapRenderer renderer = new MapRenderer() {
								
								@Override
								public void render(MapView view, MapCanvas canvas, Player player) {
							        view.setScale(MapView.Scale.NORMAL);
									canvas.drawImage(0, 0, image);
								}
							};
							
							map.addRenderer(renderer);
							is.setDurability(map.getId());
							Bukkit.getScheduler().runTask(getInstance(), new Runnable() {

								@Override
								public void run() {
									player.getInventory().addItem(is);
								}
								
							});
						} catch(MalformedURLException e) {
							player.sendMessage("Could not access URL :/");
						} catch (IOException e) {
							player.sendMessage("Could not render image :/");
						}
						
						
						
					}
					
				});
			}
			return true;
		}
		return false;
	}
	
}
