/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2002
 *
 * $Id: DivPopup.js,v 1.1 2004-11-25 11:28:17 rafal Exp $
 */

// Position point tuple
function Point(x, y)
{
    this.x = x;
    this.y = y;
}

// popup singleton call
function getDivPopup(popupNodeId, positionType)
{
    var popupNode = document.getElementById(popupNodeId);

    if(popupNode._popup == null)
    {
        popupNode._popup = new DivPopup(popupNode, positionType);
    }

    return popupNode._popup;
}

function DivPopup(node, positionType)
{
    // ----------------------------------------------------------------------
    // attach HTML popup node
    this.popupNode = node;
    // associate the cross browser style with a popup node
    browserUtil.addXBStyle(this.popupNode);

    //-----------------------------------------------------------------------
    // methods

    // set positioning method
    if(positionType == 'button')
    {
        // position the associated popup on the button
        this.getPosition = function (button, event)
        {
            var x = button.xbstyle.getPageX();
            var y = button.xbstyle.getPageY();

            return new Point(x, y);
        }
    }
    else if(positionType == 'fixed')
    {
        // do nothing - use fixed positioning
        this.getPosition = function (button, event)
        {
            return null;
        }
    }
    // DEFAULT positioning
    else
    {
        // do nothing - use fixed positioning
        this.getPosition = function (button, event)
        {
            return null;
        }
    }

    this.buttonClick = function (event)
    {
        // Get the target button element.
        var button = browserUtil.getEventTarget(event);

        // Blur focus from the link to remove that annoying outline.
        if(button.blur)
        {
            button.blur();
        }

        // associate the cross browser style with a button
        browserUtil.addXBStyle(button);

        // set popup position or use a fixed from CSS
        var point = this.getPosition(button, event);
        if(point != null)
        {
            this.setPosition(point.x, point.y);
        }

        // show the popup
        this.show(button, event);
        return false;
    }

    this.show = function (button, event)
    {
        // show the popup
        this.popupNode.xbstyle.setVisibility("visible");
    }

    this.hide = function ()
    {
        this.popupNode.xbstyle.setVisibility("hidden");
    }

    this.getPopupNode = function (child)
    {
        // Starting with the given node, find the nearest containing element
        // which has an associated popup object.
        var node = child;
        while (node != null)
        {
            if (node._popup != null)
            {
                return node;
            }
            node = node.parentNode;
        }

        return node;
    }

    this.setPosition = function (x, y)
    {
        // Make sure that the popup is within the visible
        // document boundaries.
        // maxX = winWidth - popupWidth
        // maxY = winHeight - popupHeight
        var maxX = xbGetWindowWidth() - this.popupNode.xbstyle.getWidth();
        var maxY = xbGetWindowHeight() - this.popupNode.xbstyle.getHeight();

        // crop popup cooridinates to 0:maxX, 0:maxY
        var popupLeft = (x > maxX) ? maxX : x;
        //var popupTop  = (y > maxY) ? maxY : y;
        var popupTop  = y;


        // winWidth and winHeight are relative to current document scroll,
        // add document scroll values to place a popup in a visible area
        popupLeft += - 4 - 18; // WARN: 18 is scrollbar width
        popupTop += - 4 - 18; // WARN: 18 is scrollbar width

        // set the popup position
        this.popupNode.xbstyle.setLeft(popupLeft);
        this.popupNode.xbstyle.setTop(popupTop);
        //this.popupNode.xbstyle.setPageX(popupLeft);
        //this.popupNode.xbstyle.setPageY(popupTop);
    }
}

