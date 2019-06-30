const ImageDialog = function (editor) {

    function getImageData(editor) {

        const elem = editor.selection.getNode();
        const imgDiv = editor.dom.getParent(elem, "div.image");
        const img = editor.dom.select("img", imgDiv)[0];
        if (imgDiv != null) {
          let imageData = {
            file: img.getAttribute("src"),
            width: img.getAttribute("width").slice(0, -2),
            height: img.getAttribute("height").slice(0, -2),
            alt: img.getAttribute("alt"),
            align: imgDiv.classList[1],
            fancy: imgDiv.childNodes[0].classList[0].slice(0, -1),
            title: imgDiv.childNodes[0].title,
            caption: imgDiv.childNodes[1].localName == "span",
            parent: imgDiv
          };
          console.log(imageData);
          return imageData;
        } else {
          return null;
        }
    }

    function open() {

        return tinymce.activeEditor.windowManager.open({
            title: "Insert/edit images",
            body: {
                type: "panel",
                items: [
                    {
                        type: "input",
                        name: "file",
                        label: "File",
                        disabled: true
                    }
                ]
            },
            buttons: []
        });

    //     const imageData = getImageData(editor);
    //     let image_name = "";
    
    //     // ================== File Chooser ====================
    //     let fileChooseContainer = new tinymce.ui.Container({
    //       type: "container",
    //       layout: "flex",
    //       direction: "row",
    //       align: "center",
    //       padding: 5,
    //       spacing: 15,
    //       margin: 5
    //     });
    
    //     let imagePathTextBox = new tinymce.ui.TextBox({
    //       name: "file",
    //       label: "File:",
    //       disabled: true
    //     });
    
    //     fileChooseContainer.add(imagePathTextBox);
    
    //     let browseButton = new tinymce.ui.Button({
    //       name: "browse_images",
    //       text: "Browse Images",
    //       onclick: function() {
    //         let baseURL = window.location.href;
    //         let offset = baseURL.lastIndexOf("/");
    //         let destURL = baseURL.slice(0, offset + 1) + "image_select.jsp";
    //         let selectWindow = window.open(
    //           destURL,
    //           "_blank",
    //           "scrollbars=yes,directories=no,toolbar=no,width=800,height=600,status=no,menubar=no"
    //         );
    //         window.openCCM = new Object();
    //         window.openCCM.imageSet = selectedImage => {
    //           imagePathTextBox.text(selectedImage.src);
    //           win
    //             .find("#file")
    //             .value(selectedImage.src)
    //             .fire("change");
    //           win
    //             .find("#width")
    //             .value(selectedImage.width)
    //             .fire("change");
    //           win
    //             .find("#height")
    //             .value(selectedImage.height)
    //             .fire("change");
    //           image_name = selectedImage.name;
    //           return true;
    //         };
    //       }
    //     });
    //     fileChooseContainer.add(browseButton);
    //     // ================== File Chooser ====================
    
    //     // ================== Alternate Text ==================
    //     let alternateTextBox = new tinymce.ui.TextBox({
    //       name: "alternate",
    //       label: "Alternate:"
    //     });
    //     // ================== Alternate Text ==================
    
    //     // ================== Title Text ======================
    //     let titleTextBox = new tinymce.ui.TextBox({
    //       name: "title",
    //       label: "Title:"
    //     });
    //     // ================== Title Text ======================
    
    //     // ================== Alignment =======================
    //     let alignmentContainer = new tinymce.ui.Container({
    //       type: "container",
    //       layout: "flex",
    //       direction: "row"
    //     });
    
    //     let alginLabel = new tinymce.ui.Label({
    //       text: "Alignment:"
    //     });
    
    //     let alignListBox = new tinymce.ui.ListBox({
    //       name: "alignment",
    //       values: [
    //         { text: "Not set", value: "" },
    //         { text: "Left", value: "left" },
    //         { text: "Center", value: "center" },
    //         { text: "Right", value: "right" }
    //       ]
    //     });
    
    //     alignmentContainer.add(alginLabel);
    //     alignmentContainer.add(alignListBox);
    //     // ================== Alignment =======================
    
    //     // ================== Fancy Box =======================
    //     let fancyBoxContainer = new tinymce.ui.Container({
    //       type: "container",
    //       layout: "flex",
    //       direction: "row"
    //     });
    
    //     let fancyBoxLabel = new tinymce.ui.Label({
    //       text: "Fancy Box:"
    //     });
    
    //     let fancyBoxListBox = new tinymce.ui.ListBox({
    //       name: "fancybox",
    //       values: [
    //         { text: "None", value: "" },
    //         { text: "Zoom", value: "imageZoom" },
    //         { text: "Gallery", value: "imageGallery" }
    //       ]
    //     });
    
    //     fancyBoxContainer.add(fancyBoxLabel);
    //     fancyBoxContainer.add(fancyBoxListBox);
    //     // ================== Fancy Box =======================
    
    //     // ================== Caption =========================
    //     let captionCheckBox = new tinymce.ui.Checkbox({
    //       label: "Caption:",
    //       name: "caption"
    //     });
    //     // ================== Caption =========================
    
    //     // ================== Dimension Box ===================
    //     let dimensionContainer = new tinymce.ui.Container({
    //       label: "Dimension",
    //       layout: "flex",
    //       direction: "row",
    //       align: "center",
    //       padding: 5,
    //       spacing: 15,
    //       margin: 5
    //     });
    
    //     let widthTextBox = new tinymce.ui.TextBox({
    //       name: "width",
    //       label: "Width"
    //     });
    
    //     let heightTextBox = new tinymce.ui.TextBox({
    //       name: "height",
    //       label: "Height"
    //     });
    
    //     dimensionContainer.add(widthTextBox);
    //     dimensionContainer.add({ type: "label", text: "X" });
    //     dimensionContainer.add(heightTextBox);
    //     // ================== Dimension Box ===================
    
    //     const win = editor.windowManager.open({
    //       title: "Insert/Modify Image",
    //       width: 800,
    //       height: 600,
    //       body: [
    //         fileChooseContainer,
    //         alternateTextBox,
    //         titleTextBox,
    //         alignmentContainer,
    //         fancyBoxContainer,
    //         captionCheckBox,
    //         dimensionContainer
    //       ],
    //       onsubmit: function() {
    //         let src = win.find("#file").value();
    //         let alternate = win.find("#alternate").value();
    //         let width = win.find("#width").value();
    //         let height = win.find("#height").value();
    //         let title = win.find("#title").value();
    //         let alignment = win.find("#alignment").value();
    //         let fancy_box = win.find("#fancybox").value();
    
    //         if (src != null) {
    //           let img =
    //             "<img src=" +
    //             src +
    //             ' alt="' +
    //             alternate +
    //             '" name="' +
    //             image_name +
    //             '" width="' +
    //             width +
    //             'px"' +
    //             ' height="' +
    //             height +
    //             'px"' +
    //             " />";
    
    //           let fancy_box_wrap =
    //             "<a class=" +
    //             fancy_box +
    //             '" href="' +
    //             src +
    //             '" title="' +
    //             title +
    //             '" data-mce-href="' +
    //             src +
    //             '"> ' +
    //             img +
    //             "</a>";
    //           let span = "";
    //           if (win.find("#caption").value()) {
    //             span =
    //               '<span class="caption" style="width: ' +
    //               width +
    //               'px;" data-mce-style="width: ' +
    //               width +
    //               'px;">' +
    //               image_name +
    //               "</span>";
    //           }
    //           let img_div =
    //             '<div class="image ' +
    //             alignment +
    //             '">' +
    //             fancy_box_wrap +
    //             span +
    //             "</div>";
    //           if (imageData != null) {
    //             editor.dom.replace(
    //               editor.dom.createFragment(img_div),
    //               imageData.parent
    //             );
    //           } else {
    //             editor.insertContent(img_div);
    //           }
    //         }
    //       }
    //     });
    //     // ================== Fill with selection =============
    //     if (imageData != null) {
    //       win
    //         .find("#file")
    //         .value(imageData.file)
    //         .fire("change");
    //       win
    //         .find("#alternate")
    //         .value(imageData.alt)
    //         .fire("change");
    //       win
    //         .find("#width")
    //         .value(imageData.width)
    //         .fire("change");
    //       win
    //         .find("#height")
    //         .value(imageData.height)
    //         .fire("change");
    //       if (imageData.align != undefined) {
    //         win
    //           .find("#alignment")
    //           .value(imageData.align)
    //           .fire("change");
    //       }
    //       if (imageData.fancy != undefined) {
    //         win
    //           .find("#fancybox")
    //           .value(imageData.fancy)
    //           .fire("change");
    //       }
    //       win
    //         .find("#title")
    //         .value(imageData.title)
    //         .fire("change");
    //       win
    //         .find("#caption")
    //         .value(imageData.caption)
    //         .fire("change");
    //     }
    //     // ================== Fill with selection =============
    }
    
      return {
        open
      };
}

const ccmCmsImages = function (editor, url) {

    editor.ui.registry.addButton("ccm-cms-images",
        {
            //text: "Insert/Edit image",
            icon: "image",
            tooltip: "Insert/Edit images",
            onAction: ImageDialog(editor).open,
            stateSelector: "div.image"
        });

    editor.ui.registry.addMenuItem("ccm-cms-images",
        {
            icon: "image",
            text: "Insert/Edit Images",
            onAction: ImageDialog(editor).open,
            stateSelector: "image",
            context: "insert",
            prependToContext: true
        });
}

tinymce.PluginManager.add("ccm-cms-images", ccmCmsImages);