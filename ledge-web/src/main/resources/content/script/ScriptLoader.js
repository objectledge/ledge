/*
 * This script was created by Damian Gajda (zwierzem@ngo.pl)
 * Copyright 2002
 *
 * $Id: ScriptLoader.js,v 1.3 2005-06-24 08:34:37 zwierzem Exp $
 */

function ScriptLoader(commonBasePath)
{
    this.commonBasePath = this.fixBasePath(commonBasePath);
    this.loadedScripts = [];
}

ScriptLoader.prototype.fixBasePath =
function (basePath)
{
    if(basePath.charAt(basePath.length-1) == '/')
    {
        basePath = basePath.substr(0, basePath.length-1)
    }
    return basePath;
};

ScriptLoader.prototype.loadCommon =
function (relativePath)
{
    this.load(this.commonBasePath, relativePath);
};

ScriptLoader.prototype.load =
function (basePath, relativePath)
{
    var path = basePath + '/' + relativePath;

    var alreadyLoaded = false;
    for(var i=0; i<this.loadedScripts.length; i++)
    {
        if(this.loadedScripts[i] == path)
        {
            alreadyLoaded = true;
            break;
        }
    }

    if(!alreadyLoaded)
    {
        this.loadedScripts[this.loadedScripts.length] = path;
        document.write('<script type="text/javascript" language="javascript" src="'
                        + path + '"><\/script>');
    }
};

if(!window.scriptLoader && window.javaScriptBaseDir)
    scriptLoader = new ScriptLoader(window.javaScriptBaseDir);
