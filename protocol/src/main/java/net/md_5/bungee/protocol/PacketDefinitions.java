package net.md_5.bungee.protocol;

import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.BLOCK_CHANGE_ARRAY;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.BYTE;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.DOUBLE;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.FLOAT;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.INT;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.INT_3;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.INT_BYTE;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.ITEM;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.LONG;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.METADATA;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.OPTIONAL_MOTION;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.SHORT;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.SHORT_BYTE;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.SHORT_ITEM;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.STRING;
import static net.md_5.bungee.protocol.PacketDefinitions.OpCode.BYTE_BYTE;

public class PacketDefinitions
{
    public static final OpCode[][] opCodes = new OpCode[ 256 ][];

    public enum OpCode
    {
        BYTE, DOUBLE, FLOAT, INT, INT_3, INT_BYTE, ITEM, LONG, METADATA,
        OPTIONAL_MOTION, SHORT, SHORT_BYTE, SHORT_ITEM, STRING, BLOCK_CHANGE_ARRAY, BYTE_BYTE
    }

    static {
        opCodes[0x00] = new OpCode[]{

        };
        opCodes[0x01] = new OpCode[]{
                INT, STRING, LONG, BYTE
        };
        opCodes[0x02] = new OpCode[]{
                STRING
        };
        opCodes[0x03] = new OpCode[]{
                STRING
        };
        opCodes[0x04] = new OpCode[]{
                LONG
        };
        opCodes[0x05] = new OpCode[]{
                INT, SHORT, SHORT, SHORT
        };
        opCodes[0x06] = new OpCode[]{
                INT, INT, INT
        };
        opCodes[0x07] = new OpCode[]{
                INT, INT, BYTE
        };
        opCodes[0x08] = new OpCode[]{
                SHORT
        };
        opCodes[0x09] = new OpCode[]{
                BYTE
        };
        opCodes[0x0A] = new OpCode[]{
                BYTE
        };
        opCodes[0x0B] = new OpCode[]{
                DOUBLE, DOUBLE, DOUBLE, DOUBLE, BYTE
        };
        opCodes[0x0C] = new OpCode[]{
                FLOAT, FLOAT, BYTE
        };
        opCodes[0x0D] = new OpCode[]{
                DOUBLE, DOUBLE, DOUBLE, DOUBLE, FLOAT, FLOAT, BYTE
        };
        opCodes[0x0E] = new OpCode[]{
                BYTE, INT, BYTE, INT, BYTE
        };
        opCodes[0x0F] = new OpCode[]{
                INT, BYTE, INT, BYTE, ITEM
        };
        opCodes[0x10] = new OpCode[]{
                SHORT
        };
        opCodes[0x11] = new OpCode[]{
                INT, BYTE, INT, BYTE, INT
        };
        opCodes[0x12] = new OpCode[]{
                INT, BYTE
        };
        opCodes[0x13] = new OpCode[]{
                INT, BYTE
        };
        opCodes[0x14] = new OpCode[]{
                INT, STRING, INT, INT, INT, BYTE, BYTE, SHORT
        };
        opCodes[0x15] = new OpCode[]{
                INT, SHORT, BYTE, SHORT, INT, INT, INT, BYTE, BYTE, BYTE
        };
        opCodes[0x16] = new OpCode[]{
                INT, INT
        };
        opCodes[0x17] = new OpCode[]{
                INT, BYTE, INT, INT, INT, OPTIONAL_MOTION
        };
        opCodes[0x18] = new OpCode[]{
                INT, BYTE, INT, INT, INT, BYTE, BYTE, METADATA
        };
        opCodes[0x19] = new OpCode[]{
                INT, STRING, INT, INT, INT, INT
        };
        opCodes[0x1B] = new OpCode[]{
                FLOAT, FLOAT, FLOAT, FLOAT, BYTE, BYTE
        };
        opCodes[0x1C] = new OpCode[]{
                INT, SHORT, SHORT, SHORT
        };
        opCodes[0x1D] = new OpCode[]{
                INT
        };
        opCodes[0x1E] = new OpCode[]{
                INT
        };
        opCodes[0x1F] = new OpCode[]{
                INT, BYTE, BYTE, BYTE
        };
        opCodes[0x20] = new OpCode[]{
                INT, BYTE, BYTE
        };
        opCodes[0x21] = new OpCode[]{
                INT, BYTE, BYTE, BYTE, BYTE, BYTE
        };
        opCodes[0x22] = new OpCode[]{
                INT, INT, INT, INT, BYTE, BYTE
        };
        opCodes[0x26] = new OpCode[]{
                INT, BYTE
        };
        opCodes[0x27] = new OpCode[]{
                INT, INT
        };
        opCodes[0x28] = new OpCode[]{
                INT, METADATA
        };
        opCodes[0x32] = new OpCode[]{
                INT, INT, BYTE
        };
        opCodes[0x33] = new OpCode[]{
                INT, SHORT, INT, BYTE, BYTE, BYTE, INT_BYTE
        };
        opCodes[0x34] = new OpCode[]{
                INT, INT, BLOCK_CHANGE_ARRAY
        };
        opCodes[0x35] = new OpCode[]{
                INT, BYTE, INT, BYTE, BYTE
        };
        opCodes[0x36] = new OpCode[]{
                INT, SHORT, INT, BYTE, BYTE
        };
        opCodes[0x3C] = new OpCode[]{
                DOUBLE, DOUBLE, DOUBLE, FLOAT, INT_3
        };
        opCodes[0x3D] = new OpCode[]{
                INT, INT, BYTE, INT, INT
        };
        opCodes[0x46] = new OpCode[]{
                BYTE
        };
        opCodes[0x47] = new OpCode[]{
                INT, BYTE, INT, INT, INT
        };
        opCodes[0x64] = new OpCode[]{
                BYTE, BYTE, SHORT_BYTE, BYTE
        };
        opCodes[0x65] = new OpCode[]{
                BYTE
        };
        opCodes[0x66] = new OpCode[]{
                BYTE, SHORT, BYTE, SHORT, BYTE, ITEM
        };
        opCodes[0x67] = new OpCode[]{
                BYTE, SHORT, ITEM
        };
        opCodes[0x68] = new OpCode[]{
                BYTE, SHORT_ITEM
        };
        opCodes[0x69] = new OpCode[]{
                BYTE, SHORT, SHORT
        };
        opCodes[0x6A] = new OpCode[]{
                BYTE, SHORT, BYTE
        };
        opCodes[0x82] = new OpCode[]{
                INT, SHORT, INT, STRING, STRING, STRING, STRING
        };
        opCodes[0x83] = new OpCode[]{
                SHORT, SHORT, BYTE_BYTE
        };
        opCodes[0xC8] = new OpCode[]{
                INT, BYTE
        };
        opCodes[0xFE] = new OpCode[]{
                BYTE
        };
        opCodes[0xFF] = new OpCode[]{
                STRING
        };
    }
}
