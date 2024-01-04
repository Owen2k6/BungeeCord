package net.md_5.bungee.packet;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Packet99ForwardIP extends DefinedPacket
{
    public String forwardedIP;

    public Packet99ForwardIP(String forwardedIP)
    {
        super( 0x99 );
        writeUTF( forwardedIP );
        this.forwardedIP = forwardedIP;
    }

    Packet99ForwardIP(byte[] buf)
    {
        super( 0x99, buf );
        this.forwardedIP = readUTF();
    }

    @Override
    public void handle(PacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
