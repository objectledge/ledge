/**
 * Date selector support.
 *
 * @author <a href="mailto:rkrzewsk@ngo.pl">Rafa³ Krzewski</a>
 * @version $Id: DateSelector.js,v 1.3 2003/08/08 12:45:56 rkrzewsk Exp $
 */

function initDays(selectedTime)
{
    var selected = new Date(selectedTime);
    var date = new Date(selected.getTime());

    var opts = '';
    date.setDate(1);
    while(date.getMonth() == selected.getMonth())
    {
        var s;
        s = '    <option value=\''+date.getDate()+'\'';
        if(date.getDate() == selected.getDate())
        {
            s += ' selected';
        }
        s += '>'+date.getDate()+'</option>\n';

        opts += s;
        date.setDate(date.getDate()+1);
    }
    document.write(opts);
}

function initMonths(selectedTime, monthNames)
{
    var selected = new Date(selectedTime);

    var opts = '';
    for(var i=1; i<=12; i++)
    {
        var s;
        s = '    <option value=\''+i+'\'';
        if(selected.getMonth() == i)
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
    var selected = new Date(selectedTime);

    var opts = '';
    for(var i=startYear; i<=endYear; i++)
    {
        var s;
        s = '    <option value=\''+i+'\'';
        if(selected.getFullYear() == i)
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
    var year = yearElem.options[yearElem.selectedIndex].value;
    var month = monthElem.options[monthElem.selectedIndex].value;
    var day = dayElem.options[dayElem.selectedIndex].value;
    var hour = hourElem.options[hourElem.selectedIndex].value;
    var minute = minuteElem.options[minuteElem.selectedIndex].value;

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

function dateEnabled(element)
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

    var year = yearElem.options[yearElem.selectedIndex].value;
    var month = monthElem.options[monthElem.selectedIndex].value;
    var day = dayElem.options[dayElem.selectedIndex].value;
    var hour = hourElem.options[hourElem.selectedIndex].value;
    var minute = minuteElem.options[minuteElem.selectedIndex].value;
    var selected = new Date(year, month, day, hour, minute, 0, 0);

    if(element.value == 'true')
    {
        dateElem.value = selected.getTime();
        yearElem.disabled = false;
        monthElem.disabled = false;
        dayElem.disabled = false;
        hourElem.disabled = false;
        minuteElem.disabled = false;
    }
    else
    {
        dateElem.value = '';
        yearElem.disabled = true;
        monthElem.disabled = true;
        dayElem.disabled = true;
        hourElem.disabled = true;
        minuteElem.disabled = true;
    }
}
