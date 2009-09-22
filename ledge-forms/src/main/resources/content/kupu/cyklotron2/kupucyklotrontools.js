/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupucyklotrontools.js,v 1.1 2005-03-22 06:28:08 zwierzem Exp $

/** Property selector impl for compatibility with cyklotron 1 */
function CyklotronLinkToolboxPropertySelector(linkToolbox)
{
	this.linkToolbox = linkToolbox;
	
	this.setValues = function (values)
   	{
   		this.linkToolbox.input.value = values['cms_path'];
	}
};

/** Property selector impl for compatibility with cyklotron 1 */
function CyklotronFileLinkToolboxPropertySelector(linkToolbox)
{
	this.linkToolbox = linkToolbox;
	
	this.setValues = function (values)
   	{
   		this.linkToolbox.input.value = values['absoluteurl'];
	}
};

function CyklotronLinkTool() {
	// hack for KupuTool properties
	this.KupuTool = KupuTool;
	this.KupuTool();
	this.LinkTool = LinkTool;
	this.LinkTool();
	
	this.LinkTool_createContextMenuElements = this.createContextMenuElements;
    this.createContextMenuElements = function(selNode, event) {
        var ret = this.LinkTool_createContextMenuElements(selNode, event);
		if(ret[0].action == this.createLinkHandler)
		{
			ret = new Array();
		}
        return ret;
    };
};

/** create and edit links toolbox */
function CyklotronLinkToolBox(
	inputid, buttonid, chooseButtonId, chooseFileButtonId, anchorSelectId,
	toolboxid, plainclass, activeclass) {
		
	this.AnchorLinkToolBox = AnchorLinkToolBox;
	this.AnchorLinkToolBox(inputid, buttonid, anchorSelectId, toolboxid, plainclass, activeclass);

    this.chooseButton = document.getElementById(chooseButtonId);
    this.chooseFileButton = document.getElementById(chooseFileButtonId);

  	this.AnchorLinkToolBox_initialize = this.initialize;
    this.initialize = function(tool, editor) {
  		this.AnchorLinkToolBox_initialize(tool, editor); // TODO: check if events are bound properly

	    this.chooseLinkUrl = this.editor.config.documentsel_popup_url;
	    this.chooseFileUrl = this.editor.config.filesel_popup_url;
	    
	    addEventHandler(this.chooseButton, 'click', this.chooseLinkHandler, this);
	    addEventHandler(this.chooseFileButton, 'click', this.chooseFileHandler, this);
    };

    this.chooseLinkHandler = function(event) {
    	window.propertySelector = new CyklotronLinkToolboxPropertySelector(this);
        var linkWindow = openPopup(this.chooseLinkUrl, 300, 550);
        linkWindow.focus();
    };

    this.chooseFileHandler = function(event) {
    	window.propertySelector = new CyklotronFileLinkToolboxPropertySelector(this);
        var linkWindow = openPopup(this.chooseFileUrl, 600, 550);
        linkWindow.focus();
    };
    
    this._setAnchorAsLink = function ()
    {
	    this.input.value = "htmlarea:#"+this.anchorSelect.value;
    };
};

/** Property selector impl for compatibility with cyklotron 1 */
function CyklotronImageToolboxPropertySelector(imageToolbox)
{
	this.setValues = function (values)
   	{
   		this.imageToolbox.inputfield.value = values['url'];
	}

	this.imageToolbox = imageToolbox;
}

function CyklotronImageTool() {
	// hack for KupuTool properties
	this.KupuTool = KupuTool;
	this.KupuTool();
	this.ImageTool = ImageTool;
	this.ImageTool();
	
	this.EMPTY_ARRAY = new Array();
	
    this.createContextMenuElements = function(selNode, event) {
        return this.EMPTY_ARRAY;
    };
}

function CyklotronImageToolBox(
	inputfieldid, chooseImageButtonId, insertbuttonid, floatselectid,
	altInputId, originalSizeButtonId,
	widthDisplayInputId, heightDisplayInputId,
	borderSizeSelectId, 
	leftMarginSelectId, rightMarginSelectId,
	topMarginSelectId, bottomMarginSelectId,
	toolboxid, plainclass, activeclass) {
		
	this.ExtendedImageToolBox = ExtendedImageToolBox;
	this.ExtendedImageToolBox(
		inputfieldid, insertbuttonid, floatselectid,
	 	altInputId, originalSizeButtonId,
	 	widthDisplayInputId, heightDisplayInputId,
		borderSizeSelectId, 
		leftMarginSelectId, rightMarginSelectId,
		topMarginSelectId, bottomMarginSelectId,
		toolboxid, plainclass, activeclass);

	this.chooseImageButton = document.getElementById(chooseImageButtonId);

	this.ExtendedImageToolBox_initialize = this.initialize;
    this.initialize = function(tool, editor) {
		this.ExtendedImageToolBox_initialize(tool, editor);

		this.chooseImageUrl = this.editor.config.imagesel_popup_url;
		
		addEventHandler(this.chooseImageButton, 'click', this.chooseImageHandler, this);
    };

	this.ExtendedImageToolBox_updateState = this.updateState;
    this.updateState = function(selNode, event) {
        selNode = this._getImageNode(selNode, event);
    	// super call
    	this.ExtendedImageToolBox_updateState(selNode, event);
    	// local logic
        if (this.toolboxel) {
        	var image = this.editor.getNearestParentOfType(selNode, 'img');
        	if(image) {
				this.toggler.show();
        	}
        };
    };

    this.chooseImageHandler = function(event) {
    	window.propertySelector = new CyklotronImageToolboxPropertySelector(this);
        var imageWindow = openPopup(this.chooseImageUrl, 600, 550);
        imageWindow.focus();
    };

	this.setToggler = function (toggler) {
		this.toggler = toggler;
	};
};


function CyklotronTableToolBox(addtabledivid, edittabledivid, newrowsinputid, 
                    newcolsinputid, makeheaderinputid, classselectid, alignselectid, addtablebuttonid,
                    addrowbuttonid, delrowbuttonid, addcolbuttonid, delcolbuttonid, fixbuttonid,
                    fixallbuttonid, toolboxid, plainclass, activeclass) {
		
	this.TableToolBox = TableToolBox;
	this.TableToolBox(
		addtabledivid, edittabledivid, newrowsinputid, 
        newcolsinputid, makeheaderinputid, classselectid, alignselectid, addtablebuttonid,
        addrowbuttonid, delrowbuttonid, addcolbuttonid, delcolbuttonid, fixbuttonid,
        fixallbuttonid, toolboxid, plainclass, activeclass);

	this.TableToolBox_updateState = this.updateState;
    this.updateState = function(selNode) {
    	// super call
		this.TableToolBox_updateState(selNode);
		// local logic
        var table = this.editor.getNearestParentOfType(selNode, 'table');
        //alert(table.nodeName);
        if (table) {
			this.toggler.show();
        };
    };

	this.setToggler = function (toggler) {
		this.toggler = toggler;
		this.toggler.setRadioHiding(false);
	};

};


function PageBreakTool(pagebreakbuttonid) {
	// hack for KupuTool properties
	this.KupuTool = KupuTool;
	this.KupuTool();

    /* Page break tools creates hr tags with page-break class */
    this.pageBreakButton = document.getElementById(pagebreakbuttonid);

    this.initialize = function(editor) {
        /* attach the event handlers */
        this.editor = editor;
        this._fixTabIndex(this.pageBreakButton);
        addEventHandler(this.pageBreakButton, "click", this.addPageBreak, this);
        this.editor.logMessage('Page break tool initialized');
    };
 
    this.addPageBreak = function(event, nograb) {
        var node = this.editor.getSelection().getSelectedNode();
        while(node && node.parentNode && node.parentNode.nodeName.toLowerCase() != 'body')
        {
            node = node.parentNode;
        }
        if(node.parentNode.nodeName.toLowerCase() == 'body')
        {
            var hrNode = this.editor.getDocument().getDocument().createElement("hr");
            hrNode.setAttribute("class", "page-break");
            node.parentNode.insertBefore(hrNode, node);
            hrNode.className = "page-break";
        }
     };
};

function RemoveFormatTool(removeformatbuttonid) {
	// hack for KupuTool properties
	this.KupuTool = KupuTool;
	this.KupuTool();

    /* Remove format tool removes formatting from selected text */
    this.removeFormatButton = document.getElementById(removeformatbuttonid);

    this.initialize = function(editor) {
        /* attach the event handlers */
        this.editor = editor;
        this._fixTabIndex(this.removeFormatButton);
        addEventHandler(this.removeFormatButton, "click", this.removeFormat, this);
        this.editor.logMessage('Remove format tool initialized');
    };
 
    this.removeFormat = function(event, nograb) {
        this.editor.execCommand("RemoveFormat");
    };
};


	