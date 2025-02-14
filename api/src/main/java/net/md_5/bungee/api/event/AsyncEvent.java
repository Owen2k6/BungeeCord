package net.md_5.bungee.api.event;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents an event which depends on the result of asynchronous operations.
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AsyncEvent extends Event
{

    private final Callback done;
    private final Set<Plugin> intents = Collections.newSetFromMap( new ConcurrentHashMap<Plugin, Boolean>() );
    private final AtomicBoolean fired = new AtomicBoolean();
    private final AtomicInteger latch = new AtomicInteger();

    @Override
    @SuppressWarnings("unchecked")
    public void postCall()
    {
        fired.set( true );
        if ( latch.get() == 0 )
        {
            done.done( this, null );
        }
    }

    /**
     * Register an intent that this plugin will continue to perform work on a
     * background task, and wishes to let the event proceed once the registered
     * background task has completed.
     *
     * @param plugin the plugin registering this intent
     */
    public void registerIntent(Plugin plugin)
    {
        Preconditions.checkState( !fired.get(), "Event %s has already been fired", this );
        Preconditions.checkState( !intents.contains( plugin ), "Plugin %s already registered intent for event %s", plugin, this );

        intents.add( plugin );
    }

    /**
     * Notifies this event that this plugin has done all its required processing
     * and wishes to let the event proceed.
     *
     * @param plugin a plugin which has an intent registered for this evemt
     */
    @SuppressWarnings("unchecked")
    public void completeIntent(Plugin plugin)
    {
        Preconditions.checkState( intents.contains( plugin ), "Plugin %s has not registered intent for event %s", plugin, this );
        intents.remove( plugin );
        if ( latch.decrementAndGet() == 0 )
        {
            done.done( this, null );
        }
    }
}
