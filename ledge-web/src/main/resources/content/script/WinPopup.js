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
        	if(width < 1.0)
        	{
            	width =  Math.floor(window.screen.availWidth * width);
        	}
        	this.width = width;
        }
        if(height != null)
        {
        	if(height < 1.0)
        	{
            	height = Math.floor(window.screen.availHeight * height);
        	}
			this.height = height;
        }

        // set default positioning
        if(positionType == null)
        {
            positionType = "center middle";
        }

        // set default modifiers
        if(modifiers == null)
        {
            modifiers = "dependent=yes,toolbar=no,directories=no,location=no,"+
                        "status=no,menubar=no,scrollbars=yes,resizable=yes";
        }
        // set window width and height
        if(this.width > window.screen.availWidth)
        {
            this.width = window.screen.availWidth;
        }
        if(this.height > window.screen.availHeight)
        {
            this.height = window.screen.availHeight;
        }
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
                    winLeft = window._winPopups_mouseX - winWidth / 2;
                    winTop = window._winPopups_mouseY - winHeight / 2;
                    break;
                //
                case 'left':
                    winLeft = 0;
                    break;
                case 'right':
                    winLeft = window.screen.availWidth - winWidth;
                    break;
                //
                case 'top':
                    winTop = 0;
                    break;
                case 'bottom':
                    winTop = window.screen.availHeight - winHeight;
                    break;
                //
                case 'center':
                    winLeft = (window.screen.availWidth - winWidth) / 2;
                    break;
                case 'middle':
                    winTop = (window.screen.availHeight - winHeight) / 2;
                    break;
                default:
        			var positionDef = positionElts[i].split(':');
        			var orientation = positionDef[0];
        			var posNumber = parseFloat(positionDef[1]);
        			posNumber = posNumber < 0 ? 0 : posNumber;
        			posNumber = posNumber > 1.0 ? 1.0 : posNumber;
        			if(orientation == 'horizontal')
        			{
                    	winLeft = Math.round((window.screen.availWidth - winWidth) * posNumber);
                		//alert(positionElts[i]+'\n'+orientation+'\n'+posNumber+'\n'+winLeft);
        			}
        			else if(orientation == 'vertical')
        			{
                    	winTop = Math.round((window.screen.availHeight - winHeight) * posNumber);
                		//alert(positionElts[i]+'\n'+orientation+'\n'+posNumber+'\n'+winTop);
        			}
            }
        }

        // - - - - - - - - - - - - - - - - - - -
        // fix window position to avoid exceptions
        var margin = 30; // 30 pixel margin for window decorations

        if(winLeft + winWidth >= window.screen.availWidth)
        {
            winLeft = window.screen.availWidth - winWidth - margin;
        }
        if(winLeft < 0)
        {
            winLeft = 0;
        }

        if(winTop + winHeight >= window.screen.availHeight)
        {
            winTop = window.screen.availHeight - winHeight - margin;
        }
        if(winTop < 0)
        {
            winTop = 0;
        }

        /*alert(' left:'+winLeft+' top:'+winTop+
              '\n width:'+winWidth+' height:'+winHeight+
              '\n sumWidth:'+(winLeft+winWidth)+' sumHeight:'+(winTop+winHeight)+
              '\n screenWidth:'+window.screen.availWidth+' screenHeight:'+window.screen.availHeight);
         */
        this.window.moveTo(winLeft, winTop);
    }
}

