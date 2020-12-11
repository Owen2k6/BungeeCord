package net.md_5.bungee.connection;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.EntityMap;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.messaging.MessageData;
import net.md_5.bungee.messaging.MessagingHandler;
import net.md_5.bungee.packet.Packet0KeepAlive;
import net.md_5.bungee.packet.Packet3Chat;
import net.md_5.bungee.packet.PacketFFKick;
import net.md_5.bungee.packet.PacketHandler;

@RequiredArgsConstructor
public class DownstreamBridge extends PacketHandler
{

    private final ProxyServer bungee;
    private final UserConnection con;
    private final ServerConnection server;

    @Override
    public void exception(Throwable t) throws Exception
    {
        ServerInfo def = bungee.getServerInfo( con.getPendingConnection().getListener().getDefaultServer() );
        if ( server.getInfo() != def )
        {
            con.connectNow( def );
            con.sendMessage( ChatColor.RED + "The server you were previously on went down, you have been connected to the lobby" );
        } else
        {
            con.disconnect( Util.exception( t ) );
        }
    }

    @Override
    public void disconnected(Channel channel) throws Exception
    {
        // We lost connection to the server
        server.getInfo().removePlayer( con );
        bungee.getReconnectHandler().setServer( con );

        if ( !server.isObsolete() )
        {
            con.disconnect( "[Proxy] Lost connection to server D:" );
        }
    }

    @Override
    public void handle(byte[] buf) throws Exception
    {
        EntityMap.rewrite( buf, con.serverEntityId, con.clientEntityId );
        con.ch.write( buf );
    }

    @Override
    public void handle(Packet0KeepAlive alive) throws Exception
    {
        con.trackingPingId = alive.id;
    }

    @Override
    public void handle(Packet3Chat chat) throws Exception
    {
        ChatEvent chatEvent = new ChatEvent( con.getServer(), con, chat.message );
        bungee.getPluginManager().callEvent( chatEvent );

        if ( chatEvent.isCancelled() )
        {
            throw new CancelSendSignal();
        }

        MessageData data = MessagingHandler.handleServerSpecialMessage( BungeeCord.getInstance().config.getMessagingSecret(), chat.message );
        if ( data != null )
        {
            if ( !con.handshake.username.equals( data.getUsername() ) )
            {
                return;
            }

            ProxiedPlayer player = bungee.getPlayer( data.getUsername() );
            if ( player != null )
            {
                ServerInfo targetServer = bungee.getServerInfo( data.getTargetServer() );
                if ( targetServer != null )
                {
                    player.connect( targetServer );
                }
            }
            throw new CancelSendSignal();
        }
    }

    @Override
    public void handle(PacketFFKick kick) throws Exception
    {
        ServerInfo def = bungee.getServerInfo( con.getPendingConnection().getListener().getDefaultServer() );
        if ( server.getInfo() == def )
        {
            def = null;
        }
        ServerKickEvent event = bungee.getPluginManager().callEvent( new ServerKickEvent( con, kick.message, def ) );
        if ( event.isCancelled() && event.getCancelServer() != null )
        {
            con.connectNow( event.getCancelServer() );
        } else
        {
            con.disconnect( "[Kicked] " + event.getKickReason() );
        }
        throw new CancelSendSignal();
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] <-> DownstreamBridge <-> [" + server.getInfo().getName() + "]";
    }
}
