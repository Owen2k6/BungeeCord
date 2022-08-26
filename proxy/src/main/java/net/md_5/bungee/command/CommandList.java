package net.md_5.bungee.command;

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
public class CommandList extends Command
{

    public CommandList()
    {
        super( "glist", "bungeecord.command.list" );
    }

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        for ( ServerInfo server : ProxyServer.getInstance().getServers().values() )
        {
            Collection<ProxiedPlayer> serverPlayers = server.getPlayers();

            StringBuilder message = new StringBuilder();
            message.append( ChatColor.GREEN ).append( "[" );
            message.append( server.getName() );
            message.append( "] " ).append( ChatColor.YELLOW ).append( "(" );
            message.append( serverPlayers.size() );
            message.append( "): " ).append( ChatColor.RESET );

            List<String> players = new ArrayList<>();
            for ( ProxiedPlayer player : serverPlayers )
            {
                players.add( player.getDisplayName() );
            }
            Collections.sort( players, String.CASE_INSENSITIVE_ORDER );

            if ( !players.isEmpty() )
            {
                for ( String player : players )
                {
                    message.append( player ).append( ChatColor.RESET ).append( ", " );
                }
            }

            sender.sendMessage( message.substring( 0, message.length() - 2 ) );
        }

        sender.sendMessage( "Total players online: " + ProxyServer.getInstance().getPlayers().size() );
    }
}
