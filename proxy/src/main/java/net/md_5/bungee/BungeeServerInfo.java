package net.md_5.bungee;

import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.connection.PingHandler;
import net.md_5.bungee.netty.HandlerBoss;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.packet.DefinedPacket;

import java.net.InetSocketAddress;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BungeeServerInfo extends ServerInfo
{

    @Getter
    private final Queue<DefinedPacket> packetQueue = new ConcurrentLinkedQueue<>();

    public BungeeServerInfo(String name, InetSocketAddress address, boolean restricted)
    {
        super( name, address, restricted );
    }

    @Override
    public void sendData(String channel, byte[] data)
    {
        Preconditions.checkNotNull( channel, "channel" );
        Preconditions.checkNotNull( data, "data" );

        synchronized ( packetQueue )
        {
            Server server = ( ProxyServer.getInstance().getPlayers().isEmpty() ) ? null : ProxyServer.getInstance().getPlayers().iterator().next().getServer();
            if ( server != null )
            {
                server.sendData( channel, data );
            } else
            {
                //packetQueue.add( new PacketFAPluginMessage( channel, data ) );
            }
        }
    }

    @Override
    public void ping(final Callback<ServerPing> callback)
    {
        new Bootstrap()
                .channel( NioSocketChannel.class )
                .group( BungeeCord.getInstance().eventLoops )
                .handler( PipelineUtils.BASE )
                .option( ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000 ) // TODO: Configurable
                .remoteAddress( getAddress() )
                .connect()
                .addListener( new ChannelFutureListener()
        {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception
            {
                if ( future.isSuccess() )
                {
                   future.channel().pipeline().get( HandlerBoss.class ).setHandler( new PingHandler( BungeeServerInfo.this, callback ) );
                } else
                {
                    callback.done( null, future.cause() );
                }
            }
        } );
    }
}
