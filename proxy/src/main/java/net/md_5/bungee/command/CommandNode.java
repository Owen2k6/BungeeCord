package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command to list and switch a player between available servers.
 */
public class CommandNode extends Command {

    public CommandNode() {
        super("node", "bungeecord.command.server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            sender.sendMessage(ChatColor.GOLD + "You are connected to GoplexMC " + BungeeCord.nodename + "!");
            sender.sendMessage(ChatColor.GOLD + "Your node's maintainer is " + BungeeCord.nodemnt + ".");
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}
