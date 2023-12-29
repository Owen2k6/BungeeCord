package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.*;

/**
 * Command to list all players connected to the proxy.
 */
public class CommandList extends Command {

    public CommandList() {
        super("list", "bungeecord.command.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            int i = 0;
            sender.sendMessage("\2477There are currently \2478" + ProxyServer.getInstance().getPlayers().size() + "\2477 out of \2478200\2477 currently online,");
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                Collection<ProxiedPlayer> serverPlayers = server.getPlayers();

                if(Objects.equals(server.getName(), "HA") || Objects.equals(server.getName(), "TS")){
                    continue;
                }

                StringBuilder message = new StringBuilder();
                message.append(ChatColor.GRAY).append("[");
                message.append(ChatColor.DARK_GRAY).append(server.getName());
                message.append(ChatColor.GRAY).append("] ").append("(");
                message.append(ChatColor.DARK_GRAY).append(serverPlayers.size());
                message.append(ChatColor.GRAY).append(") ").append(ChatColor.DARK_GRAY);
                message.append(ChatColor.WHITE).append("===============").append(ChatColor.DARK_GRAY);
                sender.sendMessage(message.toString());
                message = new StringBuilder();

                List<String> players = new ArrayList<>();
                for (ProxiedPlayer player : serverPlayers) {
                    players.add(player.getDisplayName());
                }
                players.sort(String.CASE_INSENSITIVE_ORDER);

                if (!players.isEmpty()) {
                    for (String player : players) {
                        i++;
                        if(i == (long) players.size()){
                            message.append(ChatColor.DARK_GRAY).append(player).append(ChatColor.GRAY);
                        } else{
                            message.append(ChatColor.DARK_GRAY).append(player).append(ChatColor.GRAY).append(", ").append(ChatColor.DARK_GRAY);
                        }
                        if (i==4){
                            sender.sendMessage(message.toString());
                            message = new StringBuilder();
                            i=0;
                        }
                    }

                }

                sender.sendMessage(message.toString());
            }
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }

    }
}
