package org.objectledge.cache.impl;

import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;

public class LongKeyChainedHashMapImpl
    extends LongKeyMapImpl
{
    protected LongKeyMap newLongKeyMap(int capacity, double loadFactor, double growthFactor)
    {
        return new LongKeyOpenHashMap(capacity, loadFactor, growthFactor);
    }
}
