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
    
    public static Sequence<DetailElement<String>> diff(String left, String right, Splitter blockSplitter)
    {
        List<String> leftBlocks = blockSplitter.split(left);
        List<String> rightBlocks = blockSplitter.split(right);
        return diff(leftBlocks, rightBlocks);
    }

    public static Sequence<Sequence<DetailElement<String>>> diff(String left, String right, Splitter blockSplitter, Splitter elementSplitter)
    {
        List<String> leftBlocks = blockSplitter.split(left);
        List<String> rightBlocks = blockSplitter.split(right);
        Sequence<DetailElement<String>> tier1diff = diff(leftBlocks, rightBlocks);
        Sequence<Sequence<DetailElement<String>>> tier1sequence = new Sequence<Sequence<DetailElement<String>>>(tier1diff.getState());
        for (DetailElement<String> diffBlock : tier1diff)
        {
            List<String> tier2left = diffBlock.getLeft() != null ? elementSplitter.split(diffBlock
                .getLeft()) : new ArrayList<String>();
            List<String> tier2right = diffBlock.getRight() != null ? elementSplitter
                .split(diffBlock.getRight()) : new ArrayList<String>();
            Sequence<DetailElement<String>> tier2diff = diff(tier2left, tier2right);
            tier1sequence.add(tier2diff);
        }
        return tier1sequence;
    }
   
    public static <T> Sequence<DetailElement<T>> diff(List<T> left, List<T> right)
    {
        Sequence<DetailElement<T>> sequence;

        Iterator<T> lefItr = left.iterator();
        Iterator<T> rightItr = right.iterator();
        Iterator<Difference> diffItr = new Diff<T>(left, right).diff().iterator();
       
        int leftIdx = 0;
        int rightIdx = 0;

        if(diffItr.hasNext())
        {
            sequence = new Sequence<DetailElement<T>>(left.isEmpty() ? Element.State.ADDED
                : right.isEmpty() ? Element.State.DELETED : Element.State.CHANGED);
            Difference diff = diffItr.next();
            while(rightItr.hasNext() || lefItr.hasNext())
            {
                // equals
                if(leftIdx < diff.getAddedStart() && rightIdx < diff.getDeletedStart()
                    || leftIdx > diff.getAddedEnd() && rightIdx > diff.getDeletedEnd())
                {
                    sequence.add(new DetailElement<T>(lefItr.next(), rightItr.next(), Element.State.EQUAL));
                    rightIdx++;
                    leftIdx++;
                }
                // added
                else if(rightIdx == diff.getDeletedStart() && diff.getDeletedEnd() == -1
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new DetailElement<T>(null, rightItr.next(), Element.State.ADDED));
                    leftIdx++;
                }
                // removed
                else if(leftIdx == diff.getAddedStart() && diff.getAddedEnd() == -1
                    && rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd())
                {
                    sequence.add(new DetailElement<T>(lefItr.next(), null, Element.State.DELETED));
                    rightIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new DetailElement<T>(lefItr.next(), rightItr.next(), Element.State.CHANGED));
                    rightIdx++;
                    leftIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && (leftIdx < diff.getAddedStart() || leftIdx > diff.getAddedEnd())
                    && -1 != diff.getAddedEnd())
                {
                    sequence.add(new DetailElement<T>(lefItr.next(), null, Element.State.CHANGED));
                    rightIdx++;
                }
                // modified
                else if((rightIdx < diff.getDeletedStart() || rightIdx > diff.getDeletedEnd())
                    && -1 != diff.getDeletedEnd() && leftIdx >= diff.getAddedStart()
                    && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(new DetailElement<T>(null, rightItr.next(), Element.State.CHANGED));
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
            sequence = new Sequence<DetailElement<T>>(Element.State.EQUAL);
            while(rightItr.hasNext() && lefItr.hasNext())
            {
                sequence.add(new DetailElement<T>(lefItr.next(), rightItr.next(), Element.State.EQUAL));
            }
        }
        return sequence;
    }
}
