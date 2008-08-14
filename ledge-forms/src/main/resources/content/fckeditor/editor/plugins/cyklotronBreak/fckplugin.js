// Define the command.
var FCKCyklotronPageBreak = function( name )
{
	this.Name = name ;
	this.EditMode = FCK.EditMode;
}

FCKCyklotronPageBreak.prototype.MoveBreakOutsideElement = function()
{
  FCK.FixBody();
	// get all elements in FCK document
	var elements = FCK.EditorDocument.getElementsByTagName( 'img' ) ;

	// check every element for childNodes
	var i = 0;
	var next ;
	while ( element = elements[i++] )
	{
		if ( element.getAttribute( '_Cyklotronpagebreak' ) == "true" )
		{
			while( ( next = element.parentNode.nodeName.toLowerCase() ) != 'body' ) 
			{
				//if we are inside p or div, close immediately this tag, insert break tag, 
				//create new element and move remaining siblings to the next element
				if ( ( next == 'div' || next == 'p' ) && ( element.parentNode.parentNode.nodeName.toLowerCase() == 'body' ) )
				{
					var oParent = element.parentNode ;
					var oDiv = FCK.EditorDocument.createElement( next.toUpperCase() ) ;
					var sibling ;

					while( sibling = element.nextSibling )
						oDiv.appendChild( sibling ) ;

					if ( oDiv.childNodes.length )
					{
						if ( oParent.nextSibling )
							FCK.EditorDocument.body.insertBefore( oDiv, oParent.nextSibling ) ;
						else
							FCK.EditorDocument.body.appendChild( oDiv ) ;
					}

					if ( element.parentNode.nextSibling )
						element.parentNode.parentNode.insertBefore( element, element.parentNode.nextSibling ) ;
					else
						element.parentNode.parentNode.appendChild( element ) ;
						
					if ( !oParent.childNodes.length )
						FCK.EditorDocument.body.removeChild( oParent ) ;
					
					//we must be sure the bogus node is available to make cursor blinking
					if ( FCKBrowserInfo.IsGeckoLike )
						FCKTools.AppendBogusBr( oParent ) ;
						
					break ;
				}
				else
				{
					if ( element.parentNode.nextSibling )
						element.parentNode.parentNode.insertBefore( element, element.parentNode.nextSibling ) ;
					else
						element.parentNode.parentNode.appendChild( element ) ;
				}				
			}			
		}
	}	
}

FCKCyklotronPageBreak.prototype.Execute = function()
{
	if ( FCK.EditMode != FCK_EDITMODE_WYSIWYG ) 
		return ;
	
	FCKUndo.SaveUndoStep() ;

	switch ( this.Name )
	{
		case 'Break' :
			
			var e = FCK.EditorDocument.createElement( 'HR' ) ;
			e.setAttribute("class", "page-break");
			
			var oFakeImage = FCKDocumentProcessor_CreateFakeImage( 'FCK__PageBreak', e ) ;
			oFakeImage.setAttribute( "_Cyklotronpagebreak", "true" ) ;
			var oRange = new FCKDomRange( FCK.EditorWindow ) ;
			oRange.MoveToSelection() ;
			var oSplitInfo = oRange.SplitBlock() ;
			oRange.InsertNode( oFakeImage ) ;
			FCK.Events.FireEvent( 'OnSelectionChange' ) ;
			
			this.MoveBreakOutsideElement();
		break;
		default :
		break;
	}	
}

FCKCyklotronPageBreak.prototype.GetState = function()
{
	return ( FCK.EditMode == FCK_EDITMODE_WYSIWYG ? FCK_TRISTATE_OFF : FCK_TRISTATE_DISABLED ) ;
}

// Register the Cyklotron tag commands.
FCKCommands.RegisterCommand( 'CyklotronPageBreak', new FCKCyklotronPageBreak( 'Break' ) ) ;
// Create the Cyklotron tag buttons.
var oCyklotronItem = new FCKToolbarButton( 'CyklotronPageBreak', FCKLang.CyklotronPageBreak, null, null, false, true, 100 ) ;
oCyklotronItem.IconPath = FCKConfig.PluginsPath + 'cyklotronBreak/break.gif';
FCKToolbarItems.RegisterItem( 'CyklotronPageBreak', oCyklotronItem ) ;

// after switch in to source mode and back proccess page and insert fake
// image for break again
// Cyklotron Page Breaks Processor

var FCKCyklotronPageBreaksProcessor = FCKDocumentProcessor.AppendNew() ;
FCKCyklotronPageBreaksProcessor.ProcessDocument = function( document )
{
	// get all elements in FCK document
	var elements = document.getElementsByTagName( '*' ) ;

	// check every element for childNodes
	var i = 0;
	while (element = elements[i++]) {
		var nodes = element.childNodes;

		var j = 0;
		while (node = nodes[j++]) {
			if (node.tagName == 'HR') {
				var re = /page-break/ ;
				var PContent;
				if (re.test(node.className))
					PContent = FCKConfig.ProtectedSource.Revert('<hr class="page-break" />', false);

				if (node.classname == 'page-break' || PContent == '<hr class="page-break" />') {					
					var oFakeImage = FCKDocumentProcessor_CreateFakeImage( 'FCK__PageBreak', node.cloneNode(true) ) ;
					oFakeImage.setAttribute( "_Cyklotronpagebreak", "true" ) ;
					node.parentNode.insertBefore( oFakeImage, node ) ;
					node.parentNode.removeChild( node ) ;						
				}
			}
		}
	}
	FCKCyklotronPageBreak.prototype.MoveBreakOutsideElement();
}

if ( !FCK.Config.ProtectedSource._RevertOld )
	FCK.Config.ProtectedSource._RevertOld = FCK.Config.ProtectedSource.Revert ;

FCK.Config.ProtectedSource.Revert = function( html, clearBin )
{
	// Call the original code.
	var result = FCK.Config.ProtectedSource._RevertOld ( html, clearBin ) ;
	
	if ( typeof FCKCyklotronPageBreak !="undefined" )
		//var re = /<(p|div)>(<hr class=\"page-break\"\W*\/>)+<\/\1>/gi ;
		var re = /<hr class=\"page-break\"\W*\/>/gi ;
		
	result = result.replace( re, '<hr class="page-break" />' );
	
	return result ;
}