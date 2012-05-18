/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2002/12/17 16:53:22  zwierzem
 * New way of JS linking
 *
 * Revision 1.2  2002/10/28 13:09:25  zwierzem
 * Updated for use with ScriptLoader.js.
 *
 * Revision 1.2  2002/10/25 11:05:23  zwierzem
 * New JS dynamic loading implmented.
 *
 * Revision 1.9  2002/07/22 14:31:51  bc6ix
 * fix license path, remove ua.js detection, use xbLibrary to load scripts
 *
 * Revision 1.8  2002/07/11 11:30:52  bc6ix
 * getEffectiveValue - Special Case IE's Clip retrieval
 *
 * Revision 1.7  2002/05/16 15:02:50  bc6ix
 * split xbStyle.js into browser specific files
 *
 * Revision 1.6  2002/05/16 04:49:49  bc6ix
 *
 * xbStyle constructor - added window argument to allow getComputedStyle to
 * use the correct document.
 *
 * xbStyleGetEffectiveValue - qualified document references using window added
 * to xbStyle constructor. fixed DOM Style property name to CSS2 property
 * name conversion. Disable gecko work arounds (at least temporarily).
 *
 * cssStyleGetClip - return comma separated values.
 *
 * cssStyleGetClientXXX - return element.clientXXX temporarily while bugs
 * worked out.
 *
 * TODO: split into 3 files, fix Client properties (or remove them).
 *
 * Revision 1.5  2002/05/14 16:52:53  bc6ix
 * use CVS Log for revision history
 *
 */

/* ***** BEGIN LICENSE BLOCK *****
 * Licensed under Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * Full Terms at /lib/js/license/mpl-tri-license.txt
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Netscape code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2001
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Bob Clary <bclary@netscape.com>
 *
 * ***** END LICENSE BLOCK ***** */

function xbStyleNotSupported() {}

function xbStyleNotSupportStringValue(propname) { xbDEBUG.dump(propname + ' is not supported in this browser'); return '';};

/////////////////////////////////////////////////////////////
// xbClipRect

function xbClipRect(a1, a2, a3, a4)
{
  this.top  = 0;
  this.right  = 0;
  this.bottom  = 0;
  this.left  = 0;

  if (typeof(a1) == 'string')
  {
    var val;
    var ca;
    var i;

    if (a1.indexOf('rect(') == 0)
    {
      // I would have preferred [0-9]+[a-zA-Z]+ for a regexp
      // but NN4 returns null for that.
      ca = a1.substring(5, a1.length-1).match(/-?[0-9a-zA-Z]+/g);
      for (i = 0; i < 4; ++i)
      {
        val = xbToInt(ca[i]);
        if (val != 0 && ca[i].indexOf('px') == -1)
        {
          xbDEBUG.dump('xbClipRect: A clipping region ' + a1 + ' was detected that did not use pixels as units.  Click Ok to continue, Cancel to Abort');
          return;
        }
        ca[i] = val;
      }
      this.top    = ca[0];
      this.right  = ca[1];
      this.bottom = ca[2];
      this.left   = ca[3];
    }
  }
  else if (typeof(a1) == 'number' && typeof(a2) == 'number' && typeof(a3) == 'number' && typeof(a4) == 'number')
  {
    this.top    = a1;
    this.right  = a2;
    this.bottom = a3;
    this.left   = a4;
  }
}

xbClipRect.prototype.top = 0;
xbClipRect.prototype.right = 0;
xbClipRect.prototype.bottom = 0;
xbClipRect.prototype.left = 0;


function xbClipRectGetWidth()
{
    return this.right - this.left;
}
xbClipRect.prototype.getWidth = xbClipRectGetWidth;

function xbClipRectSetWidth(width)
{
  this.right = this.left + width;
}
xbClipRect.prototype.setWidth = xbClipRectSetWidth;

function xbClipRectGetHeight()
{
    return this.bottom - this.top;
}
xbClipRect.prototype.getHeight = xbClipRectGetHeight;

function xbClipRectSetHeight(height)
{
  this.bottom = this.top + height;
}
xbClipRect.prototype.setHeight = xbClipRectSetHeight;

function xbClipRectToString()
{
  return 'rect(' + this.top + 'px ' + this.right + 'px ' + this.bottom + 'px ' + this.left + 'px )' ;
}
xbClipRect.prototype.toString = xbClipRectToString;

/////////////////////////////////////////////////////////////
// xbStyle
//
// Note Opera violates the standard by cascading the effective values
// into the HTMLElement.style object. We can use IE's HTMLElement.currentStyle
// to get the effective values. In Gecko we will use the W3 DOM Style Standard getComputedStyle

function xbStyle(obj, win, position)
{
  if (typeof(obj) == 'object' && typeof(obj.style) != 'undefined')
    this.styleObj = obj.style;
  else if (document.layers) // NN4
  {
    if (typeof(position) == 'undefined')
      position = '';

    this.styleObj = obj;
    this.styleObj.position = position;
  }
  this.object = obj;
  this.window = win ? win : window;
}

xbStyle.prototype.styleObj = null;
xbStyle.prototype.object = null;

/////////////////////////////////////////////////////////////
// xbStyle.getEffectiveValue()
// note that xbStyle's constructor uses the currentStyle object
// for IE5+ and that Opera's style object contains computed values
// already. Netscape Navigator's layer object also contains the
// computed values as well. Note that IE4 will not return the
// computed values.

function xbStyleGetEffectiveValue(propname)
{
  var value = null;

  if (this.window.document.defaultView && this.window.document.defaultView.getComputedStyle)
  {
    // W3
    // Note that propname is the name of the property in the CSS Style
    // Object. However the W3 method getPropertyValue takes the actual
    // property name from the CSS Style rule, i.e., propname is
    // 'backgroundColor' but getPropertyValue expects 'background-color'.

     var capIndex;
     var cappropname = propname;

     while ( (capIndex = cappropname.search(/[A-Z]/)) != -1)
     {
       if (capIndex != -1)
       {
         cappropname = cappropname.substring(0, capIndex) + '-' + cappropname.substring(capIndex, capIndex+1).toLowerCase() + cappropname.substr(capIndex+1);
       }
     }

     value = this.window.document.defaultView.getComputedStyle(this.object, '').getPropertyValue(cappropname);

     // xxxHack for Gecko:
     if (!value && this.styleObj[propname])
     {
       value = this.styleObj[propname];
     }
  }
  else if (typeof(this.styleObj[propname]) == 'undefined')
  {
    value = xbStyleNotSupportStringValue(propname);
  }
  else if (typeof(this.object.currentStyle) != 'undefined')
  {
    // IE5+
    value = this.object.currentStyle[propname];
    if (!value)
    {
      value = this.styleObj[propname];
    }

    if (propname == 'clip' && !value)
    {
      // clip is not stored in IE5/6 handle separately
      value = 'rect(' + this.object.currentStyle.clipTop + ', ' + this.object.currentStyle.clipRight + ', ' + this.object.currentStyle.clipBottom + ', ' + this.object.currentStyle.clipLeft + ')';
    }
  }
  else
  {
    // IE4+, Opera, NN4
    value = this.styleObj[propname];
  }

  return value;
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.moveAbove()

function xbStyleMoveAbove(cont)
{
  this.setzIndex(cont.getzIndex()+1);
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.moveBelow()

function xbStyleMoveBelow(cont)
{
  var zindex = cont.getzIndex() - 1;

  this.setzIndex(zindex);
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.moveBy()

function xbStyleMoveBy(deltaX, deltaY)
{
  this.moveTo(this.getLeft() + deltaX, this.getTop() + deltaY);
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.moveTo()

function xbStyleMoveTo(x, y)
{
  this.setLeft(x);
  this.setTop(y);
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.moveToAbsolute()

function xbStyleMoveToAbsolute(x, y)
{
  this.setPageX(x);
  this.setPageY(y);
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.resizeBy()

function xbStyleResizeBy(deltaX, deltaY)
{
  this.setWidth( this.getWidth() + deltaX );
  this.setHeight( this.getHeight() + deltaY );
}

/////////////////////////////////////////////////////////////////////////////
// xbStyle.resizeTo()

function xbStyleResizeTo(x, y)
{
  this.setWidth(x);
  this.setHeight(y);
}

////////////////////////////////////////////////////////////////////////

xbStyle.prototype.getEffectiveValue     = xbStyleGetEffectiveValue;
xbStyle.prototype.moveAbove             = xbStyleMoveAbove;
xbStyle.prototype.moveBelow             = xbStyleMoveBelow;
xbStyle.prototype.moveBy                = xbStyleMoveBy;
xbStyle.prototype.moveTo                = xbStyleMoveTo;
xbStyle.prototype.moveToAbsolute        = xbStyleMoveToAbsolute;
xbStyle.prototype.resizeBy              = xbStyleResizeBy;
xbStyle.prototype.resizeTo              = xbStyleResizeTo;

if (document.all || document.getElementsByName)
{
  scriptLoader.loadCommon('netscape.developer/xbStyle-css.js');
}
else if (document.layers)
{
  scriptLoader.loadCommon('netscape.developer/xbStyle-nn4.js');
}
else
{
  scriptLoader.loadCommon('netscape.developer/xbStyle-not-supported.js');
}


