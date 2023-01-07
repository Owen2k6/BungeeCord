package net.md_5.bungee.command;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * Command to terminate the proxy instance. May only be used by the console.
 */
public class CommandEnd extends Command {

    public CommandEnd() {
        super("end", "bungeecord.command.end");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            BungeeCord.getInstance().stop();
        } catch (Exception e) {
            sender.sendMessage("Sorry, an error occurred performing this command.");
            sender.sendMessage("Im not sure how this failed. but its probs your fault.");
            sender.sendMessage("Works fine on my pc ;-;");
            e.printStackTrace();
        }
    }
}
