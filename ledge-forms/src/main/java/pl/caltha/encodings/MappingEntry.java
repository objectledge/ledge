package pl.caltha.encodings;


public class MappingEntry
{
    public MappingEntry(short unicodeCode, String name)
    {
        this.unicodeCode = unicodeCode;
        this.name = name;
    }

    public MappingEntry(int unicodeCode, String name)
    {
        this((short)unicodeCode, name);
    }

    public MappingEntry(short unicodeCode, char code)
    {
        this.unicodeCode = unicodeCode;
        this.name = new String(new char[] {code} );
    }

    public MappingEntry(int unicodeCode, byte code)
    {
        this((short)unicodeCode, (char)code);
    }

    public MappingEntry(int unicodeCode, int code)
    {
        this((short)unicodeCode, (char)code);
    }
    
    public short unicodeCode;
    public String name;

    public String getName()
    {
        return name;
    }
}

