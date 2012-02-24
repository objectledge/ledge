package org.objectledge.statistics;

public class RoundRobinAverage
{
    private final long[] samples;
    
    private int sampleCount = 0;
    
    private int position;

    private final int capacity;
    
    public RoundRobinAverage(int capacity)
    {
        this.capacity = capacity;
        samples = new long[capacity];
    }
    
    public synchronized void addSample(long sample)
    {
        samples[position] = sample;
        position = (position + 1) % capacity;
        if(sampleCount < capacity)
        {
            sampleCount++;
        }
    }
    
    public synchronized double getAverage()
    {
        if(sampleCount > 0)
        {
            double sum = 0d;
            for(int i = 0; i < sampleCount; i++)
            {
                sum += samples[i];
            }
            return sum / sampleCount;            
        }
        else
        {
            return 0;
        }
    }
}
