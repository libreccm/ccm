      function catBranchToggle(id) {
        var elImg = $("catTog"+id);
        var elChildren = $("catCh"+id);

        if (elChildren.style.display != "block") {
           elChildren.innerHTML = 'loading...'; // discard the branch
           elChildren.style.display = "block";
           // reload the branch
           new Ajax.Updater('catCh'+id, 'load-cat.jsp',
                {parameters:'nodeID='+id});
           elImg.src = "/assets/action-delete.png";
           // TODO check catWd, update selected catLn/catNm
           // in case selected cats are in reloaded branch
        } else {
           elChildren.style.display = "none";
           elChildren.innerHTML = ''; // discard the branch
           elImg.src = "/assets/action-add.png";
        }
        return false;
      }
      function catToggle(id) {
        var elImg = $("catTog"+id);
        var elChildren = $("catCh"+id);

        if (elChildren.style.display != "block") {
          elChildren.style.display = "block";
          elImg.src = "/assets/action-delete.png";
        } else {
          elChildren.style.display = "none";
          elImg.src = "/assets/action-add.png";
        }
        return false;
      }
      function catSelect(id, name) {
        var elWidget = $("catWd");
        var elWidgetHidden = $("catWdHd");
        var found = 0;
        for (var i = 0 ; i < elWidget.options.length ; i++) {
          if (elWidget.options[i].value == id) {
            found = 1;
          }
        }
        if (!found) {
          var opt = new Option(name, id);
          opt.onclick = "function() { catDeselect('" + id + "'); }";
          elWidget.options[elWidget.options.length] = opt; 
          var optHidden = new Option(name, id, false, true);
          elWidgetHidden.options[elWidgetHidden.options.length] = optHidden;
        }

        var elLink = $("catLn"+id);
        var elName = $("catNm"+id);

        elLink.style.display="none";
        elName.style.display="inline";
        return false;
      }
      function catDeselect() {
        var elWidget = $("catWd");
        var elWidgetHidden = $("catWdHd");
        var idx = elWidget.selectedIndex;
        if (idx  != -1) {
            var id = elWidget.options[idx].value;
            var elLink = $("catLn"+id);
            var elName = $("catNm"+id);
            elLink.style.display="inline";
            elName.style.display="none";
            elWidget.options[idx] = null;
            elWidgetHidden.options[idx] = null;
        }
        return false;
      }
