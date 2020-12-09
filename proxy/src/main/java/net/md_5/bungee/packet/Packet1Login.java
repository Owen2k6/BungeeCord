package net.md_5.bungee.packet;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Packet1Login extends DefinedPacket
{

    public int entityId;
    public String username;
    public long seed;
    public byte dimension;

    public Packet1Login(int entityId, String username, long seed, byte dimension)
    {
        super( 0x01 );
        writeInt( entityId );
        writeUTF( username );
        writeLong( seed );
        writeByte( dimension );

        this.entityId = entityId;
        this.username = username;
        this.seed = seed;
        this.dimension = dimension;
    }

    Packet1Login(byte[] buf) {
        super(0x01, buf);
        this.entityId = readInt();
        this.username = readUTF();
        this.seed = readLong();
        this.dimension = readByte();
    }

    @Override
    public void handle(PacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}