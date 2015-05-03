package fr.mrkold.plotplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMeCoreManager;
import com.worldcretornica.plotme_core.bukkit.api.BukkitPlayer;
import com.worldcretornica.plotme_core.bukkit.api.BukkitWorld;


public class PP2Commands implements CommandExecutor {
	
	public MainClass plugin;

	public PP2Commands(MainClass mainClass) {
		this.plugin = mainClass;
	}

	private String a0;
	private String a1;
	private String a2;
	private final PlotMeCoreManager plotManager = PlotMeCoreManager.getInstance();

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		// On verifie que la commande est /pp ou /plotplus
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) {
			// On verifie que la commande est entree par un joueur
		    if(sender instanceof Player) {
		    	Player p = (Player) sender;
		    	if(!p.hasPermission("plotplus.use")){
		    		plugin.fonctions.noPerm(p);
		    		return false;
		    	}
		    	// Si la commande n'a pas d'argument on affiche l'aide
		    	if (args.length == 0) {
					p.sendMessage(ChatColor.AQUA + "------------------------------");
					p.sendMessage(ChatColor.AQUA + plugin.nomplugin + " v" + plugin.version + " by MrKold");
					p.sendMessage(ChatColor.AQUA + "'/help plotplus' for help");
					p.sendMessage(ChatColor.AQUA + "------------------------------");
				}
		    	// Si la commande a un argument
			    else {
			    	a0 = args[0];
			    	// 2 arguments
			    	if (args.length == 2) {
			    		a1 = args[1];
			    	}
			    	// 3 arguments
			    	if (args.length == 3) {
			    		a1 = args[1];
			    		a2 = args[2];
			    	}
			            
			    	// Commande reload
			        if (a0.equalsIgnoreCase("reload")) {
			           	if(p.hasPermission("plotplus.admin") || p.isOp()){
			           		plugin.ReloadPlugin(p);
			           		return true;
			           	}
			           	else{
			           		plugin.fonctions.noPerm(p);
			           		return false;
			        	}
					}
			            
			        // Detecte si l'on est bien sur un plotworld
			        BukkitWorld iworld = new BukkitWorld(p.getWorld());
			        BukkitPlayer iplayer = new BukkitPlayer(p);
			        if(plotManager.getMap(iworld) == null)
				        p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplotworld")));
				    else {
				        String id = plotManager.getPlotId(iplayer);
				        String world = p.getWorld().getName();
				        
				        if (a0.equalsIgnoreCase("bm")){
				        	if(args.length != 1){
								String name;
								name = a2;
								if(a1.equalsIgnoreCase("delete")){
	    		           			plugin.fonctions.bmDelete(p, world, name);
	    		           			return true;
								}
	    		           		if(a1.equalsIgnoreCase("list")){
	    		           			plugin.fonctions.bmList(p, world);
	    		           			return true;
								}
	    		           		if(a1.equalsIgnoreCase("tp")){
	    		           			plugin.fonctions.bmTP(p, world, name);
	    		           			return true;
								}
    		           		}
    		           		else{
    		           			p.sendMessage(ChatColor.RED + "/pp bm save <name> | /pp bm delete <name> | /pp bm list | /pp bm tp <name>");
    		           			return false;
    		           		}
				        }
				            
				        // id == "" : Ce n'est pas un plot
				        if(id.equals(""))
				            p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplot")));
				        else {
				        // recuperer les infos du plot
				        	Plot plot = plotManager.getPlotById(iplayer);
				        	String joueur = p.getName();
		    		            
		    		        // Si plot != null alors le plot appartient a quelqu'un
		    		        if(plot != null) {
		    		          	String plotid = plot.getId();
		    		            	
		    		           	// Commande time
		    		           	if (a0.equalsIgnoreCase("time")) {
		    		           		// Detecte si le plot appartient au joueur
		    		           		if ((plot.getOwner().equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin") || p.isOp()){
		    		           			if(args.length != 1){
			    		           			if(a1.equalsIgnoreCase("reset"))
				    		           			plugin.fonctions.resetTime(p, world, plotid);
				    		           		if(a1.equalsIgnoreCase("set")){
				    		           			if(a2 != null)
				    		           				plugin.fonctions.setHeure(p, world, plotid, a2);
				    		           			else
				    		           				p.sendMessage(ChatColor.RED + "/pp time set <ticks>");
				    		           		}
			    		           		}
			    		           		else
			    		           			p.sendMessage(ChatColor.RED + "/pp time set <ticks> | /pp time reset");
			    		           	}
		    		           		else
		    		           			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notyourplot")));
								}
		    		            	
		    		            // Commande weather
		    		            if (a0.equalsIgnoreCase("weather")) {
		    		            	if ((plot.getOwner().equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin") || p.isOp()){
		    		            		if(args.length != 1){
			    		           			if(a1.equalsIgnoreCase("rain"))
				    		           			plugin.fonctions.setRain(p, world, plotid);
				    		           		if(a1.equalsIgnoreCase("reset"))
				    		           			plugin.fonctions.resetWeather(p, world, plotid);
			    		           		}
			    		           		else
			    		           			p.sendMessage(ChatColor.RED + "/pp weather rain | /pp weather reset");
		    		            	}
		    		            	else
		    		            		p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notyourplot")));
								}
		    		            	
		    		            // Commande rate
		    		            if (a0.equalsIgnoreCase("rate")){	
		    		            	if(p.hasPermission("plotplus.rate") || p.hasPermission("plotplus.admin")){
		    		            		// Si la notation est activee
			    		            	if(plugin.notationenabled)
			    		            		plugin.fonctions.ratePlot(p, world, plotid, a1);
			    		            	else
			    		            		p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
		    		            	}
		    		            	else
		    		            		plugin.fonctions.noPerm(p);
								}
		    		            	
		    		            // Commande unrate
		    		            if (a0.equalsIgnoreCase("unrate")){
		    		            	if(p.hasPermission("plotplus.rate") || p.hasPermission("plotplus.admin")){
		    		            		// Si la notation est activee
			    		           		if(plugin.notationenabled)
			    		           			plugin.fonctions.unratePlot(p, world, plotid);
			    		           		else
			    		           			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
		    		            	}
		    		            	else
		    		            		plugin.fonctions.noPerm(p);
								}
		    		            // Commande info
		    		            if (a0.equalsIgnoreCase("info"))
		    		            	plugin.fonctions.plotInfo(p, world, plotid);
		    		            	
		    		            // Commande like
		    		            if (a0.equalsIgnoreCase("like"))
		    		            	plugin.fonctions.plotLike(p, world, plotid);
		    		            
		    		            // Commande Bookmark
		    		            if (a0.equalsIgnoreCase("bm")){
		    		            	if(args.length != 1){
										String name;
										name = a2;
										if(a1.equalsIgnoreCase("save"))
			    		           			plugin.fonctions.bmSave(p, world, plotid, name);
		    		           		}
		    		            }
		    		            	
		    		        }
		    		            
		    		        // Si le plot n'appartient a personne
		    		        else
		    		        	p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notowned")));
				        }
				    }
			    }
		    }
		}
		return false;
	}

}