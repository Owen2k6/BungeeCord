package net.md_5.bungee.packet;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Packet9Respawn extends DefinedPacket
{

    public static final Packet9Respawn DIM1_SWITCH = new Packet9Respawn( (byte) 1 );
    public static final Packet9Respawn DIM2_SWITCH = new Packet9Respawn( (byte) -1 );
    public int dimension;
    public byte difficulty;
    public byte gameMode;
    public short worldHeight;
    public String levelType;

    public Packet9Respawn(byte dimension)
    {
        super( 0x09 );
        writeByte( dimension );
        this.dimension = dimension;
    }

    Packet9Respawn(byte[] buf)
    {
        super( 0x09, buf );
        this.dimension = readByte();
    }

    @Override
    public void handle(PacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
