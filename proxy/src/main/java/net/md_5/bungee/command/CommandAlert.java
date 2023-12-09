package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandAlert extends Command {

    public CommandAlert() {
        super("plalert", "bungeecord.command.alert");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "You must supply a message.");
            } else if (args.length > 109)
            {
                sender.sendMessage(ChatColor.RED + "Message must be below 109 chars.");
            }
            else {
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.RED);
                builder.append("[").append(ChatColor.WHITE).append("Global").append(ChatColor.RED).append("] ").append(ChatColor.LIGHT_PURPLE);

                for (String s : args) {
                    builder.append(ChatColor.translateAlternateColorCodes('&', s));
                    builder.append(" ");
                }

                String message = builder.substring(0, builder.length() - 1);
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    player.sendMessage(message);
                }
                ProxyServer.getInstance().getConsole().sendMessage(message);
            }
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}
