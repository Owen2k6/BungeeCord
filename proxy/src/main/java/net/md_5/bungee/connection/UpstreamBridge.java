package net.md_5.bungee.connection;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.EntityMap;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.packet.Packet0KeepAlive;
import net.md_5.bungee.packet.Packet3Chat;
import net.md_5.bungee.packet.PacketHandler;
import net.md_5.bungee.BungeeCord;

@RequiredArgsConstructor
public class UpstreamBridge extends PacketHandler
{

    private final ProxyServer bungee;
    private final UserConnection con;

    @Override
    public void exception(Throwable t) throws Exception
    {
        con.disconnect( Util.exception( t ) );
    }

    @Override
    public void disconnected(Channel channel) throws Exception
    {
        // We lost connection to the client
        PlayerDisconnectEvent event = new PlayerDisconnectEvent( con );
        bungee.getPluginManager().callEvent( event );
        bungee.getTabListHandler().onDisconnect( con );
        BungeeCord.getInstance().removeConnection( con );

        if ( con.getServer() != null )
        {
            con.getServer().disconnect( "Quitting" );
        }
    }

    @Override
    public void handle(byte[] buf) throws Exception
    {
        EntityMap.rewrite( buf, con.clientEntityId, con.serverEntityId );
        if ( con.getServer() != null )
        {
            con.getServer().getCh().write( buf );
        }
    }

    @Override
    public void handle(Packet0KeepAlive alive) throws Exception
    {
        if ( alive.id == con.trackingPingId )
        {
            int newPing = (int) ( System.currentTimeMillis() - con.pingTime );
            bungee.getTabListHandler().onPingChange( con, newPing );
            con.setPing( newPing );
        }
    }

    @Override
    public void handle(Packet3Chat chat) throws Exception
    {
        if ( chat.message.startsWith( BungeeCord.getInstance().config.getMessagingSecret() ) )
        {
            throw new CancelSendSignal();
        }
        ChatEvent chatEvent = bungee.getPluginManager().callEvent( new ChatEvent( con, con.getServer(), chat.message ) );
        
        if ( chatEvent.isCancelled() )
        {
            throw new CancelSendSignal();
        }
        
        if ( chatEvent.isCommand() )
        {
            if ( bungee.getPluginManager().dispatchCommand( con, chatEvent.getMessage().substring( 1 ) ) )
            {
                throw new CancelSendSignal();
            }
        }
        
        if ( !chatEvent.getMessage().equals(chat.message) ) {
            con.getServer().getCh().write( new Packet3Chat( chatEvent.getMessage() ) );
            throw new CancelSendSignal();
        }
    }

    @Override
    public String toString()
    {
        return "[" + con.getName() + "] -> UpstreamBridge";
    }
}
