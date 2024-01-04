package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.connection.CancelSendSignal;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.packet.*;

import java.util.Queue;

@RequiredArgsConstructor
public class ServerConnector extends PacketHandler
{

    private final ProxyServer bungee;
    private Channel ch;
    private final UserConnection user;
    private final ServerInfo target;
    private State thisState = State.LOGIN;
    private final static int MAGIC_HEADER = 2;
    private String userIP = null;

    private enum State
    {
        LOGIN, FINISHED
    }

    @Override
    public void connected(Channel channel) throws Exception
    {
        this.ch = channel;
        channel.write( user.handshake );
        // IP Forwarding
        boolean flag = BungeeCord.getInstance().config.isIpForwarding();
        long address = flag ? Util.serializeAddress((userIP == null) ? user.getAddress().getAddress().getHostAddress() : userIP) : 0;
        byte header = (byte) (flag ? MAGIC_HEADER : 0);
        // end
        channel.write(new Packet1Login(BungeeCord.PROTOCOL_VERSION,  user.handshake.username, address, header));
    }

    @Override
    public void handle(Packet99ForwardIP packet) throws Exception
    {
        userIP = packet.forwardedIP;
        System.out.println("User IP was successfully forwarded: " + userIP);
    }

    @Override
    public void handle(Packet1Login login) throws Exception
    {
        Preconditions.checkState( thisState == State.LOGIN, "Not exepcting LOGIN" );

        ServerConnection server = new ServerConnection( ch, target, login );
        ServerConnectedEvent event = new ServerConnectedEvent( user, server );
        bungee.getPluginManager().callEvent( event );

        // TODO: Race conditions with many connects
        Queue<DefinedPacket> packetQueue = ( (BungeeServerInfo) target ).getPacketQueue();
        while ( !packetQueue.isEmpty() )
        {
            ch.write( packetQueue.poll() );
        }
        
        ServerInfo from = ( user.getServer() == null ) ? null : user.getServer().getInfo();

        synchronized ( user.getSwitchMutex() )
        {
            if ( user.getServer() == null )
            {
                // Once again, first connection
                user.clientEntityId = login.entityId;
                user.serverEntityId = login.entityId;

                Packet1Login modLogin = new Packet1Login(
                        login.entityId,
                        login.username,
                        login.seed,
                        login.dimension
                );
                user.ch.write( modLogin );
            } else
            {
                byte oppositeDimension = (byte) ( login.dimension >= 0 ? -1 : 0 );

                user.serverEntityId = login.entityId;

                user.ch.write( new Packet9Respawn( oppositeDimension ) );
                user.ch.write( new Packet9Respawn( login.dimension) );

                // Remove from old servers
                user.getServer().setObsolete( true );
                user.getServer().disconnect( "Quitting" );
            }

            // TODO: Fix this?
            if ( !user.ch.isActive() )
            {
                server.disconnect( "Quitting" );
                // Silly server admins see stack trace and die
                bungee.getLogger().warning( "No client connected for pending server!" );
                return;
            }

            // Add to new server
            // TODO: Move this to the connected() method of DownstreamBridge
            target.addPlayer( user );

            user.setServer( server );
            ch.pipeline().get( HandlerBoss.class ).setHandler( new DownstreamBridge( bungee, user, server ) );
        }
        
        bungee.getPluginManager().callEvent( new ServerSwitchEvent( user, from ) );

        thisState = State.FINISHED;

        throw new CancelSendSignal();
    }

    @Override
    public void handle(PacketFFKick kick) throws Exception
    {
        String message = ChatColor.RED + target.getName() + ": " + kick.message;
        if ( user.getServer() == null )
        {
            user.disconnect( message );
        } else
        {
            user.sendMessage( message );
        }
    }

    @Override
    public String toString()
    {
        return "[" + user.getName() + "] <-> ServerConnector [" + target.getName() + "]";
    }
}
