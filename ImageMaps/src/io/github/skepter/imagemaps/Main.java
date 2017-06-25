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

	private ImageHashStorage storage;
	public static HashMap<String, Short> maps;
	
	@Override
	public void onEnable() {
		getCommand("map").setExecutor(this);
		getServer().getPluginManager().registerEvents(this, this);
		storage = new ImageHashStorage(this);
		maps = storage.get();

	}
	
	@Override
	public void onDisable() {
		storage.store(maps);
	}
	
	@EventHandler
	public void onWorldSave(WorldSaveEvent event) {
		storage.store(maps);
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
							
							boolean exists = false;
							try {
								ImageHashing hashing = new ImageHashing(image);
								if(maps.containsKey(hashing.getHash())) {
									exists = true;
									String hash = hashing.getHash();
									Bukkit.getScheduler().scheduleSyncDelayedTask(getInstance(), new Runnable() {

										@Override
										public void run() {
											player.getInventory().addItem(new ItemStack(Material.MAP, 1, maps.get(hash)));
											player.sendMessage("Here you go :D");
											return;
										}
										
									});
								}
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							if(exists)
								return;
							
							
							ItemStack is = new ItemStack(Material.MAP);
							MapView map = Bukkit.createMap(player.getWorld());
							
							maps.put(new ImageHashing(image).getHash(), map.getId());
							
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
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}
					
				});
			}
			return true;
		}
		return false;
	}
	
}
