package fr.mrkold.plotplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

public class PP2Commands implements CommandExecutor {
	
	public MainClass plugin;

	public PP2Commands(MainClass mainClass) {
		this.plugin = mainClass;
	}

	private String a0;
	private String a1;
	private String a2;

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		// On verifie que la commande est /pp ou /plotplus
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) {
			// On verifie que la commande est entree par un joueur
		    if(sender instanceof Player) {
		    	Player p = (Player) sender;
		    	if(p.hasPermission("plotplus.use") || p.hasPermission("plotplus.admin")){
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
			            
			    		// Argument reload et permission ok
			            if (a0.equalsIgnoreCase("reload")) {
			            	if(p.hasPermission("plotplus.admin")){
			            		plugin.ReloadPlugin(p);
				            	return true;
			            	}
			            	else{
			            		p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".nopermission")));
			            	}
						}
			            
			            // Detecte si l'on est bien sur un plotworld
			            if(PlotManager.getMap(p) == null) {
				            p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplotworld")));
				        } else {
				            String id = PlotManager.getPlotId(p);
				            String world = p.getWorld().getName();
				            
				         // id == "" : Ce n'est pas un plot
				            if(id.equals("")) {
				                p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplot")));
				            } else {
				            	// recuperer les infos du plot
		    		            Plot plot = PlotManager.getPlotById(p);
		    		            String joueur = p.getName();
		    		            
		    		            // Si plot != null alors le plot appartient a quelqu'un
		    		            if(plot != null) {
		    		            	String plotid = plot.id;
		    		            	// Detecte si le plot appartient au joueur
		    		            	if ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin")){
		    		            		
		    		            		// Commande time
			    		            	if (a0.equalsIgnoreCase("time")) {
			    		            		if(args.length != 1){
			    		            			if(a1.equalsIgnoreCase("reset")){
				    		            			plugin.fonctions.resetTime(p, world, plotid);
				    		            		}
				    		            		if(a1.equalsIgnoreCase("set")){
				    		            			if(a2 != null){
				    		            				plugin.fonctions.setHeure(p, world, plotid, a2);
				    		            			}
				    		            			else{
				    		            				p.sendMessage(ChatColor.RED + "/pp time set <ticks>");
				    		            			}
				    		            		}
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + "/pp time set <ticks> | /pp time reset");
			    		            		}
										}
			    		            	
			    		            	// Commande weather
			    		            	if (a0.equalsIgnoreCase("weather")) {
			    		            		if(args.length != 1){
			    		            			if(a1.equalsIgnoreCase("rain")){
				    		            			plugin.fonctions.setRain(p, world, plotid);
				    		            		}
				    		            		if(a1.equalsIgnoreCase("reset")){
				    		            			plugin.fonctions.resetWeather(p, world, plotid);
				    		            		}
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + "/pp weather rain | /pp weather reset");
			    		            		}
										}
			    		            	
			    		            	// Commande rate
			    		            	if ((a0.equalsIgnoreCase("rate")) && p.hasPermission("plotplus.rate.set")){	
			    		            		// Si la notation est activ�e
			    		            		if(plugin.notationenabled){
			    		            			plugin.fonctions.ratePlot(p, world, plotid, a1);
												return true;
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
			    		            			return false;
			    		            		}
										}
			    		            	
			    		            	// Commande unrate
			    		            	if ((a0.equalsIgnoreCase("unrate")) && p.hasPermission("plotplus.rate.set")){
			    		            		// Si la notation est activ�e
			    		            		if(plugin.notationenabled){
			    		            			plugin.fonctions.unratePlot(p, world, plotid);
				    		            		return true;
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
			    		            			return false;
			    		            		}
										}
			    		            	
			    		            	// Commande info
			    		            	if (a0.equalsIgnoreCase("info")){
			    		            		plugin.fonctions.plotInfo(p, world, plotid);
			    		            		return true;
										}
			    		            	
			    		            	// Commande like
			    		            	if (a0.equalsIgnoreCase("like")){
			    		            		plugin.fonctions.plotLike(p, world, plotid);
			    		            		return true;
										}
		    		            	}
		    		            	
		    		            	// Si le plot n'appartient pas au joueur
		    		            	else {
			    		                p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notyourplot")));
			    		            }
		    		            }
		    		            
		    		            // Si le plot n'appartient � personne
		    		            else{
		    		            	p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notowned")));
		    		            }
				            }
				        }
			    	}
		    	}
		    	
		    	// Si le joueur n'a pas la permission
		    	else {
		    		p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".nopermission")));
		    	}
		    }
		}
		return false;
	}

}