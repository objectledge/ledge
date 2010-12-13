package org.objectledge.encodings.encoders;

/**
 * Encoder for ISO8859_16 character set.
 *
 * * @author    <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version   $Id: CharEncoderISO8859_16.java,v 1.1 2004-02-02 18:59:00 zwierzem Exp $
 */
public class CharEncoderISO8859_16
         extends CharEncoder
{

    /** Index table for char significant byte. */
    private static final int[] PREFIX_INDEX = {
0x0, 0x100, 0x200, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x300, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400,
0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400, 0x400
    };

    /** Index table for char least significant byte. */
    private static final char[][] SUFFIX_INDEX = {
{ 0x0 },{ 0x1 },{ 0x2 },{ 0x3 },{ 0x4 },{ 0x5 },{ 0x6 },{ 0x7 },
{ 0x8 },{ 0x9 },{ 0xa },{ 0xb },{ 0xc },{ 0xd },{ 0xe },{ 0xf },
{ 0x10 },{ 0x11 },{ 0x12 },{ 0x13 },{ 0x14 },{ 0x15 },{ 0x16 },{ 0x17 },
{ 0x18 },{ 0x19 },{ 0x1a },{ 0x1b },{ 0x1c },{ 0x1d },{ 0x1e },{ 0x1f },
{ 0x20 },{ 0x21 },{ 0x22 },{ 0x23 },{ 0x24 },{ 0x25 },{ 0x26 },{ 0x27 },
{ 0x28 },{ 0x29 },{ 0x2a },{ 0x2b },{ 0x2c },{ 0x2d },{ 0x2e },{ 0x2f },
{ 0x30 },{ 0x31 },{ 0x32 },{ 0x33 },{ 0x34 },{ 0x35 },{ 0x36 },{ 0x37 },
{ 0x38 },{ 0x39 },{ 0x3a },{ 0x3b },{ 0x3c },{ 0x3d },{ 0x3e },{ 0x3f },
{ 0x40 },{ 0x41 },{ 0x42 },{ 0x43 },{ 0x44 },{ 0x45 },{ 0x46 },{ 0x47 },
{ 0x48 },{ 0x49 },{ 0x4a },{ 0x4b },{ 0x4c },{ 0x4d },{ 0x4e },{ 0x4f },
{ 0x50 },{ 0x51 },{ 0x52 },{ 0x53 },{ 0x54 },{ 0x55 },{ 0x56 },{ 0x57 },
{ 0x58 },{ 0x59 },{ 0x5a },{ 0x5b },{ 0x5c },{ 0x5d },{ 0x5e },{ 0x5f },
{ 0x60 },{ 0x61 },{ 0x62 },{ 0x63 },{ 0x64 },{ 0x65 },{ 0x66 },{ 0x67 },
{ 0x68 },{ 0x69 },{ 0x6a },{ 0x6b },{ 0x6c },{ 0x6d },{ 0x6e },{ 0x6f },
{ 0x70 },{ 0x71 },{ 0x72 },{ 0x73 },{ 0x74 },{ 0x75 },{ 0x76 },{ 0x77 },
{ 0x78 },{ 0x79 },{ 0x7a },{ 0x7b },{ 0x7c },{ 0x7d },{ 0x7e },{ 0x7f },
{ 0x80 },{ 0x81 },{ 0x82 },{ 0x83 },{ 0x84 },{ 0x85 },{ 0x86 },{ 0x87 },
{ 0x88 },{ 0x89 },{ 0x8a },{ 0x8b },{ 0x8c },{ 0x8d },{ 0x8e },{ 0x8f },
{ 0x90 },{ 0x91 },{ 0x92 },{ 0x93 },{ 0x94 },{ 0x95 },{ 0x96 },{ 0x97 },
{ 0x98 },{ 0x99 },{ 0x9a },{ 0x9b },{ 0x9c },{ 0x9d },{ 0x9e },{ 0x9f },
{ 0xa0 },null,null,null,null,null,null,{ 0xa7 },
null,{ 0xa9 },null,{ 0xab },null,{ 0xad },null,null,
{ 0xb0 },{ 0xb1 },null,null,null,null,{ 0xb6 },{ 0xb7 },
null,null,null,{ 0xbb },null,null,null,null,
{ 0xc0 },{ 0xc1 },{ 0xc2 },null,{ 0xc4 },null,{ 0xc6 },{ 0xc7 },
{ 0xc8 },{ 0xc9 },{ 0xca },{ 0xcb },{ 0xcc },{ 0xcd },{ 0xce },{ 0xcf },
null,null,{ 0xd2 },{ 0xd3 },{ 0xd4 },null,{ 0xd6 },null,
null,{ 0xd9 },{ 0xda },{ 0xdb },{ 0xdc },null,null,{ 0xdf },
{ 0xe0 },{ 0xe1 },{ 0xe2 },null,{ 0xe4 },null,{ 0xe6 },{ 0xe7 },
{ 0xe8 },{ 0xe9 },{ 0xea },{ 0xeb },{ 0xec },{ 0xed },{ 0xee },{ 0xef },
null,null,{ 0xf2 },{ 0xf3 },{ 0xf4 },null,{ 0xf6 },null,
null,{ 0xf9 },{ 0xfa },{ 0xfb },{ 0xfc },null,null,{ 0xff },
null,null,{ 0xc3 },{ 0xe3 },{ 0xa1 },{ 0xa2 },{ 0xc5 },{ 0xe5 },
null,null,null,null,{ 0xb2 },{ 0xb9 },null,null,
{ 0xd0 },{ 0xf0 },null,null,null,null,null,null,
{ 0xdd },{ 0xfd },null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,{ 0xa3 },{ 0xb3 },{ 0xd1 },{ 0xf1 },null,null,null,
null,null,null,null,null,null,null,null,
{ 0xd5 },{ 0xf5 },{ 0xbc },{ 0xbd },null,null,null,null,
null,null,{ 0xd7 },{ 0xf7 },null,null,null,null,
{ 0xa6 },{ 0xa8 },null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
{ 0xd8 },{ 0xf8 },null,null,null,null,null,null,
{ 0xbe },{ 0xac },{ 0xae },{ 0xaf },{ 0xbf },{ 0xb4 },{ 0xb8 },null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
{ 0xaa },{ 0xba },{ 0xde },{ 0xfe },null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,{ 0xb5 },{ 0xa5 },null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,{ 0xa4 },null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null
    };

    /** Constructor. */
    public CharEncoderISO8859_16()
    {
        this.encodingName = "ISO8859_16";
        ((CharEncoder)this).prefixIndex = PREFIX_INDEX;
        ((CharEncoder)this).suffixIndex = SUFFIX_INDEX;
    }
}
