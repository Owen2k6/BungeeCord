package net.md_5.bungee.command;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

import java.util.Collection;
import java.util.Collections;

/**
 * Command sender representing the proxy console.
 */
public class ConsoleCommandSender implements CommandSender
{

    @Getter
    private static final ConsoleCommandSender instance = new ConsoleCommandSender();

    private ConsoleCommandSender()
    {
    }

    @Override
    public void sendMessage(String message)
    {
        System.out.println( ChatColor.stripColor( message ) );
    }

    @Override
    public void sendMessages(String... messages)
    {
        for ( String message : messages )
        {
            sendMessage( message );
        }
    }

    @Override
    public String getName()
    {
        return "CONSOLE";
    }

    @Override
    public Collection<String> getGroups()
    {
        return Collections.emptySet();
    }

    @Override
    public void addGroups(String... groups)
    {
        //throw new UnsupportedOperationException( "Console may not have groups" );
        System.out.println("Console may not have groups");
    }

    @Override
    public void removeGroups(String... groups)
    {
        //throw new UnsupportedOperationException( "Console may not have groups" );
        System.out.println("Console may not have groups");
    }

    @Override
    public boolean hasPermission(String permission)
    {
        return true;
    }

    @Override
    public void setPermission(String permission, boolean value)
    {
        //throw new UnsupportedOperationException( "Console has all permissions" );
        System.out.println("Console has all permissions");
    }
}
