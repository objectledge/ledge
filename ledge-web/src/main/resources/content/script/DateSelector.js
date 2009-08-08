/**
 * Date selector support.
 *
 * @author <a href="mailto:rafal@caltha.pl">Rafa????? Krzewski</a>
 * @author <a href="mailto:dgajda@caltha.pl">Damian Gajda</a>
 * @version $Id: $
 */
function initDays(selectedTime)
{
    var selectedDate = new Date(selectedTime);
    var date = new Date(selectedDate.getTime());

    var opts = '';
    date.setDate(1);
    while(date.getMonth() == selectedDate.getMonth())
    {
        var s;
        s = '    <option value=\''+date.getDate()+'\'';
        if(date.getDate() == selectedDate.getDate())
        {
            s += ' selected="selected"';
        }
        s += '>'+date.getDate()+'</option>\n';

        opts += s;
        date.setDate(date.getDate()+1);
    }
    document.write(opts);
}

function initMonths(selectedTime, monthNames)
{
    var selectedDate = new Date(selectedTime);

    var opts = '';
    for(var i=1; i<=12; i++)
    {
        var s;
        s = '    <option value=\''+i+'\'';
        if(selectedDate.getMonth() == i)
        {
            s += ' selected';
        }
        s += '>'+monthNames[i-1]+'</option>\n';

        opts += s;
    }
    document.write(opts);
}

function initYears(selectedTime, startYear, endYear)
{
    var selectedDate = new Date(selectedTime);

    var opts = '';
    for(var i=startYear; i<=endYear; i++)
    {
        var s;
        s = '    <option value=\''+i+'\'';
        if(selectedDate.getFullYear() == i)
        {
            s += ' selected';
        }
        s += '>'+i+'</option>\n';

        opts += s;
    }
    document.write(opts);
}

function dateElementChanged(element)
{
    var i = element.name.lastIndexOf('_');
    var base = element.name.substring(0,i);
    var changed = element.name.substring(i+1);
    var form = element.form;

    var yearElem = form.elements[base+'_year'];
    var monthElem = form.elements[base+'_month'];
    var dayElem = form.elements[base+'_day'];
    var dateElem = form.elements[base];
    var hourElem = form.elements[base+'_hour'];
    var minuteElem = form.elements[base+'_minute'];
    var year = yearElem.value;
    var month = monthElem.value;
    var day = dayElem.value;
    var hour = hourElem.value;
    var minute = minuteElem.value;

    if(changed == 'month' || (changed == 'year' && month == 1))
    {
        var date = new Date(year, month, 1);
        var ref = new Date(year, month, 1);
        if(dayElem.options != null)
        {
            dayElem.options.length = 0;
        }
        while(date.getMonth() == ref.getMonth())
        {
            var ooption = document.createElement('OPTION');
            ooption.text = date.getDate();
            ooption.value = date.getDate();
            if(browserUtil.ie)
            {
                dayElem.add(ooption);
            }
            else if(browserUtil.dom)
            {
                dayElem.add(ooption, null);
            }
            date.setDate(date.getDate()+1);
        }
        if(day > dayElem.options.length)
        {
            day = dayElem.options.length;
        }
        dayElem.selectedIndex = day-1;
    }
    var selected = new Date(year, month, day, hour, minute);
    dateElem.value = selected.getTime();
}

function dateEnabled(element, saveDisabledString)
{
    var i = element.name.lastIndexOf('_');
    var base = element.name.substring(0,i);
    var form = element.form;

    var yearElem = form.elements[base+'_year'];
    var monthElem = form.elements[base+'_month'];
    var dayElem = form.elements[base+'_day'];
    var hourElem = form.elements[base+'_hour'];
    var minuteElem = form.elements[base+'_minute'];
    var dateElem = form.elements[base];

    var year = yearElem.value;
    var month = monthElem.value;
    var day = dayElem.value;
    var hour = hourElem.value;
    var minute = minuteElem.value;
    var selectedDate = new Date(year, month, day, hour, minute, 0, 0);

    if(element.value == 'true')
    {
        dateElem.value = selectedDate.getTime();
        yearElem.disabled = false;
        monthElem.disabled = false;
        dayElem.disabled = false;
        hourElem.disabled = false;
        minuteElem.disabled = false;
    }
    else
    {
    	if(saveDisabledString)
    	{
        	dateElem.value = ''+selectedDate.getTime() + '/disabled';
    	}
    	else
    	{
        	dateElem.value = '';
    	}
        yearElem.disabled = true;
        monthElem.disabled = true;
        dayElem.disabled = true;
        hourElem.disabled = true;
        minuteElem.disabled = true;
    }
}

/**
function dateToggleMaxMin(element)
{
    var i = element.name.lastIndexOf('_');
    var base = element.name.substring(0,i);
    var form = element.form;

    var yearElem = form.elements[base+'_year'];
    var monthElem = form.elements[base+'_month'];
    var dayElem = form.elements[base+'_day'];
    var hourElem = form.elements[base+'_hour'];
    var minuteElem = form.elements[base+'_minute'];
    var dateElem = form.elements[base];

    var limes = element.options[element.selectedIndex].value;

    var year = yearElem.options[yearElem.selectedIndex].value;
    var month = monthElem.options[monthElem.selectedIndex].value;
    var day = dayElem.options[dayElem.selectedIndex].value;
    var hour = hourElem.options[hourElem.selectedIndex].value;
    var minute = minuteElem.options[minuteElem.selectedIndex].value;
    var selectedDate = new Date(year, month, day, hour, minute, 0, 0);

	if(limes != 'date')
	{
        yearElem.disabled = true;
        monthElem.disabled = true;
        dayElem.disabled = true;
        hourElem.disabled = true;
        minuteElem.disabled = true;
	}
	else
	{
        dateElem.value = selectedDate.getTime();
        yearElem.disabled = false;
        monthElem.disabled = false;
        dayElem.disabled = false;
        hourElem.disabled = false;
        minuteElem.disabled = false;
	}

	if(limes == '-')
	{
        dateElem.value = 0;
	}
	else if(limes == '+')
	{
        dateElem.value = 128849018820000; // 6053-01-23 03:07
	}
	else if(limes == 'disabled')
	{
        dateElem.value = '';
	}
}
*/
