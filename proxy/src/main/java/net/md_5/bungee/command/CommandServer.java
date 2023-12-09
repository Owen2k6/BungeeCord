package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Map;

/**
 * Command to list and switch a player between available servers.
 */
public class CommandServer extends Command {

    public CommandServer() {
        super("server", "bungeecord.command.server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            sender.sendMessage(ChatColor.GOLD + "You are connected to GoplexMC "+ BungeeCord.nodename +"!");
            sender.sendMessage(ChatColor.BLUE + "This means that you can't switch to OSM.");
            sender.sendMessage(ChatColor.BLUE + "Connect to gmc.owen2k6.com to play on the main service.");
            sender.sendMessage(ChatColor.BLUE + "Connect to os-mc.net to play on osm on the main service.");
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}
