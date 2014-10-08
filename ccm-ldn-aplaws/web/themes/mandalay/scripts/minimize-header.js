/**
JavaScript for minimizing/toggling the header image.

Uses JQuery and the JQuery cookie plugin
*/

//Create namespace to avoid possible naming conflicts
var minimizeImage = {};

/**
This function is called using the onClick attribute of the image toogle link.

@param imageClass The (css) class of the div encapsulating the img element
@param linkClass The (css) class assigned to the linkClass
@param minimizeLabel Text to show for the minimize link (image is maximized)
@param maximizeLabel Text to show for the maximize link (image is minimized)
*/
minimizeImage.minimize = function (imageClass,
                                   linkClass,
                                   minimizeLabel,
                                   maximizeLabel) {

   if ($("." + imageClass + " img.minimized").css("display") == "none") {
        //Image is currently maximized, minimized image is not visible
       
        //Display minimized image
        $("." + imageClass + " img.minimized").css("display", "inline");
        //Hide maximized image
        $("." + imageClass + " img.maximized").css("display", "none");
        
        //Toogle link text to maximizeLabel 
        $("." + linkClass).removeClass("maximized");
        $("." + linkClass).addClass("minimized");
        $("." + linkClass).text(maximizeLabel);
        
        //Store status in cookie
        $.cookie(imageClass + "Status", 'minimized', { path: "/" });
   } else {   
        //Image is currently minimized.        
        //Hide minimized image
        $("." + imageClass + " img.minimized").css("display", "none");
        //Show maximized image
        $("." + imageClass + " img.maximized").css("display", "inline");
        
        //Toogle link text to minimizeLabel 
        $("." + linkClass).removeClass("minimized");
        $("." + linkClass).addClass("maximized");
        $("." + linkClass).text(minimizeLabel);
        
        //Store status in cookie
        $.cookie(imageClass + "Status", 'maximized', { path: "/" });
   }
   
   return false;
}

/**
This function is called by the JavaScript block inserted after the link when the
HTML document is loaded (using JQuery $(document).ready). It restores the 
status from a previous visit on the site.
*/
minimizeImage.restore = function(imageClass, 
                                 linkClass,
                                 minimizeLabel,
                                 maximizeLabel) {

    //alert("restoring!");

    //var status = $.cookie(imageClass + "Status", { path: "/" });
    var status = $.cookie(imageClass + "Status", String);

    //alert("status = " + status);
    
    if (status == 'minimized') {
        $("." + imageClass + " img.minimized").css("display", "inline");
        $("." + imageClass + " img.maximized").css("display", "none");
        
        $("." + linkClass).removeClass("maximized");
        $("." + linkClass).addClass("minimized");
        $("." + linkClass).text(maximizeLabel);
        
    } else {
        $("." + imageClass + " img.minimized").css("display", "none");
        $("." + imageClass + " img.maximized").css("display", "inline");
        
        $("." + linkClass).removeClass("minimized");
        $("." + linkClass).addClass("maximized");
        $("." + linkClass).text(minimizeLabel);
    }

    //alert(imageClass + "Status = " + $.cookie(imageClass + "Status"));
}
