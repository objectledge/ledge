/**
 * This script was created by:
 * @author <a href="rkrzewsk@caltha.pl">Rafa� Krzewski</a>
 * @author <a href="dgajda@caltha.pl">Damian Gajda</a>
 * Copyright 2002-2003 Caltha
 *
 * $Id: PropertySelector.js,v 1.1 2004-11-25 11:28:17 rafal Exp $
 */
scriptLoader.loadCommon('Forms.js');

function PropertySelector(attributeNames, form, elementNames, callBackObject)
{
   var attributesStr = new String(attributeNames);
   this.attributes = attributesStr.split(' ');

   this.form = form;

   var elementsStr = new String(elementNames);
   this.elements = elementsStr.split(' ');

   this.setValues = function (values)
   {
   		if(callBackObject)
   		{
			callBackObject.beforeValuesSet(this);
		}
        for(var i=0; i<this.elements.length; i++)
        {
        	var elementName = this.elements[i];
            var attributeName = this.attributes[i];
            var value = values[attributeName];

            Forms.setValue(this.form, elementName, value);
        }
   		if(callBackObject)
   		{
	        callBackObject.afterValuesSet(this);
	    }
   };
};
