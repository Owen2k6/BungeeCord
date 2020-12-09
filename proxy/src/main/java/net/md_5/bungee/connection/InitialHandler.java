package net.md_5.bungee.connection;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.packet.Packet2Handshake;
import net.md_5.bungee.packet.PacketFEPing;
import net.md_5.bungee.packet.PacketFFKick;
import net.md_5.bungee.packet.PacketHandler;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class InitialHandler extends PacketHandler implements PendingConnection
{

    private final ProxyServer bungee;
    private Channel ch;
    @Getter
    private final ListenerInfo listener;
    private Packet2Handshake handshake;
    private State thisState = State.HANDSHAKE;

    private enum State
    {
        HANDSHAKE, FINISHED;
    }

    @Override
    public void connected(Channel channel) throws Exception
    {
        this.ch = channel;
    }

    @Override
    public void exception(Throwable t) throws Exception
    {
        disconnect( ChatColor.RED + Util.exception( t ) );
    }

    @Override
    public void handle(PacketFEPing ping) throws Exception
    {
        ServerPing response = new ServerPing( bungee.getProtocolVersion(), bungee.getGameVersion(),
                listener.getMotd(), bungee.getPlayers().size(), listener.getMaxPlayers() );

        response = bungee.getPluginManager().callEvent( new ProxyPingEvent( this, response ) ).getResponse();

        String kickMessage = ChatColor.DARK_BLUE
                + "\00" + response.getProtocolVersion()
                + "\00" + response.getGameVersion()
                + "\00" + response.getMotd()
                + "\00" + response.getCurrentPlayers()
                + "\00" + response.getMaxPlayers();
        disconnect( kickMessage );
    }

    @Override
    public void handle(Packet2Handshake handshake) throws Exception
    {
        Preconditions.checkState( thisState == State.HANDSHAKE, "Not expecting HANDSHAKE" );
        Preconditions.checkArgument( handshake.username.length() <= 16, "Cannot have username longer than 16 characters" );

        int limit = BungeeCord.getInstance().config.getPlayerLimit();
        Preconditions.checkState( limit <= 0 || bungee.getPlayers().size() < limit, "Server is full!" );

        this.handshake = handshake;

        UserConnection userCon = new UserConnection( (BungeeCord) bungee, ch, this, handshake );
        bungee.getPluginManager().callEvent( new PostLoginEvent( userCon ) );

        ch.pipeline().get( HandlerBoss.class ).setHandler( new UpstreamBridge( bungee, userCon ) );

        ServerInfo server = bungee.getReconnectHandler().getServer( userCon );
        userCon.connect( server, true );

        thisState = State.FINISHED;
        throw new CancelSendSignal();
    }

    @Override
    public synchronized void disconnect(String reason)
    {
        if ( ch.isActive() )
        {
            ch.write( new PacketFFKick( reason ) );
            ch.close();
        }
    }

    @Override
    public String getName()
    {
        return ( handshake == null ) ? null : handshake.username;
    }

    @Override
    public byte getVersion()
    {
        return ( handshake == null ) ? -1 : BungeeCord.PROTOCOL_VERSION;
    }

    @Override
    public InetSocketAddress getVirtualHost()
    {
        return ( handshake == null ) ? null : new InetSocketAddress( "localhost", 1234 );
    }

    @Override
    public InetSocketAddress getAddress()
    {
        return (InetSocketAddress) ch.remoteAddress();
    }

    @Override
    public String toString()
    {
        return "[" + ( ( getName() != null ) ? getName() : getAddress() ) + "] <-> InitialHandler";
    }
}
