function FormsUtil()
{
	/**
	 * This function gets a value from a HTML control
	 *
	 * @param formName <i>string</i> - form's name
	 * @param controlName <i>string</i> - control's name
	 *
	 * @return <i>string</i> or <i>array</i> - controls value, or null
	 */
	this.getValue = function (formName, controlName)
	{
		var controlType = this.getType(formName, controlName);
		
		if(controlType == null)
		{
			return null;
		}
		
		var control = document.forms[formName].elements[controlName];
		
		// text controls with value property
		if(controlType == 'hidden' || controlType == 'text' || controlType == 'textarea' ||
				controlType == 'password' || controlType == 'file')
		{
			return control.value;
		}
		
		// "SELECT" without multiple
		if(controlType == 'select-one')
		{
			if(control.selectedIndex >= 0)
			{
				return control.options[control.selectedIndex].value;
			}
			else
			{
				return null;
			}
		}
		
		// "SELECT" with MULTIPLE
		if(controlType == 'select-multiple')
		{
			result = new Array();
			for (var j = 0; j < control.options.length; j++)
			{
				if(control.options[j].selected)
				{
					result.push(control.options[j].value);
				}
			}
			return result;
		}
		
		// CHECKBOX or RADIO
		if(controlType == 'checkbox' || controlType == 'radio')
		{
			if(control.checked)
			{
				return control.value;
			}
			else
			{
				return null;
			}
		}
		
		//  CHECKBOXes or RADIO group
		if(controlType == 'field-group')
		{
			var result = new Array();
			
			var iter = new Iterator(control);
			while(iter.hasNext())
			{
				var tmpControl = iter.next();
				var tmpType = tmpControl.type;
				if(tmpType == 'hidden' || tmpType == 'text' || tmpType == 'textarea' ||
						tmpType == 'password' || tmpType == 'file'
							|| tmpControl.checked)
				{
					result.push(tmpControl.value);
				}
			}
			return result;
		}
		
		// all sorts of buttons
		if(controlType == 'reset' || controlType == 'submit' || controlType == 'button')
		{
			return control.value;
		}
		
		// FIELDSET is not supported
		
		alert('UNSUPPORTED CONTROL TYPE '+controlType);
	};
	
	/**
	 * Sets a value of a control.
	 *
	 * @param formName <i>string</i> - form's name
	 * @param controlName <i>string</i> - control's name
	 * @param value <i>string</i> || <i>array</i> - value to be set
	 *              if it is a string it is treated as a normal value,
	 *              if it is an array and a control is a multiple select or checkbox group
	 *                       - it is treated as a collection of values
	 */
	this.setValue = function (formName, controlName, value)
	{
		var controlType = this.getType(formName, controlName);
		var control = document.forms[formName].elements[controlName];
		
		// text controls with value property
		if(controlType == 'hidden' || controlType == 'text' || controlType == 'textarea' ||
				controlType == 'password' || controlType == 'file' )
		{
			control.value = value;
		}
		
		// "SELECT" without multiple
		else if(controlType == 'select-one')
		{
			if(typeof(value) == 'string')
			{
				for (var colindex=0; colindex < control.length; colindex++)
				{
					if(control.options[colindex].value == value)
					{
						control.selectedIndex = colindex;
					}
				}
			}
		}
		
		// "SELECT" with MULTIPLE
		else if(controlType == 'select-multiple')
		{
			if(typeof(value) == 'string')
			{
				for (var j=0; j < control.length; j++)
				{
					if(value == control.options[j].value)
					{
						control.options[j].selected = true;
					}
				}
			}
			else if(typeof(value) == 'array')
			{
				for (var j=0; j < control.length; j++)
				{
					for (var k=0; k < value.length; k++)
					{
						if(value[k] == control.options[j].value)
						{
							control.options[j].selected = true;
						}
					}
				}
			}
		}
		
		// CHECKBOX or RADIO
		else if(controlType == 'checkbox' || controlType == 'radio')
		{
			if(typeof(value) == 'string')
			{
				if(value == control.value)
				{
					control.checked = true;
				}
				else
				{
					control.checked = false;
				}
			}
			else if(typeof(value) == 'array')
			{
				for (j = 0; j < value.length; j++)
				{
					if(value[j] == control.value)
					{
						control.checked = true;
					}
					else
					{
						control.checked = false;
					}
				}
			}
		}
		
		//  CHECKBOXes or RADIO group
		else if(controlType == 'field-group')
		{
			var iter = new Iterator(control);
			if(typeof(value) == 'string')
			{
				while(iter.hasNext())
				{
					var tmpControl = iter.next();
					if(value == tmpControl.value)
					{
						tmpControl.checked = true;
					}
					else
					{
						tmpControl.checked = false;
					}
				}
			}
			else if(typeof(value) == 'array')
			{
				while(iter.hasNext())
				{
					var tmpControl = iter.next();
					
					for (j = 0; j < value.length; j++)
					{
						if(value[j] == tmpControl.value)
						{
							tmpControl.checked = true;
						}
						else
						{
							tmpControl.checked = false;
						}
					}
				}
			}
		}
		
		// przyciski r�nego typu
		else if(controlType == 'reset' || controlType == 'submit' || controlType == 'button')
		{
			control.value = value;
		}
		
		else
		{
			alert('UNSUPPORTED CONTROL TYPE '+controlType);
		}
	};
	
	/**
	 * Returns a type of a control
	 * @param formName <i>string</i> - form's name
	 * @param controlName <i>string</i> - control's name
	 *
	 * @return <i>string</i> - control type or null
	 */
	this.getType = function (formName, controlName)
	{
		if(controlName == null || formName == null)
		{
			alert("WRONG PARAMETERS!!!");
			return null;
		}
		
		if(document.forms[formName] == null)
		{
			alert("FORM '"+formName+"' DOES NOT EXIST");
			return null;
		}
		
		if(document.forms[formName].elements[controlName] == null)
		{
			alert("CONTROL '"+controlName+"' in FORM '"+formName+"' DOES NOT EXIST");
			return null;
		}
		
		/*    alert(formName+' '+controlName+'\n\n'+typeof(document.forms[formName].elements[controlName])
          +"\n\n"+document.forms[formName].elements[controlName]
          +"\n\n"+document.forms[formName].elements[controlName].length);
		 */
		var control = document.forms[formName].elements[controlName];
		return this.getType2(control);
	};
	
	/**
	 * Returns a type of a control
	 * @param control <i>object</i> - control
	 *
	 * @return <i>string</i> - control type or null
	 */
	this.getType2 = function (control)
	{
		if(control == null)
		{
			alert("WRONG PARAMETERS!!!");
			return null;
		}
		
		if(control.type != null)
		{
			return control.type;
		}
		
		if(control.length > 0)
		{
			//alert("classname = "+control.className);
			return 'field-group';
		}
		
		// Checking for FieldSet
		// W3C DOM implementation
		if(document.implementation != null && document.implementation.hasFeature('HTML',''))
		{
			if(control.tagName == 'FIELDSET')
			{
				return 'field-set';
			}
		}
		// old Browser -- przetestowano Netscape 4.76
		else if(typeof(control) == 'object')
		{
			return 'field-set-0';
		}
		
		alert("UNKNOWN CONTROL TYPE");
		return null;
	};	
}

var Forms = new FormsUtil();

/**
 *  Collection Iterator
 */
function Iterator(collection)
{
    this.collection = collection;
    this.index = 0;
    this.lastLength = this.collection.length;

    this.reset = function ()
    {
        this.index = 0;
        this.lastLength = this.collection.length;
    };

    this.hasNext = function ()
    {
        return (this.move(0) != null);
    };

    this.next = function ()
    {
        return this.move(1);
    };

    this.prev = function ()
    {
        return this.move(-1);
    };

    this.move = function (step)
    {
        // collection changed between iterations
        if(this.lastLength != this.collection.length) {
            alert('Iterator: collection changed between iterations!');
            return null;
        }

        // passed collection boundary
        if(this.index >= collection.length ||
            this.index < 0 ) {
          //alert('Iterator: Passed a collection boundary!');
            return null;
        }

        // everything seems to be OK
        var index = this.index;
        this.index += step;

        // W3C DOM implementation
        if(document.implementation != null
           && document.implementation.hasFeature('HTML',''))
        {
            return this.collection.item(index);
        }
        // old Browser
        else if(this.collection.length != null
                && this.collection.length > 0)
        {
            return this.collection[index];
        }
    };
}

