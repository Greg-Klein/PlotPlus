package fr.mrkold.plotplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;


public class PPCommands implements CommandExecutor {
	
	private PlotPlusPlugin plugin;
	public PPCommands(PlotPlusPlugin plugin){
		this.plugin = plugin;
	}
	
	private String a0;
	private String a1;
	private String a2;

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		// On vérifie que la commande est /pp ou /plotplus
		if((label.equalsIgnoreCase("pp"))||(label.equalsIgnoreCase("plotplus"))) {
			// On vérifie que la commande est entrée par un joueur
		    if(sender instanceof Player) {
		    	Player p = (Player) sender;
		    	if(p.hasPermission("plotplus.use")){
		    		// Si la commande n'a pas d'argument on affiche l'aide
		    		if (args.length == 0) {
						p.sendMessage(ChatColor.AQUA + "------------------------------");
						p.sendMessage(ChatColor.AQUA + plugin.nomplugin + " v" + plugin.version + " by MrKold");
						p.sendMessage(ChatColor.AQUA + "------------------------------");
						p.sendMessage(ChatColor.GREEN + "");
						p.sendMessage(ChatColor.AQUA + "Syntax:");
						p.sendMessage(ChatColor.GREEN + "Time: " + ChatColor.AQUA + "/pp ticks|resettime");
						p.sendMessage(ChatColor.GREEN + "Weather: " + ChatColor.AQUA + "/pp rain|resetweather");
						if(p.hasPermission("plotplus.rate.set")){
							p.sendMessage(ChatColor.AQUA + "------------------------------");
							p.sendMessage(ChatColor.GREEN + "Rate plot: " + ChatColor.AQUA + "/pp rate XX (Where XX is an integer between 0 and 20)");
							p.sendMessage(ChatColor.GREEN + "Unrate: " + ChatColor.AQUA + "/pp unrate");
						}
						if(p.hasPermission("plotplus.rate.view")){
							p.sendMessage(ChatColor.GREEN + "Get infos about plot: " + ChatColor.AQUA + "/pp info");
						}
						if(p.hasPermission("plotplus.admin")){
							p.sendMessage(ChatColor.AQUA + "------------------------------");
							p.sendMessage(ChatColor.GREEN + "Reload: " + ChatColor.AQUA + "/pp reload");
						}
						return true;
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
			            if ((a0.equalsIgnoreCase("reload")) && (p.hasPermission("plotplus.admin"))) {
			            	plugin.ReloadPlugin(p);
				    		return true;
						}
			            
			            // Détecte si l'on est bien sur un plotworld
			            if(PlotManager.getMap(p) == null) {
				            p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplotworld")));
				        } else {
				            String id = PlotManager.getPlotId(p);
				            String world = p.getWorld().getName();
				            
				         // id == "" : Ce n'est pas un plot
				            if(id.equals("")) {
				                p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".noplot")));
				            } else {
				            	// récupérer les infos du plot
		    		            Plot plot = PlotManager.getPlotById(p);
		    		            String joueur = p.getName();
		    		            
		    		            // Si plot != null alors le plot appartient à quelqu'un
		    		            if(plot != null) {
		    		            	String plotid = plot.id;
		    		            	// Détecte si le plot appartient au joueur
		    		            	if ((plot.owner.equalsIgnoreCase(joueur)) || p.hasPermission("plotplus.admin")){
		    		            		
		    		            		// Commande resettime
			    		            	if (a0.equalsIgnoreCase("resettime")) {	
			    		            		plugin.functions.resetTime(p, world, plotid);
											return true;
										}
			    		            	
			    		            	// Commande rain
			    		            	if (a0.equalsIgnoreCase("rain")) {
			    		            		p.sendMessage("DEBUG");
			    		            		plugin.functions.setRain(p, world, plotid);
			    		            		return true;
										}
			    		            	
			    		            	// Commande resetweather
			    		            	if (a0.equalsIgnoreCase("resetweather")) {									// Si l'argument est resetweather
			    		            		plugin.functions.resetWeather(p, world, plotid);
											return true;
										}
			    		            	
			    		            	// Commande rate
			    		            	if ((a0.equalsIgnoreCase("rate")) && p.hasPermission("plotplus.rate.set")){	
			    		            		// Si la notation est activée
			    		            		if(plugin.notationenabled){
			    		            			plugin.functions.ratePlot(p, world, plotid, a1, a2);
												return true;
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
			    		            			return false;
			    		            		}
										}
			    		            	
			    		            	// Commande unrate
			    		            	if ((a0.equalsIgnoreCase("unrate")) && p.hasPermission("plotplus.rate.set")){
			    		            		// Si la notation est activée
			    		            		if(plugin.notationenabled){
			    		            			plugin.functions.unratePlot(p, world, plotid);
				    		            		return true;
			    		            		}
			    		            		else{
			    		            			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notationdisabled")));
			    		            			return false;
			    		            		}
										}
			    		            	
			    		            	// Commande info
			    		            	if (a0.equalsIgnoreCase("info")){
			    		            		plugin.functions.plotInfo(p, world, plotid);
			    		            		return true;
										}
			    		            	
			    		            	// Si aucune des commandes plus haut on définit l'heure grace à l'argument passé
			    		            	else {
			    		            		plugin.functions.setHeure(p, world, plotid, a0);
										}
		    		            	}
		    		            	
		    		            	// Si le plot n'appartient pas au joueur
		    		            	else {
			    		                p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notyourplot")));
			    		                return false;
			    		            }
		    		            }
		    		            
		    		            // Si le plot n'appartient à personne
		    		            else{
		    		            	p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".notowned")));
		    		            	return false;
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
