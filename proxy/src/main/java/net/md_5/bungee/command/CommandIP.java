package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandIP extends Command {

    public CommandIP() {
        super("ip", "bungeecord.command.ip");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Please follow this command by a user name");
                return;
            }
            ProxiedPlayer user = ProxyServer.getInstance().getPlayer(args[0]);
            if (user == null) {
                sender.sendMessage(ChatColor.RED + "That user is not online");
            } else {
                sender.sendMessage(ChatColor.BLUE + "IP of " + args[0] + " is " + user.getAddress());
            }
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}
