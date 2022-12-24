package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
            sender.sendMessage("\2477There are currently \2478" + ProxyServer.getInstance().getPlayers().size() + "\2477 out of \2478200\2477 currently online,");
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                Collection<ProxiedPlayer> serverPlayers = server.getPlayers();

                StringBuilder message = new StringBuilder();
                message.append(ChatColor.GRAY).append("[");
                message.append(ChatColor.DARK_GRAY).append(server.getName());
                message.append(ChatColor.GRAY).append("] ").append("(");
                message.append(ChatColor.DARK_GRAY).append(serverPlayers.size());
                message.append(ChatColor.GRAY).append("): ").append(ChatColor.DARK_GRAY);

                List<String> players = new ArrayList<>();
                for (ProxiedPlayer player : serverPlayers) {
                    players.add(player.getDisplayName());
                }
                Collections.sort(players, String.CASE_INSENSITIVE_ORDER);

                if (!players.isEmpty()) {
                    for (String player : players) {
                        message.append(ChatColor.DARK_GRAY).append(player).append(ChatColor.GRAY).append(", ").append(ChatColor.DARK_GRAY);
                    }
                }

                sender.sendMessage(message.substring(0, message.length() - 2));
            }
        } catch (Exception e) {
            sender.sendMessage("How.");
            e.printStackTrace();
        }

    }
}
