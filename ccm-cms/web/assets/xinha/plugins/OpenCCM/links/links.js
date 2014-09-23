OpenCCM.prototype.showLinkDialog = function(link)
{
    if (!this.dialogs["links"])
    {
        this.prepareLinkDialog();
    }

    if (!link && this.editor.selectionEmpty(this.editor.getSelection()))
    {
        alert(this._lc("You need to select some text before creating a link"));
        return false;
    }

    var editor = this.editor;
    this.link = link;

    var data =
            {
                title: '',
                type: 'internal',
                ci_name: '',
                ci_href: '',
                href: ''
            };

    if (link && link.tagName.toLowerCase() == 'a')
    {
        var href = this.editor.fixRelativeLinks(link.getAttribute('href'));
        var internal = href.match(/^(\/.*)$/);
        var external = href.match(/^http(.*)$/);
        /*
         var mailto = href.match(/^mailto:(.*@[^?&]*)(\?(.*))?$/);
         var anchor = href.match(/^#(.*)$/);
         */
        data.title = link.title;

        if (internal)
        {
            data.type = 'internal';
            data.ci_name = link.alt;
            data.ci_href = href;
        }

        if (external)
        {
            data.type = 'external';
            data.href = href;
        }

    }

    // now calling the show method of the Xinha.Dialog object to set the values and show the actual dialog
    this.dialogs["links"].show(data);

};

// Prepare the link dialog
OpenCCM.prototype.prepareLinkDialog = function()
{
    var self = this;
    var editor = this.editor;

    var dialog = this.dialogs["links"] = new Xinha.Dialog(editor, OpenCCM.linksHtml, 'Xinha', {width: 400})

    // Connect the OK and Cancel buttons
    dialog.getElementById('ok').onclick = function() {
        self.linkApply();
    };
    dialog.getElementById('clear').onclick = function() {
        self.linkRemove();
    };
    dialog.getElementById('cancel').onclick = function() {
        self.dialogs["links"].hide()
    };

    // Connect the Browse and Search button
    dialog.getElementById('ci_browse').onclick = function() {
        self.linkBrowse(window);
    };
    /* Suche deaktivert, bis eine bessere JSP / Java-Klasse vorhanden ist
     dialog.getElementById('ci_search').onclick = function() { alert("Das geht auch noch nicht.");};
     */

    this.linkDialogReady = true;
};

// 
OpenCCM.prototype.linkApply = function()
{

    var values = this.dialogs["links"].hide();
    var link = this.link;
    var editor = this.editor;

    var linkAttr =
            {
                href: '',
                alt: '',
                title: '',
                class: ''
            };

    // Read needed form values
    switch (values.type)
    {
        case "internal":  //alert("Internal");
            linkAttr.href = values.ci_href;
            /* Deaktiviert bis es eine bessere JSP / Java-Klasse gibt
             linkAttr.alt   = values.ci_name;
             */
            linkAttr.title = values.title;
            linkAttr.class = "linkInternal";
            break;

        case "external":  //alert("External");
            linkAttr.href = values.href;
            linkAttr.title = values.title;
            linkAttr.class = "linkExternal";

            // Make absolute url

            break;

        default:          //alert("Mist");
            return false;
    }

    // If not all mandatory informations are set
    if (linkAttr.href == "" || linkAttr.title == "")
    {
        // don't do anything at all
        return false;
    }

    // Modify Link
    if (link && link.tagName.toLowerCase() == "a")
    {

        for (var i in linkAttr)
        {
            link.setAttribute(i, linkAttr[i]);
        }

        /* from Linker-Plugin
         // If we change a mailto link in IE for some hitherto unknown
         // reason it sets the innerHTML of the link to be the
         // href of the link.  Stupid IE.
         if(Xinha.is_ie)
         {
         if(/mailto:([^?<>]*)(\?[^<]*)?$/i.test(link.innerHTML))
         {
         link.innerHTML = RegExp.$1;
         }
         }
         */

    }
    // Add Link
    else
    {

        // Insert a link, we let the editor do this, we figure it knows best
        var tmp = Xinha.uniq("http://www.example.com/Link");
        editor._doc.execCommand("createlink", false, tmp);

        // Fix it up
        var anchors = editor._doc.getElementsByTagName('a');
        for (var i = 0; i < anchors.length; i++)
        {
            var anchor = anchors[i];
            if (anchor.href == tmp)
            {
                // Found one.
                if (!link)
                    link = anchor;
                for (var j in linkAttr)
                {
                    anchor.setAttribute(j, linkAttr[j]);
                }
            }
        }

    }

    editor.selectNodeContents(link);
    editor.updateToolbar();
};

// Remove a link
OpenCCM.prototype.linkRemove = function()
{

    var values = this.dialogs["links"].hide();
    var link = this.link;
    var editor = this.editor;

    if (link && link.tagName.toLowerCase() == 'a')
    {
        if (confirm(this._lc('Are you sure you wish to remove this link?')))
        {
            var parent = link.parentNode;
            while (link.hasChildNodes())
            {
                parent.insertBefore(link.removeChild(link.childNodes[0]), link);
            }

            parent.removeChild(link);
            editor.updateToolbar();
            return true;
        }
    }
};

OpenCCM.prototype.linkBrowse = function(window)
{
    var baseURL = window.location.href;
    var offset = baseURL.lastIndexOf("/");
    var destURL = baseURL.slice(0, offset + 1) + "search.jsp?useURL=true&widget=getElementById('" + this.dialogs["links"].id["ci_href"] + "')";

//  var searchDialog = new Xinha.Dialog(this.editor, destURL, '');//,{width:800, height:600}, {'closable':true});
//  searchDialog.show();
    window.open(destURL, "_blank", "scrollbars=yes,directories=no,toolbar=no,width=960,height=600,status=no,menubar=no");

    return false;
}

// Get selected anchor
OpenCCM.prototype._getSelectedAnchor = function()
{
    var sel = this.editor.getSelection();
    var rng = this.editor.createRange(sel);
    var link = this.editor.activeElement(sel);

    if (link != null && link.tagName.toLowerCase() == 'a')
    {
        return link;
    }
    else
    {
        link = this.editor._getFirstAncestor(sel, 'a');
        if (link != null)
        {
            return link;
        }
    }
    return null;
};
