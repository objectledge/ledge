/*****************************************************************************
 *
 * Copyright (c) 2003-2004 Kupu Contributors. All rights reserved.
 *
 * This software is distributed under the terms of the Kupu
 * License. See LICENSE.txt for license text. For a list of Kupu
 * Contributors see CREDITS.txt.
 *
 *****************************************************************************/

// $Id: kupuextendedimagetool.js,v 1.1 2005-03-22 06:28:08 zwierzem Exp $

/** Extended image toolbox */
function ExtendedImageToolBox(
    inputfieldid, insertbuttonid, floatselectid, 
	altInputId, originalSizeCheckboxId,
	widthDisplayInputId, heightDisplayInputId,
	borderSizeSelectId, 
	leftMarginSelectId, rightMarginSelectId,
	topMarginSelectId, bottomMarginSelectId,
	toolboxid, plainclass, activeclass) {

	this.ImageToolBox = ImageToolBox;
	this.ImageToolBox(inputfieldid, insertbuttonid, floatselectid,
		toolboxid, plainclass, activeclass);

	this.altInput = document.getElementById(altInputId);
	this.originalSizeCheckbox = document.getElementById(originalSizeCheckboxId);
	this.widthDisplayInput = document.getElementById(widthDisplayInputId);
	this.heightDisplayInput = document.getElementById(heightDisplayInputId);
	this.borderSizeSelect = document.getElementById(borderSizeSelectId);
	this.leftMarginSelect = document.getElementById(leftMarginSelectId);
	this.rightMarginSelect = document.getElementById(rightMarginSelectId);
	this.topMarginSelect = document.getElementById(topMarginSelectId);
	this.bottomMarginSelect = document.getElementById(bottomMarginSelectId);

	this.ImageToolBox_initialize = this.initialize;
    this.initialize = function(tool, editor) {
		this.ImageToolBox_initialize(tool, editor);
		
		//WARN: this is tricky for non JS programmers!!!
    	originalStyleFilter = editor.xhtmlvalid.attrFilters.style;
    	validation = editor.xhtmlvalid;
		editor.xhtmlvalid.setAttrFilter(['style'], function(name, htmlnode, xhtmlnode) {
	        if(htmlnode.nodeName.toLowerCase() == 'img')
    	    {
	    	    var val = htmlnode.style.cssText;
	        	if (val) xhtmlnode.setAttribute('style', val);
	        }
    	    else
        	{
    	    	originalStyleFilter(name, htmlnode, xhtmlnode);
	        }
	    });
    };


	this.ImageToolBox_updateState = this.updateState;
    this.updateState = function(selNode, event) {
        selNode = this._getImageNode(selNode, event);
    	// super call
    	this.ImageToolBox_updateState(selNode, event);
    	// local logic
        if (this.toolboxel) {
        	var image = this.editor.getNearestParentOfType(selNode, 'img');
			this.widthDisplayInput.value = '';
			this.heightDisplayInput.value = '';
        	if(image) {
        		this.altInput.value = image.alt;

        	    if(image.style.width || image.style.height)
        	    {
        	    	this.originalSizeCheckbox.checked = false;
					this.widthDisplayInput.value = image.style.width;
					this.heightDisplayInput.value = image.style.height;
				}
        	    else if(image.width || image.height)
        	    {
        	    	this.originalSizeCheckbox.checked = false;
					this.widthDisplayInput.value = image.width;
					this.heightDisplayInput.value = image.height;
				}
				else
				{
        	    	this.originalSizeCheckbox.checked = true;
				}

				selectSelectItem(this.borderSizeSelect, parseInt(image.style.borderWidth));
				selectSelectItem(this.leftMarginSelect, parseInt(image.style.marginLeft));
				selectSelectItem(this.rightMarginSelect, parseInt(image.style.marginRight));
				selectSelectItem(this.topMarginSelect, parseInt(image.style.marginTop));
				selectSelectItem(this.bottomMarginSelect, parseInt(image.style.marginBottom));

				// WARN: Fix for KUPU bug!!!
				var floatValue = (_SARISSA_IS_MOZ) ? image.style.cssFloat : image.style.styleFloat;
                var floatstyle = floatValue ? floatValue : 'none';
                selectSelectItem(this.floatselect, floatstyle);
        	}
        };
    };

	this._getImageNode = function (selNode, event) {
        // WARN: fix for bad selection behaviour in gecko
        // selNode is really selected image's parent node
        // (weird behaviour, since only image is shown as selected)
        if(_SARISSA_IS_MOZ)
        {
	    	var mozSelection = this.editor.getSelection().selection;
            // take care only about the one click selection with only image
            // selected
	    	if(mozSelection.anchorNode == mozSelection.focusNode &&
	    	   mozSelection.rangeCount == 1)
	    	{
	    	    var range = mozSelection.getRangeAt(0);
	    	    var rootElt = range.commonAncestorContainer;
	    	    var selectedNode = null;
                // only one element must be selected
		        if(mozSelection.focusOffset - mozSelection.anchorOffset == 1) {
	                selectedNode = rootElt.childNodes.item(mozSelection.anchorOffset);
		        };
		 		return selectedNode;
	    	}
        }
        return selNode;
	}

    this.addImage = function() {
        var url = this.inputfield.value;
        var floatstyle = this.floatselect.value;
        var alt = this.altInput.value;

        var setOriginalSize = this.originalSizeCheckbox.checked;
        var width = this.widthDisplayInput.value;
        var height = this.heightDisplayInput.value;

        var borderSize = parseInt(this.borderSizeSelect.value);

        var marginLeft = this.leftMarginSelect.value + "px";
        var marginRight = this.rightMarginSelect.value + "px";
        var marginTop = this.topMarginSelect.value + "px";
        var marginBottom = this.bottomMarginSelect.value + "px";
        
        var img = this.tool.createImage(url, floatstyle);

        img.alt = alt;
        img.setAttribute('alt', alt);

        if(_SARISSA_IS_MOZ)
        {
            img.style.cssFloat = floatstyle;
        }
        else
        {
            img.style.styleFloat = floatstyle;
        }

        if (setOriginalSize) {
		    img.removeAttribute("width");
		    img.removeAttribute("height");
		    img.style.width = null;
		    img.style.height = null;
		}
		else
		{
		    img.style.width = width;
		    img.style.height = height;
		}

        if(borderSize == 0)
        {
            img.style.border = "none";
            img.removeAttribute("border");
        }
        else
        {
            img.style.borderWidth = borderSize+"px";
            img.style.borderStyle = "solid";
        }

        img.style.marginLeft = marginLeft;
        img.style.marginRight = marginRight;
        img.style.marginTop = marginTop;
        img.style.marginBottom = marginBottom;

        this.editor.updateState();
	    return img;
    }
};
