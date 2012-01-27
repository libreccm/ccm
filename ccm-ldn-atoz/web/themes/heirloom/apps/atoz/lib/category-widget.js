      // Overrides the version from ccm-cms. This behaves as a single-selection
      // widget. It always stores the selection in slot 0, and makes sure the
      // previous selection is made clickable again.
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
          if (elWidget.options[0] != null) {
	        var prevId = elWidget.options[0].value;
            var elLink = $("catLn"+prevId);
            var elName = $("catNm"+prevId);
            elLink.style.display="inline";
            elName.style.display="none";
	      }

          var opt = new Option(name, id);
          opt.onclick = "function() { catDeselect('" + id + "'); }";
          elWidget.options[0] = opt; 
          var optHidden = new Option(name, id, false, true);
          elWidgetHidden.options[0] = optHidden;
        }

        var elLink = $("catLn"+id);
        var elName = $("catNm"+id);

        elLink.style.display="none";
        elName.style.display="inline";
        return false;
      }
