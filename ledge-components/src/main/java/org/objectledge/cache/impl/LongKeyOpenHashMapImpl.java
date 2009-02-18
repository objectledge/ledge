package org.objectledge.cache.impl;

import bak.pcj.map.LongKeyChainedHashMap;
import bak.pcj.map.LongKeyMap;

public class LongKeyOpenHashMapImpl
    extends LongKeyMapImpl
{
    protected LongKeyMap newLongKeyMap(int capacity, double loadFactor, double growthFactor)
    {
        return new LongKeyChainedHashMap(capacity, loadFactor, growthFactor);
    }
}
