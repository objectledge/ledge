browserUtil.addEventListener(document, "mousemove", function (event)
{
    window._winPopups_mouseX = event.screenX;
    window._winPopups_mouseY = event.screenY;
});

function getWinPopup(popupId)
{
    if(window._winPopups == null)
    {
        window._winPopups = [];
    }

    if(window._winPopups[popupId] == null)
    {
        window._winPopups[popupId] = new WinPopup(popupId);
    }

    return window._winPopups[popupId];
}

function WinPopup(id)
{
    this.id = id;
    this.window = null;
    this.width = 300;
    this.height = 400;

    this.close = function ()
    {
        if(this.window != null && !this.window.closed)
        {
            this.window.close();
        }
    }

    this.open = function (urlOrContent, width, height, positionType, modifiers)
    {
        // set window width and height
        if(width != null)
        {
            this.width = width;
        }
        if(height != null)
        {
            this.height = height;
        }

        // set default positioning
        if(positionType == null)
        {
            positionType = "mouse";
        }

        // set default modifiers
        if(modifiers == null)
        {
            modifiers = "dependent=yes,toolbar=no,directories=no,location=no,"+
                        "status=no,menubar=no,scrollbars=yes,resizable=yes";
        }
        // set window width and height
        modifiers += ",width="+this.width+",height="+this.height;

        // close previously opened window
        this.close();


        // set the url or content inside the window
        if(urlOrContent.indexOf('<') == 0 || urlOrContent.indexOf('<html') != -1)
        {
	        // (re)open window
	        this.window = window.open("", this.id, modifiers);
            this.window.document.open();
            this.window.document.write(urlOrContent);
            this.window.document.close();
        }
        else
        {
	        // (re)open window
	        this.window = window.open(urlOrContent, this.id, modifiers);
        }

        // position the window
        this.setPosition(positionType, width, height);
        // focus it
        this.window.focus();

        return this.window;
    }

    this.setPosition = function (positionType, winWidth, winHeight)
    {
        var winLeft = 0;
        var winTop = 0;

        positionType = positionType.toLowerCase();
        var positionElts = positionType.split(' ');

        for(var i=0; i < positionElts.length; i++)
        {
            switch(positionElts[i])
            {
                case 'mouse':
                    winLeft = window._winPopups_mouseX;
                    winTop = window._winPopups_mouseY;
                    break;
                //
                case 'left':
                    winLeft = 0;
                    break;
                case 'right':
                    winLeft = window.screen.width;
                    break;
                //
                case 'top':
                    winTop = 0;
                    break;
                case 'bottom':
                    winTop = window.screen.height;
                    break;
                //
                case 'center':
                    winLeft = (window.screen.width - winWidth) / 2;
                    break;
                case 'middle':
                    winTop = (window.screen.height - winHeight) / 2;
                    break;
            }
        }

        // - - - - - - - - - - - - - - - - - - -
        // fix window position to avoid exceptions
        var margin = 30; // 30 pixel margin for window decorations

        if(winLeft + winWidth >= window.screen.width)
        {
            winLeft = window.screen.width - winWidth - margin;
        }
        if(winLeft < 0)
        {
            winLeft = 0;
        }

        if(winTop + winHeight >= window.screen.height)
        {
            winTop = window.screen.height - winHeight - margin;
        }
        if(winTop < 0)
        {
            winTop = 0;
        }

        //alert(winLeft+' '+winTop+' '+winWidth+' '+winHeight);
        this.window.moveTo(winLeft, winTop);
    }
}

