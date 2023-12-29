package net.md_5.bungee.command;

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

    private ServerInfo findServer(String name)
    {
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
        for (ServerInfo server : servers.values())
            if (server.getName().equalsIgnoreCase(name)) return server;
        return null;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }
            ProxiedPlayer player = (ProxiedPlayer) sender;
            Map<String, ServerInfo> servers = ProxyServer.getInstance().getServers();
            if (args.length == 0) {
                StringBuilder serverList = new StringBuilder();
                for (ServerInfo server : servers.values()) {
                    if (server.canAccess(player)) {
                        serverList.append(server.getName());
                        serverList.append(", ");
                    }
                }
                if (serverList.length() != 0) {
                    serverList.setLength(serverList.length() - 2);
                }
                player.sendMessage(ChatColor.GOLD + "Available Servers: " +ChatColor.GREEN+ serverList.toString());
            } else {
                ServerInfo server = findServer(args[0]);
                if (server == null) {
                    player.sendMessage(ChatColor.RED + "Unknown Server! Run /server for a list of available servers.");
                } else if (!server.canAccess(player)) {
                    player.sendMessage(ChatColor.RED + "You aren't allowed to join this server!");
                } else {
                    player.connect(server);
                }
            }
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}
