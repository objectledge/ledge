package org.objectledge.modules.rest.upload;

import java.util.Comparator;

public interface ItemInfo
{
    int getId();

    String getName();

    long getSize();

    public static final Comparator<ItemInfo> BY_ID = new Comparator<ItemInfo>()
        {
            @Override
            public int compare(ItemInfo i1, ItemInfo i2)
            {
                return i1.getId() - i2.getId();
            }
        };
}
