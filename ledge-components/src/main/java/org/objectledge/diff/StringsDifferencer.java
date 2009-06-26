package org.objectledge.diff;

import org.incava.util.diff.Diff;
import org.incava.util.diff.Difference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringsDifferencer
{
    /** The name of The empty line (<code>""</code>). */
    private final static String EMPTY_LINE = "";

    private enum State
    {
        DIFF, EQUALS, ADDED, DEL
    }

    public class Element
    {

        private String leftElement;

        private String rightElement;

        private State state;

        public Element(String left, String right)
        {
            leftElement = left;
            rightElement = right;
            state = State.EQUALS;
        }

        public Element(String left, String right, State s)
        {
            leftElement = left;
            rightElement = right;
            state = s;
        }

        public final State getState()
        {
            return state;
        }

        public void setState(State state)
        {
            this.state = state;
        }

        public final String getLeftElement()
        {
            return leftElement;
        }

        public final String getRightElement()
        {
            return rightElement;
        }
    }

    public class Block
    {
        private List<Element> elements;

        private State state;

        public Block()
        {
            elements = new ArrayList<Element>();
            state = State.EQUALS;
        }

        public Block(List<Element> elem)
        {
            elements = elem;
            state = State.EQUALS;
        }

        public Block(List<Element> elem, State s)
        {
            elements = elem;
            state = s;
        }

        public final State getState()
        {
            return state;
        }

        public void setState(State state)
        {
            this.state = state;
        }

        public void add(Element elem)
        {
            elements.add(elem);
        }

        public void addAll(List<Element> elem)
        {
            elements = elem;
        }

        public final List<Element> getElements()
        {
            return elements;
        }

        public void clear()
        {
            elements.clear();
        }

        public final int getSize()
        {
            return elements.size();
        }
    }

    /*
     *  parse example
     *    
          stringA      stringB  ia  ib     Difference 
             
            "a",         "a",    0, 0
                         "x",    1, 1     [1,-1], [1,2]
                         "y",    1, 2
            "b",         "b".    1, 3
            "c",         "c",    2, 4
            "d",         "j",    3, 5     [3, 3], [5,5]
            "e"          "e",    4, 6
            
    */
    
    /** The name of default blocks separator (<code>"\n"</code>). */
    public final static String BLOCKS_SEPARATOR = "\n\r";

    /** The name of default elements separator (<code>"\\b"</code>). */
    public final static String ELEMENTS_SEPARATOR = "\\b";

    private List<String> leftBlocks;

    private List<String> rightBlocks;

    private String blocksSeparator;

    private String elementsSeparator;

    public StringsDifferencer(String oldStringsBlock, String newStringsBlock)
    {
        blocksSeparator = BLOCKS_SEPARATOR;
        elementsSeparator = ELEMENTS_SEPARATOR;

        leftBlocks = new ArrayList<String>(Arrays.asList(oldStringsBlock.split(blocksSeparator)));
        rightBlocks = new ArrayList<String>(Arrays.asList(newStringsBlock.split(blocksSeparator)));
    }

    public StringsDifferencer(String oldStringsBlock, String newStringsBlock,
        String blockSeparator, String elementSeparator)
    {
        blocksSeparator = blockSeparator;
        elementsSeparator = elementSeparator;

        leftBlocks = new ArrayList<String>(Arrays.asList(oldStringsBlock.split(blockSeparator)));
        rightBlocks = new ArrayList<String>(Arrays.asList(newStringsBlock.split(blockSeparator)));
    }

    public StringsDifferencer(List<String> oldStringsList, List<String> newStringsList)
    {
        blocksSeparator = BLOCKS_SEPARATOR;
        elementsSeparator = ELEMENTS_SEPARATOR;

        leftBlocks = oldStringsList;
        rightBlocks = newStringsList;
    }

    public StringsDifferencer(List<String> oldStringsList, List<String> newStringsList,
        String blockSeparator, String elementSeparator)
    {
        blocksSeparator = blockSeparator;
        elementsSeparator = elementSeparator;

        leftBlocks = oldStringsList;
        rightBlocks = newStringsList;
    }

    public List<String> getLeftBlocks()
    {
        return leftBlocks;
    }

    public List<String> getRightBlocks()
    {
        return rightBlocks;
    }

    public String getBlocksSeparator()
    {
        return blocksSeparator;
    }

    public String getElementsSeparator()
    {
        return elementsSeparator;
    }

    public List<Element> diffBlocks()
    {
        return splitDiff(leftBlocks, rightBlocks);
    }

    public List<Block> diffElements()
    {
        List<String> leftElementsList;
        List<String> rightElementsList;

        List<Element> diffBlocksList;
        Block diffElementsList;
        List<Block> diffList;

        diffList = new ArrayList<Block>();

        diffBlocksList = splitDiff(leftBlocks, rightBlocks);
        for (Element diffBlock : diffBlocksList)
        {
            diffElementsList = new Block();
            if(diffBlock.getState().equals(State.DIFF))
            {
                leftElementsList = new ArrayList<String>(Arrays.asList(diffBlock.leftElement
                    .split(elementsSeparator)));
                rightElementsList = new ArrayList<String>(Arrays.asList(diffBlock.rightElement
                    .split(elementsSeparator)));

                diffElementsList.addAll(splitDiff(leftElementsList, rightElementsList));
                diffElementsList.setState(State.DIFF);
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

    private List<Element> splitDiff(List<String> leftList, List<String> rightList)
    {
        Difference difference;

        Diff<String> diff = new Diff<String>(leftList, rightList);
        List<Difference> differences = diff.diff();
        List<Element> diffList = new ArrayList<Element>();

        Iterator<Difference> differencesItr;
        Iterator<String> rightBlocksItr, leftBlocksItr;

        int rightBlockIndex = 0;
        int leftBlockIndex = 0;

        diffList.clear();
        differencesItr = differences.iterator();
        rightBlocksItr = rightList.iterator();
        leftBlocksItr = leftList.iterator();

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
                    diffList.add(new Element(leftBlocksItr.next(), rightBlocksItr.next(),
                        State.EQUALS));
                    rightBlockIndex++;
                    leftBlockIndex++;
                }
                // added
                else if(rightBlockIndex == difference.getDeletedStart()
                    && difference.getDeletedEnd() == -1
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    diffList.add(new Element(EMPTY_LINE, rightBlocksItr.next(), State.ADDED));
                    leftBlockIndex++;
                }
                // removed
                else if(leftBlockIndex == difference.getAddedStart()
                    && difference.getAddedEnd() == -1
                    && rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd())
                {
                    diffList.add(new Element(leftBlocksItr.next(), EMPTY_LINE, State.DEL));
                    rightBlockIndex++;
                }
                // modified
                else if(rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd()
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    diffList.add(new Element(leftBlocksItr.next(), rightBlocksItr.next(),
                        State.DIFF));
                    rightBlockIndex++;
                    leftBlockIndex++;
                }
                // modified
                else if(rightBlockIndex >= difference.getDeletedStart()
                    && rightBlockIndex <= difference.getDeletedEnd()
                    && (leftBlockIndex < difference.getAddedStart() || leftBlockIndex > difference
                        .getAddedEnd()) && -1 != difference.getAddedEnd())
                {
                    diffList.add(new Element(leftBlocksItr.next(), EMPTY_LINE, State.DIFF));
                    rightBlockIndex++;
                }
                // modified
                else if((rightBlockIndex < difference.getDeletedStart() || rightBlockIndex > difference
                    .getDeletedEnd())
                    && -1 != difference.getDeletedEnd()
                    && leftBlockIndex >= difference.getAddedStart()
                    && leftBlockIndex <= difference.getAddedEnd())
                {
                    diffList.add(new Element(EMPTY_LINE, rightBlocksItr.next(), State.DIFF));
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
                diffList
                    .add(new Element(leftBlocksItr.next(), rightBlocksItr.next(), State.EQUALS));
            }
        }
        return diffList;
    }
}

