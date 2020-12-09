package net.md_5.bungee.protocol.netty;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

abstract class Instruction
{

    static final Instruction BYTE = new Jump( 1 );
    static final Instruction DOUBLE = new Jump( 8 );
    static final Instruction FLOAT = new Jump( 4 );
    static final Instruction INT = new Jump( 4 );
    static final Instruction INT_3 = new IntHeader( new Jump( 3 ) );
    static final Instruction INT_BYTE = new IntHeader( BYTE );
    static final Instruction ITEM = new Item();
    static final Instruction LONG = new Jump( 8 );
    static final Instruction METADATA = new MetaData();
    static final Instruction OPTIONAL_MOTION = new OptionalMotion();
    static final Instruction SHORT = new Jump( 2 );
    static final Instruction SHORT_BYTE = new ShortHeader( BYTE );
    static final Instruction SHORT_ITEM = new ShortHeader( ITEM );
    static final Instruction STRING = new ShortHeader( new Jump( 2 ) );
    static final Instruction USHORT_BYTE = new UnsignedShortByte();
    static final Instruction BLOCK_CHANGE_ARRAY = new BlockChangeArray();

    abstract void read(ByteBuf in) throws IOException;
}
