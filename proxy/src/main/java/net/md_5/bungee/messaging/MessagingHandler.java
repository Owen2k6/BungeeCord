package net.md_5.bungee.messaging;

public class MessagingHandler {

    public static MessageData handleServerSpecialMessage( String secretKey, String message )
    {
        if ( !message.startsWith(secretKey) )
        {
            return null;
        }
        
        message = message.substring( secretKey.length() + 1 );

        String[] data = message.split( "#" );

        return new MessageData( data );
    }
}
