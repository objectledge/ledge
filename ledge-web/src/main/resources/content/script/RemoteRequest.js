// Based on the DevEdge article by Marcio Galli, Roger Soares and Ian Oeschger
// http://devedge.netscape.com/viewsource/2003/inner-browsing/

// initialization ///////////////////////////////////////////////////////////

function XMLRemoteRequest() 
{
	try 
	{
		this.xComponent = new XMLHttpRequest();
		this.stringHandler = geckoStringHandler;
		this.domHandler = geckoDomHandler;
	} 
	catch(e) 
	{
		try 
		{
			this.xComponent = new ActiveXObject("Microsoft.XMLHTTP");
            this.stringHandler = msieStringHandler;
			this.domHandler = msieDomHandler;
		} 
		catch(e) 
		{
			window.alert("Browser does not support XMLHTTPRequest.\nPlease upgdade.");
		}
	}
}

// Public API ///////////////////////////////////////////////////////////////

XMLRemoteRequest.prototype.getRemoteDocument = function (urlString) 
{
    return this.domHandler(this.xComponent, urlString);
}

XMLRemoteRequest.prototype.getRemoteDocumentString = function (urlString) 
{
	return this.stringHandler(this.xComponent, urlString);
}

// Gecko implementation /////////////////////////////////////////////////////

function geckoDomHandler(xmlComp, urlString) 
{
	xmlComp.open("GET", urlString, false);
	xmlComp.send(null);

	if (xmlComp.responseXML) 
	{
		return xmlComp.responseXML;
	}
	else
	{
	    return null;
	}
}

function geckoStringHandler(xmlComp, urlString) 
{
	xmlComp.open("GET", urlString, false);
	xmlComp.send(null);
	if(xmlComp.responseXML) 
	{
		var dummyDoc = xmlComp.responseXML;
		var dummySerializer = new XMLSerializer();
		docString = dummySerializer.serializeToString(dummyDoc);

		return docString;
	}
	else
	{
	    return null;
	}
}

// MSIE Implementation //////////////////////////////////////////////////////

function msieDomHandler(xmlComp, requestString) 
{
	xmlComp.open("GET", requestString, false);
	xmlComp.send();
	return xmlComp.responseXML;
}

function msieStringHandler(xmlComp, requestString) 
{
	xmlComp.open("GET", requestString, false);
	xmlComp.send();
	return xmlComp.responseText;
}
