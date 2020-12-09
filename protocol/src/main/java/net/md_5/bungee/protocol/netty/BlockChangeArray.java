package net.md_5.bungee.protocol.netty;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

class BlockChangeArray extends Instruction {

    @Override
    void read(ByteBuf in) throws IOException
    {
        short size = in.readShort();
        for ( short s = 0; s < size; s++ )
        {
            in.readShort();
            in.readByte();
            in.readByte();
        }
    }
}
