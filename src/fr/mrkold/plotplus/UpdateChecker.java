package fr.mrkold.plotplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;

public class UpdateChecker {

    
    public static List<String> readURL(String url)
    
    {
            try {
                    URL site = new URL(url);
                    URLConnection urlC = site.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlC.getInputStream()));
     
                    List<String> lines = new ArrayList<String>();
                    String line;
                    while((line = in.readLine()) != null)
                    {
                            lines.add(line);
                    }
     
                    in.close();
     
                    return lines;
            } catch(MalformedURLException e) {
                    e.printStackTrace();
            } catch(IOException e) {
                    e.printStackTrace();
            }
     
            return null;
    }
    
    public static String checkVersion(PluginDescriptionFile pdfFile){
    	
    	VersionNumber currentVersion = new VersionNumber(pdfFile.getVersion());
    	List<String> versionURL = readURL("http://www.mrkold.fr/pluginsversions/plotplus2.txt");
    	String lVersion = versionURL.get(0) + "." + versionURL.get(1) + "." + versionURL.get(2);
    	VersionNumber latestVersion = new VersionNumber(lVersion);
    	int cv0 = currentVersion.version[0];
    	int cv1 = currentVersion.version[1];
    	int cv2 = currentVersion.version[2];
    	int lv0 = latestVersion.version[0];
    	int lv1 = latestVersion.version[1];
    	int lv2 = latestVersion.version[2];
    	String noupd = "";
    	String upd = ChatColor.AQUA + "[" + pdfFile.getName() + "] " + ChatColor.GREEN + "Nouvelle version disponible! (v"+ lVersion + ")";
    	 
    	if(cv0 < lv0){
    	    return upd;
    	}
    	if(cv0 == lv0){
    		if(cv1 < lv1){
    			return upd;
    		}
    		if(cv1 == lv1){
    			if(cv2 < lv2){
    				return upd;
    			}
    		}
    	}
    	
    	return noupd;

    }

}
