package net.md_5.bungee;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.providers.netty.NettyAsyncHttpProvider;
import com.ning.http.client.providers.netty.NettyAsyncHttpProviderConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.TabListHandler;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import net.md_5.bungee.command.CommandAlert;
import net.md_5.bungee.command.CommandBungee;
import net.md_5.bungee.command.CommandEnd;
import net.md_5.bungee.command.CommandIP;
import net.md_5.bungee.command.CommandList;
import net.md_5.bungee.command.CommandPerms;
import net.md_5.bungee.command.CommandReload;
import net.md_5.bungee.command.CommandSend;
import net.md_5.bungee.command.CommandServer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.YamlConfig;
import net.md_5.bungee.netty.PipelineUtils;
import net.md_5.bungee.packet.DefinedPacket;
import net.md_5.bungee.scheduler.BungeeScheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.util.CaseInsensitiveMap;

/**
 * Main BungeeCord proxy class.
 */
public class BungeeCord extends ProxyServer
{

    /**
     * Server protocol version.
     */
    public static final byte PROTOCOL_VERSION = 14;
    /**
     * Server game version.
     */
    public static final String GAME_VERSION = "Beta 1.7.3";
    /**
     * Current operation state.
     */
    public volatile boolean isRunning;
    /**
     * Configuration.
     */
    public final Configuration config = new Configuration();
    /**
     * Thread pools.
     */
    //public final ScheduledExecutorService executors = new ScheduledThreadPoolExecutor( 8, new ThreadFactoryBuilder().setNameFormat( "Bungee Pool Thread #%1$d" ).build() );
    public final MultithreadEventLoopGroup eventLoops = new NioEventLoopGroup( Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder().setNameFormat( "Netty IO Thread #%1$d" ).build() );
    /**
     * locations.yml save thread.
     */
    private final Timer saveThread = new Timer( "Reconnect Saver" );
    /**
     * Server socket listener.
     */
    private Collection<Channel> listeners = new HashSet<>();
    /**
     * Fully qualified connections.
     */
    private final Map<String, UserConnection> connections = new CaseInsensitiveMap<>();
    private final ReadWriteLock connectionLock = new ReentrantReadWriteLock();
    /**
     * Tab list handler
     */
    @Getter
    @Setter
    public TabListHandler tabListHandler;
    /**
     * Plugin manager.
     */
    @Getter
    public final PluginManager pluginManager = new PluginManager( this );
    @Getter
    @Setter
    private ReconnectHandler reconnectHandler;
    @Getter
    @Setter
    private ConfigurationAdapter configurationAdapter = new YamlConfig();
    private final Collection<String> pluginChannels = new HashSet<>();
    @Getter
    private final File pluginsFolder = new File( "plugins" );
    @Getter
    private final BungeeScheduler scheduler = new BungeeScheduler();
    /*@Getter
    private final AsyncHttpClient httpClient = new AsyncHttpClient(
            new NettyAsyncHttpProvider(
            new AsyncHttpClientConfig.Builder().setAsyncHttpClientProviderConfig(
            new NettyAsyncHttpProviderConfig().addProperty( NettyAsyncHttpProviderConfig.BOSS_EXECUTOR_SERVICE, executors ) ).setExecutorService( executors ).build() ) );
*/
    
    {
        getPluginManager().registerCommand( new CommandReload() );
        getPluginManager().registerCommand( new CommandEnd() );
        getPluginManager().registerCommand( new CommandList() );
        getPluginManager().registerCommand( new CommandServer() );
        getPluginManager().registerCommand( new CommandIP() );
        getPluginManager().registerCommand( new CommandAlert() );
        getPluginManager().registerCommand( new CommandBungee() );
        getPluginManager().registerCommand( new CommandPerms() );
        getPluginManager().registerCommand( new CommandSend() );

        registerChannel( "BungeeCord" );
    }

    public static BungeeCord getInstance()
    {
        return (BungeeCord) ProxyServer.getInstance();
    }

    /**
     * Starts a new instance of BungeeCord.
     *
     * @param args command line arguments, currently none are used
     * @throws Exception when the server cannot be started
     */
    public static void main(String[] args) throws Exception
    {
        BungeeCord bungee = new BungeeCord();
        ProxyServer.setInstance( bungee );
        bungee.getLogger().info( "Enabled BungeeCord version " + bungee.getVersion() );
        bungee.start();

        BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
        while ( bungee.isRunning )
        {
            String line = br.readLine();
            if ( line != null )
            {
                boolean handled = getInstance().getPluginManager().dispatchCommand( ConsoleCommandSender.getInstance(), line );
                if ( !handled )
                {
                    System.err.println( "Command not found" );
                }
            }
        }
    }

    /**
     * Start this proxy instance by loading the configuration, plugins and
     * starting the connect thread.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception
    {
        pluginsFolder.mkdir();
        pluginManager.loadPlugins( pluginsFolder );
        config.load();
        if ( reconnectHandler == null )
        {
            reconnectHandler = new YamlReconnectHandler();
        }
        isRunning = true;

        pluginManager.enablePlugins();

        startListeners();

        saveThread.scheduleAtFixedRate( new TimerTask()
        {
            @Override
            public void run()
            {
                getReconnectHandler().save();
            }
        }, 0, TimeUnit.MINUTES.toMillis( 5 ) );
    }

    public void startListeners()
    {
        for ( ListenerInfo info : config.getListeners() )
        {
            Channel server = new ServerBootstrap()
                    .channel( NioServerSocketChannel.class )
                    .childAttr( PipelineUtils.LISTENER, info )
                    .childHandler( PipelineUtils.SERVER_CHILD )
                    .group( eventLoops )
                    .localAddress( info.getHost() )
                    .bind().channel();
            listeners.add( server );

            getLogger().info( "Listening on " + info.getHost() );
        }
    }

    public void stopListeners()
    {
        for ( Channel listener : listeners )
        {
            getLogger().log( Level.INFO, "Closing listener {0}", listener );
            try
            {
                listener.close().syncUninterruptibly();
            } catch ( ChannelException ex )
            {
                getLogger().severe( "Could not close listen thread" );
            }
        }
        listeners.clear();
    }

    @Override
    public void stop()
    {
        new Thread( "Shutdown Thread" ) {
            @Override
            public void run() {
                BungeeCord.this.isRunning = false;

                //httpClient.close();
                //executors.shutdown();

                stopListeners();
                getLogger().info( "Closing pending connections" );

                connectionLock.readLock().lock();
                try {
                    getLogger().info( "Disconnecting " + connections.size() + " connections" );
                    for ( UserConnection user : connections.values() )
                    {
                        user.disconnect( "Server restarting." );
                    }
                } finally {
                    connectionLock.readLock().unlock();
                }

                getLogger().info( "Closing IO threads" );
                eventLoops.shutdownGracefully();
                try
                {
                    eventLoops.awaitTermination( Long.MAX_VALUE, TimeUnit.NANOSECONDS );
                } catch ( InterruptedException ex )
                {
                }

                if ( reconnectHandler != null )
                {
                    getLogger().info( "Saving reconnect locations" );
                    reconnectHandler.save();
                }
                //getLogger().info( "Saving reconnect locations" );
                //reconnectHandler.save();
                saveThread.cancel();

                // TODO: Fix this shit
                getLogger().info( "Disabling plugins" );
                for ( Plugin plugin : pluginManager.getPlugins() )
                {
                    try
                    {
                        plugin.onDisable();
                    } catch ( Throwable t )
                    {
                        getLogger().severe( "Exception disabling plugin " + plugin.getDescription().getName() );
                        t.printStackTrace();
                    }
                    getScheduler().cancel( plugin );
                }

                getLogger().info( "Thank you and goodbye" );
                System.exit( 0 );
            }
        }.start();
    }

    /**
     * Broadcasts a packet to all clients that is connected to this instance.
     *
     * @param packet the packet to send
     */
    public void broadcast(DefinedPacket packet)
    {
        connectionLock.readLock().lock();
        try {
            for ( UserConnection con : connections.values() )
            {
                con.sendPacket( packet );
            }
        } finally {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public String getName()
    {
        return "BungeeCord";
    }

    @Override
    public String getVersion()
    {
        return ( BungeeCord.class.getPackage().getImplementationVersion() == null ) ? "unknown" : BungeeCord.class.getPackage().getImplementationVersion();
    }

    @Override
    public Logger getLogger()
    {
        return BungeeLogger.instance;
    }

    @Override
    @SuppressWarnings("unchecked") // TODO: Abstract more
    public Collection<ProxiedPlayer> getPlayers()
    {
        connectionLock.readLock().lock();
        try {
            return (Collection) connections.values();
        } finally {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public ProxiedPlayer getPlayer(String name)
    {
        connectionLock.readLock().lock();
        try
        {
            return connections.get( name );
        } finally
        {
            connectionLock.readLock().unlock();
        }
    }

    @Override
    public Map<String, ServerInfo> getServers()
    {
        return config.getServers();
    }

    @Override
    public ServerInfo getServerInfo(String name)
    {
        return getServers().get( name );
    }

    @Override
    @Synchronized("pluginChannels")
    public void registerChannel(String channel)
    {
        pluginChannels.add( channel );
    }

    @Override
    @Synchronized("pluginChannels")
    public void unregisterChannel(String channel)
    {
        pluginChannels.remove( channel );
    }

    @Override
    @Synchronized("pluginChannels")
    public Collection<String> getChannels()
    {
        return Collections.unmodifiableCollection( pluginChannels );
    }

    /*public PacketFAPluginMessage registerChannels()
    {
        StringBuilder sb = new StringBuilder();
        for ( String s : getChannels() )
        {
            sb.append( s );
            sb.append( '\00' );
        }
        byte[] payload = sb.substring( 0, sb.length() - 1 ).getBytes();
        return new PacketFAPluginMessage( "REGISTER", payload );
    }*/

    @Override
    public byte getProtocolVersion()
    {
        return PROTOCOL_VERSION;
    }

    @Override
    public String getGameVersion()
    {
        return GAME_VERSION;
    }

    @Override
    public ServerInfo constructServerInfo(String name, InetSocketAddress address, boolean restricted)
    {
        return new BungeeServerInfo( name, address, restricted );
    }

    @Override
    public CommandSender getConsole()
    {
        return ConsoleCommandSender.getInstance();
    }
    
    public void addConnection(UserConnection con)
    {
        connectionLock.writeLock().lock();
        try
        {
            connections.put( con.getName(), con );
        } finally
        {
            connectionLock.writeLock().unlock();
        }
    }

    public void removeConnection(UserConnection con)
    {
        connectionLock.writeLock().lock();
        try
        {
            connections.remove( con.getName() );
        } finally
        {
            connectionLock.writeLock().unlock();
        }
    }
}
