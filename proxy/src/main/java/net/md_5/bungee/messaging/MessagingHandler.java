package net.md_5.bungee.messaging;

public class MessagingHandler {

    public static MessageData handleServerSpecialMessage( String secretKey, String message )
    {
        if ( !message.startsWith(secretKey) )
        {
            return null;
        }

        String[] parts = message.split( "#" );
        if ( parts.length != 3 )
        {
            return null;
        }

        String username = parts[1];
        String server = parts[2];

        return new MessageData( username, server );
    }
}
