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
        Difference difference;

        Diff<T> diff = new Diff<T>(left, right);
        List<Difference> differences = diff.diff();
        Sequence<T> sequence = new Sequence<T>();

        Iterator<Difference> differencesItr;
        Iterator<T> rightBlocksItr, leftBlocksItr;

        int rightBlockIndex = 0;
        int leftBlockIndex = 0;

        sequence.clear();
        differencesItr = differences.iterator();
        rightBlocksItr = right.iterator();
        leftBlocksItr = left.iterator();

        if(differencesItr.hasNext())
        {
            difference = differencesItr.next();
            while(rightBlocksItr.hasNext() || leftBlocksItr.hasNext())
            {

                // equals
                if(leftBlockIndex < difference.getAddedStart()
                    && rightBlockIndex < difference.getDeletedStart()
                    || leftBlockIndex > difference.getAddedEnd()
                    && rightBlockIndex > difference.getDeletedEnd())
                {
                    sequence.add(leftBlocksItr.next(), rightBlocksItr.next(), State.EQUAL);
                    rightBlockIndex++;
                    leftBlockIndex++;
                }
                // added
                else if(rightBlockIndex == difference.getDeletedStart()
                    && difference.getDeletedEnd() == -1
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    sequence.add(null, rightBlocksItr.next(), State.ADDED);
                    leftBlockIndex++;
                }
                // removed
                else if(leftBlockIndex == difference.getAddedStart()
                    && difference.getAddedEnd() == -1
                    && rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd())
                {
                    sequence.add(leftBlocksItr.next(), null, State.DELETED);
                    rightBlockIndex++;
                }
                // modified
                else if(rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd()
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    sequence.add(leftBlocksItr.next(), rightBlocksItr.next(), State.CHANGED);
                    rightBlockIndex++;
                    leftBlockIndex++;
                }
                // modified
                else if(rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd()
                    && (leftBlockIndex < difference.getAddedStart() || leftBlockIndex > difference
                        .getAddedEnd()) && -1 != difference.getAddedEnd())
                {
                    sequence.add(leftBlocksItr.next(), null, State.CHANGED);
                    rightBlockIndex++;
                }
                // modified
                else if((rightBlockIndex < difference.getDeletedStart() || rightBlockIndex > difference
                    .getDeletedEnd())
                    && -1 != difference.getDeletedEnd()
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    sequence.add(null, rightBlocksItr.next(), State.CHANGED);
                    leftBlockIndex++;
                }

                if(leftBlockIndex > difference.getAddedEnd()
                    && rightBlockIndex > difference.getDeletedEnd() && differencesItr.hasNext())
                {
                    difference = differencesItr.next();
                }
            }
        }
        else
        {
            while(rightBlocksItr.hasNext() && leftBlocksItr.hasNext())
            {
                sequence.add(leftBlocksItr.next(), rightBlocksItr.next(), State.EQUAL);
            }
        }
        return sequence;
    }
}
