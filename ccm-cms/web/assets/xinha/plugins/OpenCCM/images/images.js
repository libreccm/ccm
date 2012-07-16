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
    image = editor.getParentElement();
    if ( image && image.tagName.toLowerCase() != "img" )
    {
      image = null;
    }
  }

  this.image = image;

  if(image && image.tagName.toLowerCase() == "img")
  {
    data.src    = this.editor.fixRelativeLinks(image.getAttribute("src"));
    data.alt    = image.getAttribute("alt");
    data.name   = image.getAttribute("name");
    data.title  = image.getAttribute("title");
    data.width  = image.getAttribute("width");
    data.height = image.getAttribute("height");

    if(image.parentNode.tagName.toLowerCase() == "div")
    {
      data.alignment = image.parentNode.getAttribute("class").substring(image.parentNode.getAttribute("class").indexOf(" "));
      data.caption = (image.nextSibling.tagName.toLowerCase() == "span") ? image.nextSibling.firstChild.nodeValue : "";
    }
    else
    {
      data.alignment = image.parentNode.parentNode.getAttribute("class").substring(image.parentNode.getAttribute("class").indexOf(" "));
      data.zoom = (image.parentNode.getAttribute("class") == "imageZoom") ? "on" : "";
      data.gallery = (image.parentNode.getAttribute("class") == "imageGallery") ? "on" : "";
//    data.galleryName = (image.parentNode.getAttribute("class") == "imageGallery") ? image.parentNode.getAttribute("rel") : "";

      data.caption = (image.parentNode.nextSibling.tagName.toLowerCase() == "span") ? image.parentNode.nextSibling.firstChild.nodeValue : "";
    }

    // Calculate aspect ratio
    data.aspect = data.width / data.height;
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
  dialog.getElementById("ok").onclick = function() {self.imageApply();}
  dialog.getElementById("remove").onclick = function() { self.imageRemove(); };
  dialog.getElementById("cancel").onclick = function() { self.dialogs["images"].hide()};

  // Connect the Select button
  dialog.getElementById("browse").onclick = function() { self.imageBrowse(window); };

  // Connect onkeyup event handler with dimension filed to recalculate the size according to aspect ratio
  dialog.getElementById("width").onkeyup = function() { self.calcHeight(); };
  dialog.getElementById("height").onkeyup = function() { self.calcWidth(); };

  // OnResize 
  this.dialogs["images"].onresize = function ()
  {
    var newHeightForPreview = 
    parseInt(this.height, 10) 
//    - this.getElementById("h1").offsetHeight 
//    - this.getElementById("buttons").offsetHeight
//    - this.getElementById("inputs").offsetHeight 
    - parseInt(this.rootElem.style.paddingBottom, 10);
    this.getElementById("preview").style.height = ((newHeightForPreview > 0) ? newHeightForPreview : 0) + "px";
    this.getElementById("preview").style.width = "98%";
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
  if(values.zoom || values.gallery)
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
  if(values.zoom || values.gallery)
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

OpenCCM.prototype.imageRemove = function() {
  
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
  this.imageSet({
      src    : "/theme/mandalay/ccm/cms-service/stream/image/?image_id=9001", 
      width  : "304",
      height : "420",
      name   : "Schild.jpg"
    });
};

OpenCCM.prototype.imageSet = function(imageData)
{
  var dialog = this.dialogs["images"];
  dialog.getElementById(dialog.id["src"]).value = imageData.src;
  dialog.getElementById(dialog.id["ipreview"]).src = imageData.src;
  dialog.getElementById(dialog.id["width"]).value = imageData.width;
  dialog.getElementById(dialog.id["height"]).value = imageData.height;
  dialog.getElementById(dialog.id["name"]).value = imageData.name;
  dialog.getElementById(dialog.id["aspect"]).value = dialog.getElementById(dialog.id["width"]).value / dialog.getElementById(dialog.id["height"]).value;
  if(imageData.src != "")
  {
    dialog.getElementById(dialog.id["preview"]).style.display = "block";
  }
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

