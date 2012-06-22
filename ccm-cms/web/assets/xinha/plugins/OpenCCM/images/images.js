OpenCCM.prototype.showImageDialog = function(image)
{
  if (!this.dialogs["images"]) this.prepareImageDialog();
  
  var editor = this.editor;
	if ( typeof image == "undefined" )
  {
    image = editor.getParentElement();
    if ( image && image.tagName.toLowerCase() != 'img' )
    {
      image = null;
    }
  }

  if ( image )
  {
    function getSpecifiedAttribute(element,attribute)
    {
      var a = element.attributes;
      for (var i=0;i<a.length;i++)
      {
        if (a[i].nodeName == attribute && a[i].specified)
        {
          return a[i].value;
        }
      }
      return '';
    }
    outparam =
    {
      f_url    : editor.stripBaseURL(image.getAttribute('src',2)), // the second parameter makes IE return the value as it is set, as opposed to an "interpolated" (as MSDN calls it) value
      f_alt    : image.alt,
      f_border : image.border,
      f_align  : image.align,
      f_vert   : getSpecifiedAttribute(image,'vspace'),
      f_horiz  : getSpecifiedAttribute(image,'hspace'),
      f_width  : image.width,
      f_height : image.height
    };
  }
  else{
  {
    outparam =
    {
      f_url    : '',
      f_alt    : '',
      f_border : '',
      f_align  : '',
      f_vert   : '',
      f_horiz  : '',
      f_width  : '',
      f_height : ''
    };
  }
  }
  this.image = image;
  // now calling the show method of the Xinha.Dialog object to set the values and show the actual dialog
  this.dialogs["images"].show(outparam);
};

OpenCCM.prototype.prepareImageDialog = function()
{
  var self = this;
  var editor = this.editor;
  
  var dialog = this.dialogs["images"] = new Xinha.Dialog(editor, OpenCCM.imagesHtml, 'Xinha',{width:410})

  // Connect the OK and Cancel buttons
  dialog.getElementById('ok').onclick = function() {self.imageApply();}
  dialog.getElementById('cancel').onclick = function() { self.dialogs["images"].hide()};

/*
  dialog.getElementById('preview').onclick = function() { 
    var f_url = dialog.getElementById("f_url");
    var url = f_url.value;

    if (!url) {
      alert(dialog._lc("You must enter the URL"));
      f_url.focus();
        return false;
      }
    dialog.getElementById('ipreview').src = url;
    return false;
  }
  dialog.onresize = function ()
  {
    var newHeightForPreview = 
    parseInt(this.height,10) 
    - this.getElementById('h1').offsetHeight 
    - this.getElementById('buttons').offsetHeight
    - this.getElementById('inputs').offsetHeight 
    - parseInt(this.rootElem.style.paddingBottom,10); // we have a padding at the bottom, gotta take this into acount


    this.getElementById("ipreview").style.height = ((newHeightForPreview > 0) ? newHeightForPreview : 0) + "px"; // no-go beyond 0

    this.getElementById("ipreview").style.width = this.width - 2   + 'px'; // and the width

  }
*/

  this.imageDialogReady = true;
};

// and finally ... take some action
OpenCCM.prototype.imageApply = function()
{
  var param = this.dialogs["images"].hide();
  if (!param.f_url)
  {
    return;
  }
  var editor = this.editor;
  var img = this.image;
  if ( !img )
  {
    if ( Xinha.is_ie )
    {
      var sel = editor.getSelection();
      var range = editor.createRange(sel);
      editor._doc.execCommand("insertimage", false, param.f_url);
      img = range.parentElement();
      // wonder if this works...
      if ( img.tagName.toLowerCase() != "img" )
      {
        img = img.previousSibling;
      }
    }
    else
    {
      img = document.createElement('img');
      img.src = param.f_url;
      editor.insertNodeAtSelection(img);
      if ( !img.tagName )
      {
        // if the cursor is at the beginning of the document
        img = range.startContainer.firstChild;
      }
    }
  }
  else
  {
    img.src = param.f_url;
  }

  for ( var field in param )
  {
    var value = param[field];
    switch (field)
    {
      case "f_alt":
      if (value)
      img.alt = value;
      else
      img.removeAttribute("alt");
      break;
      case "f_border":
      if (value)
      img.border = parseInt(value || "0");
      else
      img.removeAttribute("border");
      break;
      case "f_align":
      if (value.value)
      img.align = value.value;
      else
      img.removeAttribute("align");
      break;
      case "f_vert":
      if (value != "")
      img.vspace = parseInt(value || "0");
      else
      img.removeAttribute("vspace");
      break;
      case "f_horiz":
      if (value != "")
      img.hspace = parseInt(value || "0");
      else
      img.removeAttribute("hspace");
      break;
      case "f_width":
      if (value)
      img.width = parseInt(value || "0");
      else
      img.removeAttribute("width");
      break;
      case "f_height":
      if (value)
      img.height = parseInt(value || "0");
      else
      img.removeAttribute("height");
      break;
    }
  }

};
