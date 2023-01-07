package net.md_5.bungee.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class CommandHelp extends Command {

    public CommandHelp() {
        super("bhelp", "bungeecord.command.help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "==== Global Command list.");
            sender.sendMessage(ChatColor.GOLD + "/server {server}  " + ChatColor.BLUE + " Switch Servers");
            sender.sendMessage(ChatColor.GOLD + "/list  " + ChatColor.BLUE + " See who is online");
            if (sender.hasPermission("global.staff.basic")) {
                sender.sendMessage(ChatColor.YELLOW + "/discon {user} {reason}  " + ChatColor.BLUE + " Disconnect someone");
                sender.sendMessage(ChatColor.YELLOW + "/ratelim {user} {reason}  " + ChatColor.BLUE + " Enable Rate Limiting");
                sender.sendMessage(ChatColor.YELLOW + "/jban {user} {reason} {length}  " + ChatColor.BLUE + " Johny bans");
            }
            if (sender.hasPermission("global.staff.medium")) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/ip {user}  " + ChatColor.BLUE + " Self explanitory.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/send {user} {server}  " + ChatColor.BLUE + " Send someone somewhere.");
            }
            if (sender.hasPermission("global.staff.administrator")) {
                sender.sendMessage(ChatColor.RED + "/greload  " + ChatColor.BLUE + " Reload BungeeCord");
                sender.sendMessage(ChatColor.RED + "/end  " + ChatColor.BLUE + " close the proxy to restart");
                sender.sendMessage(ChatColor.RED + "/alert  " + ChatColor.BLUE + " send a global message.");
            }

        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            e.printStackTrace();
        }
    }
}