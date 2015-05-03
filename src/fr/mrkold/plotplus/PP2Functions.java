package fr.mrkold.plotplus;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.worldcretornica.plotme.Plot;
import com.worldcretornica.plotme.PlotManager;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PP2Functions {
	
	public MainClass plugin;

	public PP2Functions(MainClass mainClass) {
		this.plugin = mainClass;
	}
	
	// If on plot
	public boolean onPlot(Player p, String id) {
		if(!id.equalsIgnoreCase("")){
				return true;
		}
		return false;
	}
	
	// save Plot Config
	public void savePlotConfig() {
		try {
			plugin.plots.save(plugin.myFile);
		} catch (IOException e) {
			// catch block
			e.printStackTrace();
		}
	}
	
	// reset Time
	public void resetTime(Player p, String world, String plotid) {
		String weather = (plugin.plots.getString("plots." + world + "." + plotid + ".rain"));
		plugin.plots.set("plots." + world + "." + plotid + ".time", null);
		if(weather == null){
			plugin.plots.set("plots." + world + "." + plotid, null);
		}
		savePlotConfig();
		p.sendMessage(ChatColor.GREEN + (plugin.getConfig().getString("messages."+ plugin.lang +".resettime")));
	}
	
	// set Rain
	public void setRain(Player p, String world, String plotid) {
		plugin.plots.set("plots." + world + "." + plotid + ".rain", true);
		savePlotConfig();
		p.sendMessage(ChatColor.GREEN + (plugin.getConfig().getString("messages."+ plugin.lang +".setrain")));
	}
	
	// reset Weather
	public void resetWeather(Player p, String world, String plotid) {
		String time = (plugin.plots.getString("plots." + world + "." + plotid + ".time"));
		plugin.plots.set("plots." + world + "." + plotid + ".rain", null);
		if(time == null){
			plugin.plots.set("plots." + world + "." + plotid, null);
		}
		savePlotConfig();
		p.sendMessage(ChatColor.GREEN + (plugin.getConfig().getString("messages."+ plugin.lang +".resetweather")));
	}
	
	// set Heure
	public boolean setHeure(Player p, String world, String plotid, String a0) {
		try {
			int setheure = -1;
			setheure =  Integer.parseInt(a0);
			if ((setheure < 0)||(setheure > 24000)){
				p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".badtime")));
				return false;
			}
			plugin.plots.set("plots." + world + "." + plotid + ".time", setheure);
			savePlotConfig();
		    p.sendMessage(ChatColor.GREEN + (plugin.getConfig().getString("messages."+ plugin.lang +".settime")) + ": " + setheure + "ticks");
		    return true;
		}
		catch (NumberFormatException nfe) {
			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".badargument")));
			return false;
		}
	}
	
	// clear Plot Infos
	public void clearPlotInfos(Player p, String plotid) {
		String world = p.getWorld().getName();
		plugin.plots.set("plots." + world + "." + plotid, null);
		savePlotConfig();
	}
	
	// get Rank
		public String getRank(Player p, String owner) {
			String rank = "";
			if(plugin.PEXOK){
				try{
					rank = PermissionsEx.getUser(owner).getPrefix();
					if(rank != ""){
						return rank;
					}
				} catch (NullArgumentException e){
					
				}
			}
			return rank;
		}
		
		// Test if note is between 0 and 20
		private int testNote(Player p, String a2){
			int note;
			try {
				note =  Integer.parseInt(a2);
			}
			catch (NumberFormatException nfe) {
				p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".badargument")));
				return 0;
			}
			if ((note < 0)||(note > 20)){
				p.sendMessage((plugin.getConfig().getString("messages."+ plugin.lang +".badnotation")));
				return 0;
			}
			return note;
		}
		
		// rate Plot
		public boolean ratePlot(Player p, String world, String plotid, String a1) {
			if(a1 == null){
				p.sendMessage((plugin.getConfig().getString("messages."+ plugin.lang +".badnotation")));
				return false;
			}
			else{
				int note = plugin.fonctions.testNote(p, a1);
					plugin.plots.set("plots." + world + "." + plotid + ".rate", note);
					savePlotConfig();
					p.sendMessage(ChatColor.GREEN + (plugin.getConfig().getString("messages."+ plugin.lang +".noteset")) + " (" + note + "/20)");
					return true;
				}
		}
		
		// unrate Plot
		public void unratePlot(Player p, String world, String plotid) {
			plugin.plots.set("plots." + world + "." + plotid + ".rate", null);
			savePlotConfig();
			p.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages."+ plugin.lang +".notereset"));
		}
		
		// plot Info
		public void plotInfo(Player p, String world, String plotid) {
			String plotownerm = plugin.getConfig().getString("messages."+ plugin.lang +".plotowner");
			Plot plot = PlotManager.getPlotById(p);
			String owner = plot.owner;
			owner = plotownerm + " " + ChatColor.AQUA + owner;
			int Inblike = plugin.plots.getInt("plots." + world + "." + plotid + ".like");
			String nblike =  ChatColor.AQUA + "" + Inblike + ChatColor.GREEN + " like(s)";
			String mnote;

			String Snote = (plugin.plots.getString("plots." + world + "." + plotid + ".rate"));
			
			try {
				int Inote =  Integer.parseInt(Snote);
				
				mnote = ChatColor.GREEN+"Note: " + ChatColor.AQUA + Inote + "/20";

			}
			catch (NumberFormatException nfe) {
					mnote = plugin.getConfig().getString("messages."+ plugin.lang +".notrated");
			}
			owner = ChatColor.translateAlternateColorCodes('&', owner);
			mnote = ChatColor.translateAlternateColorCodes('&', mnote);
			
			p.sendMessage(ChatColor.GREEN + "------------------------------");
			p.sendMessage(ChatColor.GREEN + owner);
			p.sendMessage(ChatColor.GREEN + mnote);
			p.sendMessage(ChatColor.GREEN + nblike);
			p.sendMessage(ChatColor.GREEN + "------------------------------");
		}
		
		// like Plot
		public void plotLike(Player p, String world, String plotid) {
			int nblike = plugin.plots.getInt("plots." + world + "." + plotid + ".like");
			List<String> likers = plugin.plots.getStringList("plots." + world + "." + plotid + ".likers");
			if(likers.contains(p.getName())){
				p.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages."+ plugin.lang +".alreadylike"));
				return;
			}
			likers.add(p.getName());
			nblike++;
			plugin.plots.set("plots." + world + "." + plotid + ".likers", likers);
			plugin.plots.set("plots." + world + "." + plotid + ".like", nblike);
			savePlotConfig();
			p.getWorld().playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.HEART, 1);
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
			p.sendMessage(ChatColor.GREEN + plugin.getConfig().getString("messages."+ plugin.lang +".likeplot"));
		}
		
		// NoPermission
		public void noPerm(Player p){
			p.sendMessage(ChatColor.RED + (plugin.getConfig().getString("messages."+ plugin.lang +".nopermission")));
		}

}
