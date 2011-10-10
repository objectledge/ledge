/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2002
 *
 * $Id: StringBuffer.js,v 1.1 2004-11-25 11:28:17 rafal Exp $
 */
function StringBuffer(sString)
{
    //-----------------------------------------------------------------------
    // fields
    this.length = 0;
    this._current   = 0;
    this._parts     = [];
    this._string    = null;    // used to cache the toString results

    //-----------------------------------------------------------------------
    // methods
    this.append = function (sString)
    {
        // append argument
        this.length += (this._parts[this._current++] = String(sString)).length;

        // reset cache
        this._string = null;
        return this;
    };

    this.toString = function ()
    {
        if (this._string != null)
        {
            return this._string;
        }

        return this._string = this._parts.join("");
    };

    // Parametrized constructor !!!
    if (sString != null)
    {
        this.append(sString);
    }
}

