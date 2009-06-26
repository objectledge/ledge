package org.objectledge.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class DiffUtil
{
    private DiffUtil()
    {
        // cannot be instantiated, intended for static method use
    }
    
    public Sequence<Element<String>> diff(String left, String right, Splitter blockSplitter)
    {
        List<String> leftBlocks = blockSplitter.split(left);
        List<String> rightBlocks = blockSplitter.split(right);
        return diff(leftBlocks, rightBlocks);
    }

    public static Sequence<Sequence<Element<String>>> diff(String left, String right, Splitter blockSplitter, Splitter elementSplitter)
    {
        List<String> leftBlocks = blockSplitter.split(left);
        List<String> rightBlocks = blockSplitter.split(right);
        Sequence<Element<String>> tier1diff = diff(leftBlocks, rightBlocks);
        Sequence<Sequence<Element<String>>> tier1sequence = new Sequence<Sequence<Element<String>>>(tier1diff.getState());
        for (Element<String> diffBlock : tier1diff)
        {
            List<String> tier2left = diffBlock.getLeft() != null ? elementSplitter.split(diffBlock
                .getLeft()) : new ArrayList<String>();
            List<String> tier2right = diffBlock.getRight() != null ? elementSplitter
                .split(diffBlock.getRight()) : new ArrayList<String>();
            Sequence<Element<String>> tier2diff = diff(tier2left, tier2right);
            tier1sequence.add(tier2diff);
        }
        return tier1sequence;
    }
   
    public static <T> Sequence<Element<T>> diff(List<T> left, List<T> right)
    {
        Sequence<Element<T>> sequence;

        Iterator<T> lefItr = left.iterator();
        Iterator<T> rightItr = right.iterator();
        Iterator<Difference> diffItr = new Diff<T>(left, right).diff().iterator();
       
        int leftIdx = 0;
        int rightIdx = 0;

        if(diffItr.hasNext())
        {
            sequence = new Sequence<Element<T>>(left.isEmpty() ? State.ADDED
                : right.isEmpty() ? State.DELETED : State.CHANGED);
            Difference diff = diffItr.next();
            while(rightItr.hasNext() || lefItr.hasNext())
            {
                // equals
                if(leftIdx < diff.getAddedStart() && rightIdx < diff.getDeletedStart()
                    || leftIdx > diff.getAddedEnd() && rightIdx > diff.getDeletedEnd())
                {
                    sequence.add(new Element<T>(lefItr.next(), rightItr.next(), State.EQUAL));
                    rightIdx++;
                    leftIdx++;
                }
                // added
                else if(rightIdx == diff.getDeletedStart() && diff.getDeletedEnd() == -1
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new Element<T>(null, rightItr.next(), State.ADDED));
                    leftIdx++;
                }
                // removed
                else if(leftIdx == diff.getAddedStart() && diff.getAddedEnd() == -1
                    && rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd())
                {
                    sequence.add(new Element<T>(lefItr.next(), null, State.DELETED));
                    rightIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new Element<T>(lefItr.next(), rightItr.next(), State.CHANGED));
                    rightIdx++;
                    leftIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && (leftIdx < diff.getAddedStart() || leftIdx > diff.getAddedEnd())
                    && -1 != diff.getAddedEnd())
                {
                    sequence.add(new Element<T>(lefItr.next(), null, State.CHANGED));
                    rightIdx++;
                }
                // modified
                else if((rightIdx < diff.getDeletedStart() || rightIdx > diff.getDeletedEnd())
                    && -1 != diff.getDeletedEnd() && leftIdx >= diff.getAddedStart()
                    && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new Element<T>(null, rightItr.next(), State.CHANGED));
                    leftIdx++;
                }

                if(leftIdx > diff.getAddedEnd() && rightIdx > diff.getDeletedEnd()
                    && diffItr.hasNext())
                {
                    diff = diffItr.next();
                }
            }
        }
        else
        {
            sequence = new Sequence<Element<T>>(State.EQUAL);
            while(rightItr.hasNext() && lefItr.hasNext())
            {
                sequence.add(new Element<T>(lefItr.next(), rightItr.next(), State.EQUAL));
            }
        }
        return sequence;
    }
}
