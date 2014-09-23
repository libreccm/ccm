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
    alignment   : "",
    caption     : "",
    fancybox    : "",
    aspect      : ""
  };

  if (typeof image == "undefined" )
  {
    image = editor.getParentElement();
    if ( image && image.tagName.toLowerCase() != "img" )
    {
      image = null;
    }
  }

  this.image = image;

  if (image && image.tagName.toLowerCase() == "img")
  {
    data.src    = this.editor.fixRelativeLinks(image.getAttribute("src"));
    data.alt    = image.getAttribute("alt");
    data.name   = image.getAttribute("name");
    data.title  = image.getAttribute("title");
    data.width  = image.getAttribute("width");
    data.height = image.getAttribute("height");

    if (image.parentNode.tagName.toLowerCase() == "div")
    {
      // Parent node is not a link, so there is not zoom or gallery function
      alignment = image.parentNode.getAttribute("class");
      if (alignment != "image none")
      {
        data.alignment = alignment.substring(alignment.indexOf(" ") + 1);
      }
      else
      {
        data.alignment = "none";
      }
      data.caption = (image.nextSibling.tagName.toLowerCase() == "span" && image.nextSibling.firstChild) ? image.nextSibling.firstChild.nodeValue : "";

      data.fancybox = "none";
//      data.galleryName = "";
    }
    else
    {
      // Parent node is a link
      alignment = image.parentNode.parentNode.getAttribute("class");
      if (alignment != "image none")
      {
        data.alignment = alignment.substring(alignment.indexOf(" ") + 1);
      }
      else
      {
        data.alignment="none";
      }

      data.fancybox = image.parentNode.getAttribute("class");
//    data.galleryName = (image.parentNode.getAttribute("class") == "imageGallery") ? image.parentNode.getAttribute("rel") : "";

      data.caption = (image.parentNode.nextSibling.tagName.toLowerCase() == "span" && image.parentNode.nextSibling.firstChild) ? image.parentNode.nextSibling.firstChild.nodeValue : "";
    }
    
    // Calculate aspect ratio
    data.aspect = data.width / data.height;
  }
  else
  {
    data.alignment = "none";
//    data.caption = "";
    data.fancybox = "none";
//    data.galleryName = "";
  }
  
  // now calling the show method of the Xinha.Dialog object to set the values and show the actual dialog
  this.dialogs["images"].show(data);

  // If image set, show preview
  if(data.src != "")
  {
    this.dialogs["images"].getElementById(this.dialogs["images"].id["ipreview"]).src = data.src;
    this.resizePreview(this.dialogs["images"]);
    this.dialogs["images"].getElementById(this.dialogs["images"].id["preview"]).style.display = "block";
  }
  else
  {
    // Preview dekativieren
    this.dialogs["images"].getElementById(this.dialogs["images"].id["ipreview"]).src = "about:blank";
    this.dialogs["images"].getElementById(this.dialogs["images"].id["preview"]).style.display = "none";
  }

};

OpenCCM.prototype.prepareImageDialog = function()
{
  var self = this;
  var editor = this.editor;
  
  var dialog = this.dialogs["images"] = new Xinha.Dialog(editor, OpenCCM.imagesHtml, 'Xinha',{width:410})

  // Connect the OK and Cancel buttons
  dialog.getElementById("ok").onclick = function() {self.imageApply();}
  dialog.getElementById("remove").onclick = function() { self.imageRemove(); };
  dialog.getElementById("cancel").onclick = function() { self.dialogs["images"].hide(); };

  // Connect the Select button
  dialog.getElementById("browse").onclick = function() { self.imageBrowse(window); };

  // Connect onkeyup event handler with dimension filed to recalculate the size according to aspect ratio
  dialog.getElementById("width").onkeyup = function() { self.calcHeight(); };
  dialog.getElementById("height").onkeyup = function() { self.calcWidth(); };

  // OnResize 
  this.dialogs["images"].onresize = function ()
  {
    self.resizeDialog(this);
  };

  this.imageDialogReady = true;
};

// Write HTML code
OpenCCM.prototype.imageApply = function()
{

  var values = this.dialogs["images"].hide();
  var image  = this.image;
  var editor = this.editor;
  var modifyImage = (image != null && image.tagName.toLowerCase() == "img");
  
  var imgAttr =
  {
    src    : "",
    alt    : "",
    title  : "",
    name   : "",
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
	title : "",
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
  imgAttr.name = values.name;
  imgAttr.title = values.title;
  imgAttr.width = values.width;
  imgAttr.height = values.height;

  // Read form values for caption
  if(values.caption)
  {
    spanAttr.style = "width:" + imgAttr.width + "px";
  }

  // Read form values for link
  if(values.fancybox.value == "imageZoom")
  {
    linkAttr.href = values.src;
	linkAttr.title = values.title;
    linkAttr.rel = "";
    linkAttr.class = "imageZoom";
  }
    
  else if(values.fancybox.value == "imageGallery")
  {
    linkAttr.href = values.src;
	linkAttr.title = values.title;
    linkAttr.rel = "imageGallery";
    linkAttr.class = "imageGallery";
  }

  // Read form values for div
  if(values.alignment.value != "")
  {
    divAttr.class += " " + values.alignment.value;
  }

  var div;

  // Modify Image
  if(modifyImage)
  {
    // Get the parent for img node
    div = image.parentNode;
    
    // if the tagname of div is "a"
    if(div.tagName.toLowerCase() == "a")
    {
      // go up one level to find the real div
      div = div.parentNode;
    }
    
    // remove all childs from div.image
    while (div.childNodes.length > 0)
    {
      div.removeChild(div.firstChild);
    }
  }
  
  // Add Image
  else
  {
    // create new div.image
    div = document.createElement("div");
  }

  // set attributes for div
  for(var attr in divAttr)
  {
    div.setAttribute(attr, divAttr[attr]);
  }

  // the folling has to be done for both cases
  // insert link, if fancybox features are activated
  if(values.fancybox.value != "none")
  {
    link = document.createElement("a");
    for(var attr in linkAttr)
    {
      link.setAttribute(attr, linkAttr[attr]);
    }
    div.appendChild(link);
  }

  // insert the image, obviously
  var img = document.createElement("img");
  for(var attr in imgAttr)
  {
    img.setAttribute(attr, imgAttr[attr]);
  }
  if(values.fancybox.value != "none")
  {
    link.appendChild(img);
  }
  else
  {
    div.appendChild(img);
  }

  // insert caption if selected
  if(values.caption)
  {
    var span = document.createElement("span");
    for(var attr in spanAttr)
    {
      span.setAttribute(attr, spanAttr[attr]);
    }
    span.appendChild(document.createTextNode(imgAttr.title));
    
    div.appendChild(span);
  }

  // insert new div only if we not modifying an image
  if(!modifyImage) {
    editor.insertNodeAtSelection(div);
  }

  // set cursor after div
  this.editor.selectNodeContents(div.nextSibling, 0);
};

OpenCCM.prototype.imageRemove = function()
{
  
  // Close the dialog
  this.dialogs["images"].hide();
  
  // Get the parent div for img node
  var div = this.image.parentNode;
  
  // if the tagname of div is "a"
  if(div.tagName.toLowerCase() == "a")
  {
    // go up one level to find the real div
    div = div.parentNode;
  }
  
  // remove this div
  div.parentNode.removeChild(div);
  
  return true;
};

OpenCCM.prototype.imageBrowse = function(window)
{
  var baseURL = window.location.href;
  var offset  = baseURL.lastIndexOf("/");
  var destURL = baseURL.slice(0, offset+1) + "image_select.jsp";

//  var searchDialog = new Xinha.Dialog(this.editor, destURL, '');//,{width:800, height:600}, {'closable':true});
//  searchDialog.show();
  var selectWindow = window.open(destURL, "_blank", "scrollbars=yes,directories=no,toolbar=no,width=800,height=600,status=no,menubar=no");
  window.openCCM = this;
  return false;
};

OpenCCM.prototype.imageSet = function(imageData)
{
  var dialog = this.dialogs["images"];
  dialog.getElementById(dialog.id["src"]).value = imageData.src;
  dialog.getElementById(dialog.id["ipreview"]).src = imageData.src;
  dialog.getElementById(dialog.id["width"]).value = imageData.width;
  dialog.getElementById(dialog.id["height"]).value = imageData.height;
  dialog.getElementById(dialog.id["name"]).value = imageData.name;
  dialog.getElementById(dialog.id["aspect"]).value = imageData.width / imageData.height;

  if(imageData.src != "")
  {
//    this.resizePreview(dialog);
    dialog.getElementById(dialog.id["preview"]).style.display = "block";
    this.resizeDialog(dialog);
  } else {
    dialog.getElementById(dialog.id["preview"]).style.display = "none";
  }
};

OpenCCM.prototype._getCombinedComponentHeight = function()
{
  var dialog = this.dialogs["images"];
  return  dialog.getElementById(dialog.id["h1"]).offsetHeight +
          dialog.getElementById(dialog.id["common"]).offsetHeight +
          dialog.getElementById(dialog.id["layout"]).offsetHeight +
          dialog.getElementById(dialog.id["dimensions"]).offsetHeight +
          dialog.getElementById(dialog.id["buttons"]).offsetHeight +
          parseInt(dialog.rootElem.style.paddingBottom,10) +
          10;
};

OpenCCM.prototype.resizeDialog = function(dialog)
{
  if(dialog.getElementById(dialog.id["preview"]).style.display == "block")
  {


    // Recalculate height of preview
    dialog.getElementById(dialog.id["preview"]).style.height = Math.min(Math.max(0, dialog.height - this._getCombinedComponentHeight() - 20), Math.round((window.innerHeight * 0.8) - this._getCombinedComponentHeight())) + "px";
    // Resize preview image
    this.resizePreview(dialog);
  }
  dialog.width  = Math.min(Math.max(10, dialog.width), Math.round((window.innerWidth * 0.8)));
  dialog.height = Math.min(Math.max(10, dialog.height), Math.round((window.innerHeight * 0.8)));
};

OpenCCM.prototype.resizePreview = function(dialog)
{
  var layoutElem = dialog.getElementById(dialog.id["layout"]);
  var previewElem = dialog.getElementById(dialog.id["preview"]);
  
  var maxWidth = layoutElem.offsetWidth - 5;
  var maxHeight = previewElem.offsetHeight && previewElem.offsetHeight - 15 < Math.round((window.innerHeight * 0.8) - this._getCombinedComponentHeight()) 
                    ? previewElem.offsetHeight - 15
                    : Math.round((window.innerHeight * 0.8) - this._getCombinedComponentHeight());

  dialog.getElementById(dialog.id["ipreview"]).style.width = "auto";
  dialog.getElementById(dialog.id["ipreview"]).style.height = "auto";

  var width = parseInt(dialog.getElementById(dialog.id["ipreview"]).width,10);
  var height = parseInt(dialog.getElementById(dialog.id["ipreview"]).height,10);

// alert("W: " + maxWidth +" "+ width + "H: " + maxHeight +" "+ height);

  var zoom = height > 0
                ? Math.min(maxWidth / width, maxHeight / height)
                : maxWidth / width;
  
// alert("Zoom: " + zoom);
  
  var w = dialog.getElementById(dialog.id["ipreview"]).style.width = Math.round(width * zoom) + "px";
  var h = dialog.getElementById(dialog.id["ipreview"]).style.height = Math.round(height * zoom) + "px";
};

OpenCCM.prototype.calcWidth = function()
{
    var dialog = this.dialogs["images"];
    dialog.getElementById(dialog.id["width"]).value = 
      Math.round(dialog.getElementById("height").value * dialog.getElementById("aspect").value);
};

OpenCCM.prototype.calcHeight = function()
{
    var dialog = this.dialogs["images"];
    dialog.getElementById(dialog.id["height"]).value = 
      Math.round(dialog.getElementById("width").value / dialog.getElementById("aspect").value);
};

