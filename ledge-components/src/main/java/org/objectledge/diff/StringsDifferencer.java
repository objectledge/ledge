package org.objectledge.diff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

public class StringsDifferencer
{
    private List<String> leftBlocks;

    private List<String> rightBlocks;

    private Splitter elementSplitter;

    public StringsDifferencer(String oldStringsBlock, String newStringsBlock, Splitter blockSplitter)
    {
        leftBlocks = blockSplitter.split(oldStringsBlock);
        rightBlocks = blockSplitter.split(newStringsBlock);
    }

    public StringsDifferencer(String oldStringsBlock, String newStringsBlock,
        Splitter blockSplitter, Splitter elementSplitter)
    {       
        leftBlocks = blockSplitter.split(oldStringsBlock);
        rightBlocks = blockSplitter.split(newStringsBlock);
        this.elementSplitter = elementSplitter;
    }

    public StringsDifferencer(List<String> oldStringsList, List<String> newStringsList,
        Splitter elementSplitter)
    {
        leftBlocks = oldStringsList;
        rightBlocks = newStringsList;
        this.elementSplitter = elementSplitter;
    }

    public List<String> getLeftBlocks()
    {
        return leftBlocks;
    }

    public List<String> getRightBlocks()
    {
        return rightBlocks;
    }

    public Sequence<String> diffBlocks()
    {
        return diff(leftBlocks, rightBlocks);
    }

    public List<Sequence<String>> diffElements()
    {
        List<String> leftElementsList;
        List<String> rightElementsList;

        Sequence<String> diffBlocksList;
        Sequence<String> diffElementsList;
        List<Sequence<String>> diffList;

        diffList = new ArrayList<Sequence<String>>();

        diffBlocksList = diff(leftBlocks, rightBlocks);
        for (Element<String> diffBlock : diffBlocksList.getElements())
        {
            diffElementsList = new Sequence<String>();
            if(diffBlock.getState().equals(State.CHANGED))
            {
                leftElementsList = elementSplitter.split(diffBlock.getLeft());
                rightElementsList = elementSplitter.split(diffBlock.getRight());

                diffElementsList.addAll(diff(leftElementsList, rightElementsList).getElements());
                diffElementsList.setState(State.CHANGED);
                diffList.add(diffElementsList);
            }
            else
            {
                diffElementsList.add(diffBlock);
                diffElementsList.setState(diffBlock.getState());
                diffList.add(diffElementsList);
            }
        }
        return diffList;
    }
   
    private <T> Sequence<T> diff(List<T> left, List<T> right)
    {
        Sequence<T> sequence = new Sequence<T>();

        Iterator<T> lefItr = left.iterator();
        Iterator<T> rightItr = right.iterator();
        Iterator<Difference> diffItr = new Diff<T>(left, right).diff().iterator();
       
        int leftIdx = 0;
        int rightIdx = 0;

        if(diffItr.hasNext())
        {
            Difference diff = diffItr.next();
            while(rightItr.hasNext() || lefItr.hasNext())
            {
                // equals
                if(leftIdx < diff.getAddedStart() && rightIdx < diff.getDeletedStart()
                    || leftIdx > diff.getAddedEnd() && rightIdx > diff.getDeletedEnd())
                {
                    sequence.add(lefItr.next(), rightItr.next(), State.EQUAL);
                    rightIdx++;
                    leftIdx++;
                }
                // added
                else if(rightIdx == diff.getDeletedStart() && diff.getDeletedEnd() == -1
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(null, rightItr.next(), State.ADDED);
                    leftIdx++;
                }
                // removed
                else if(leftIdx == diff.getAddedStart() && diff.getAddedEnd() == -1
                    && rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd())
                {
                    sequence.add(lefItr.next(), null, State.DELETED);
                    rightIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && leftIdx >= diff.getAddedStart() && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(lefItr.next(), rightItr.next(), State.CHANGED);
                    rightIdx++;
                    leftIdx++;
                }
                // modified
                else if(rightIdx >= diff.getDeletedStart() && rightIdx <= diff.getDeletedEnd()
                    && (leftIdx < diff.getAddedStart() || leftIdx > diff.getAddedEnd())
                    && -1 != diff.getAddedEnd())
                {
                    sequence.add(lefItr.next(), null, State.CHANGED);
                    rightIdx++;
                }
                // modified
                else if((rightIdx < diff.getDeletedStart() || rightIdx > diff.getDeletedEnd())
                    && -1 != diff.getDeletedEnd() && leftIdx >= diff.getAddedStart()
                    && leftIdx <= diff.getAddedEnd())
                {
                    sequence.add(null, rightItr.next(), State.CHANGED);
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
            while(rightItr.hasNext() && lefItr.hasNext())
            {
                sequence.add(lefItr.next(), rightItr.next(), State.EQUAL);
            }
        }
        return sequence;
    }
}
