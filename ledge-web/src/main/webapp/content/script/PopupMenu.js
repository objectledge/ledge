/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2003
 *
 * $Id: PopupMenu.js,v 1.1 2004-11-25 11:28:17 rafal Exp $
 */
browserUtil.addEventListener(document, "mousedown", openPopupMenu);

function openPopupMenu(event)
{
    // get popup menu
    if(document._popupMenuSingleton == null)
    {
        document._popupMenuSingleton = new PopupMenu();
    }
    var popupMenu = document._popupMenuSingleton;

    // process event
    return popupMenu.process(event);
}

function PopupMenu()
{
    this.currentMenu = null;
}

PopupMenu.prototype.process =
function (event)
{
    var target = browserUtil.getEventTarget(event);

    while(target.nodeType != 1) // not ELEMENT_NODE
    {
        target = target.parentNode;
    }

    var button = this.getButtonNode(target);
    // button or menu
    if(button != null)
    {
        // menu
        if(this.isInMenu(this.currentMenu, target))
        {
            // do nothing = let the menu item handle it
        }
        // button
        else
        {
            var elts = button.getElementsByTagName('DIV');
            var menuDiv = elts[0];


            browserUtil.addXBStyle(button);
            var x = button.xbstyle.getPageX();
            var y = button.xbstyle.getPageY();
            //var x = event.pageX;
            //var y = event.pageY;
            y += button.xbstyle.getClientHeight();

            browserUtil.addXBStyle(menuDiv);

            menuDiv.xbstyle.setLeft(x);
            menuDiv.xbstyle.setTop(y);
            //menuDiv.xbstyle.setPageX(x);
            //menuDiv.xbstyle.setPageY(y);

            if(menuDiv == this.currentMenu)
            {
                this.hideMenu();
            }
            else
            {
                this.hideMenu();
                menuDiv.xbstyle.setVisibility("visible");
                this.currentMenu = menuDiv;
            }
            //alert(event.pageX+' '+event.pageY+'\n'+x+' '+y);
        }
    }
    // other
    else
    {
        this.hideMenu();
    }

    return false;
}

PopupMenu.prototype.getButtonNode =
function (elt)
{
    while(elt != document)
    {
        if(elt.className.indexOf("-popupmenubutton") != -1)
        {
            return elt;
        }
        elt = elt.parentNode;
    }
    return null;
}

PopupMenu.prototype.isInMenu =
function (menu, elt)
{
    while(elt != document)
    {
        if(elt == menu)
        {
            return true;
        }
        elt = elt.parentNode;
    }
    return false;
}

PopupMenu.prototype.hideMenu =
function ()
{
    if(this.currentMenu != null)
    {
    this.currentMenu.xbstyle.setVisibility("hidden");
    }
    this.currentMenu = null;
}

