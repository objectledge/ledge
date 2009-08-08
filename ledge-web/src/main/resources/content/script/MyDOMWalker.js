/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2002
 *
 * $Id: MyDOMWalker.js,v 1.1 2004-11-25 11:28:17 rafal Exp $
 */
function MyDOMWalker()
{
    this.receiver = null;
    //--------------------------------------------------------------
    // Data Access methods

    /** This method traverses a tree. */
    this.walk = function (receiver, node, includeRootNode)
    {
        // set up receiver
        this.receiver = receiver;
        //walk tree
        // including given root
        if(includeRootNode)
        {
            this._walkNode(node, 0);
        }
        // walk only children
        else
        {
            this._loopThroughChildren(node, 0);
        }
    }

    /** This method loops through node children, and calls _walkNode
     * on each of them.
     */
    this._loopThroughChildren = function (node, level)
    {
        var cs = node.childNodes;
        var l = cs.length;
        for (var i = 0; i < l; i++)
        {
            this._walkNode(cs[i], level);
        }
    }

    this._walkNode = function (node, level)
    {
        switch (node.nodeType)
        {
            case 1: // node.ELEMENT_NODE
                // recurse down the tree
                if (node.canHaveChildren || node.hasChildNodes())
                {
                    this.receiver.startElement(node, level);
                    this._loopThroughChildren(node, level+1);
                    this.receiver.endElement(node, level);
                }
                // empty element
                else
                {
                    this.receiver.emptyElement(node, level);
                }
                break;

            case 3: // node.TEXT_NODE
                this.receiver.text(node);
                break;

            case 4: // node.CDATA_SECTION_NODE
                this.receiver.cdata(node);
                break;

            case 8: // node.COMMENT_NODE
                this.receiver.comment(node, level);
                break;

            case 9: // node.DOCUMENT_NODE
                this.receiver.documentStart(node);
                this._loopThroughChildren(node, 0);
                this.receiver.documentEnd(node);
                break;

            default:
                this.receiver.unknownNode(node, level);
        }
    }
}

