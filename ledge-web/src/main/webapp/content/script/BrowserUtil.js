scriptLoader.loadCommon('netscape.developer/xbStyle.js');
scriptLoader.loadCommon('netscape.developer/xbDOM.js');
//---------------------------------------------------------------------------
// Browser detection and utility functions

function BrowserUtil()
{
    this.op = (navigator.userAgent.indexOf('Opera') != -1);
    this.ie = !this.op && document.all != null;
    this.dom = !this.op && document.getElementsByName != null;

    if (this.ie)
    {
        this.getEventTarget = function (event)
        {
            return event.srcElement;
        }
    }
    else
    {
        this.getEventTarget = function (event)
        {
            return event.target;
        }
    }

    this.getNextElement = function (node)
    {
        var next;

        for (next = node.nextSibling; next; next = next.nextSibling)
        {
            if (next.nodeType == 1) // Node.ELEMENT_NODE
            {
                return next;
            }
        }

        return null;
    }

    this.addXBStyle = function (node)
    {
        if (node.xbstyle == null)
        {
            node.xbstyle = new xbStyle(node);
        }
    }

    if(this.ie)
    {
        this.addEventListener = function (eventTarget, eventName, listenerFunction)
        {
            eventTarget.attachEvent("on" + eventName, listenerFunction);
        }
    }
    else if(this.dom)
    {
        this.addEventListener = function (eventTarget, eventName, listenerFunction)
        {
            eventTarget.addEventListener(eventName, listenerFunction, false);
        }
    }

    this.submitForm = function (formName, actionValue)
    {
        var form = document.forms[formName];
        form.action = actionValue;
        form.submit();
    }
}

var browserUtil = new BrowserUtil();

