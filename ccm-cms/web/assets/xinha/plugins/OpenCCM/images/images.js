OpenCCM.prototype.showImageDialog = function(image)
{
  if (!this.dialogs["images"])
  {
    this.prepareImageDialog();
  }
  
  var editor = this.editor;
  this.image = image;

  var data =
  {
    src         : "",
    name        : "",
    alt         : "",
    title       : "",
    width       : "",
    height      : "",
    caption     : "",
    zoom        : "",
    gallery     : "",
    aspect      : ""
  };

  if (typeof image == "undefined" )
  {
  alert ("was?");
    image = editor.getParentElement();
    if ( image && image.tagName.toLowerCase() != 'img' )
    {
      image = null;
      alert("WAS?");
    }
  }

  if(image && image.tagName.toLowerCase == "img")
  {
    var src = this.editor.fixRelativeLinks(image.getAttribute('src'));
    
    data.src    = src;
    data.alt    = image.alt;
    data.name   = image.name;
    data.title  = image.title;
    data.width  = image.width;
    data.height = image.height;

    data.caption = ""/*image.next.tagName=="span"*/;
    data.zoom = "" /*image.parent.rel == "zoom"*/;
    data.gallery = "" /*image.parent.rel == "imageGallery"*/;

//    data.context = image.parent.rel;

    data.alignment   = "" /*image.parent.parent.class*/;

//      aspect      = "";
  }

  // now calling the show method of the Xinha.Dialog object to set the values and show the actual dialog
  this.dialogs["images"].show(data);
};

OpenCCM.prototype.prepareImageDialog = function()
{
  var self = this;
  var editor = this.editor;
  
  var dialog = this.dialogs["images"] = new Xinha.Dialog(editor, OpenCCM.imagesHtml, 'Xinha',{width:410})

  // Connect the OK and Cancel buttons
  dialog.getElementById('ok').onclick = function() {self.imageApply();}
//  dialog.getElementById('clear').onclick = function() { self.imageRemove(); };
  dialog.getElementById('cancel').onclick = function() { self.dialogs["images"].hide()};

  // Connect the Select button
  dialog.getElementById('browse').onclick = function() { self.imageBrowse(window); };

  this.imageDialogReady = true;
};

// 
OpenCCM.prototype.imageApply = function()
{

  var values = this.dialogs["images"].hide();
  var image  = this.image;
  var editor = this.editor;

  var imgAttr =
  {
    src    : "",
    alt    : "",
    title  : "",
    width  : "",
    height : ""
  };

  var spanAttr =
  {
    class   : "caption",
    style   : "",
  }

  var linkAttr =
  {
    href  : "",
    rel   : "",
    class : ""
    
  };

  var divAttr =
  {
    class : "image"
  };

  // If not all mandatory informations are set
  if (!values.src)
  {
    // don't do anything at all
    return false;
  }

  // Read form values for image
  imgAttr.src = values.src;
  imgAttr.alt = values.alt;
  imgAttr.title = values.title;
  imgAttr.width = values.width;
  imgAttr.height = values.height;

  // Read form values for caption
  if(values.caption)
  {
    spanAttr.style = "width:" + imgAttr.width + "px";
  }

  // Read form values for link
  if(values.zoom || values.gallery)
  {
    linkAttr.href = values.src;
    
    if(values.zoom)
    {
      linkAttr.rel = "";
      linkAttr.class = "imageZoom";
    }
    
    else if(values.gallery)
    {
      linkAttr.rel = "imageGalleryName";
      linkAttr.class = "imageGallery";
    }
  }

  // Read form values for div
/*
  if(values.alignment != "")
  {
    var alignment = values.alignment;
    divAttr.class += " " + alignment;
  }
*/

  // Modify Image
  if(image && image.tagName.toLowerCase() == "img")
  {
   alert("Modifying image aka removing currently selected image");
  }

  // Add Image
  alert("Adding image");
  

  var div = document.createElement("div");
  for(var attr in divAttr)
  {
    div.setAttribute(attr, divAttr[attr]);
  }

  if(values.zoom || values.gallery)
  {
    link = document.createElement("a");
    for(var attr in linkAttr)
    {
      link.setAttribute(attr, linkAttr[attr]);
    }
    div.appendChild(link);
  }

  var img = document.createElement("img");
  for(var attr in imgAttr)
  {
    img.setAttribute(attr, imgAttr[attr]);
  }
  if(values.zoom || values.gallery)
  {
    link.appendChild(img);
  }
  else
  {
    div.appendChild(img);
  }

  if(values.caption)
  {
    var span = document.createElement("span");
    for(var attr in spanAttr)
    {
      span.setAttribute(attr, spanAttr[attr]);
    }
    
    div.appendChild(span);
  }

  editor.insertNodeAtSelection(div);

/*
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

*/
};

OpenCCM.prototype.imageBrowse = function(window)
{
  this.dialogs["images"].getElementById(this.dialogs["images"].id["src"]).value = "/theme/mandalay/ccm/cms-service/stream/image/?image_id=9001";
  this.dialogs["images"].getElementById(this.dialogs["images"].id["width"]).value = "304";
  this.dialogs["images"].getElementById(this.dialogs["images"].id["height"]).value = "420";
  this.dialogs["images"].getElementById(this.dialogs["images"].id["name"]).value = "Schild.jpg";
};
