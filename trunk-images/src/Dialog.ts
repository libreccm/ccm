declare var tinymce: any;

const open = function(editor) {
  let image_name = "";

  // ================== File Chooser ====================
  let fileChooseContainer = new tinymce.ui.Container({
    type: "container",
    layout: "flex",
    direction: "row",
    align: "center",
    padding: 5,
    spacing: 15,
    margin: 5
  });

  let imagePathTextBox = new tinymce.ui.TextBox({
    name: "file",
    label: "File:",
    disabled: true
  });

  fileChooseContainer.add(imagePathTextBox);

  let browseButton = new tinymce.ui.Button({
    name: "browse_images",
    text: "Browse Images",
    onclick: function() {
      let baseURL = window.location.href;
      let offset = baseURL.lastIndexOf("/");
      let destURL = baseURL.slice(0, offset + 1) + "image_select.jsp";
      let selectWindow = window.open(
        destURL,
        "_blank",
        "scrollbars=yes,directories=no,toolbar=no,width=800,height=600,status=no,menubar=no"
      );
      (<any>window).openCCM = new Object();
      (<any>window).openCCM.imageSet = selectedImage => {
        imagePathTextBox.text(selectedImage.src);
        win
          .find("#file")
          .value(selectedImage.src)
          .fire("change");
        win
          .find("#width")
          .value(selectedImage.width)
          .fire("change");
        win
          .find("#height")
          .value(selectedImage.height)
          .fire("change");
        image_name = selectedImage.name;
        return true;
      };
    }
  });
  fileChooseContainer.add(browseButton);
  // ================== File Chooser ====================

  // ================== Alternate Text ==================
  let alternateTextBox = new tinymce.ui.TextBox({
    name: "alternate",
    label: "Alternate:"
  });
  // ================== Alternate Text ==================

  // ================== Title Text ======================
  let titleTextBox = new tinymce.ui.TextBox({
    name: "title",
    label: "Title:"
  });
  // ================== Title Text ======================

  // ================== Alignment =======================
  let alignmentContainer = new tinymce.ui.Container({
    type: "container",
    layout: "flex",
    direction: "row"
  });

  let alginLabel = new tinymce.ui.Label({
    text: "Alignment:"
  });

  let alignListBox = new tinymce.ui.ListBox({
    name: "alignment",
    values: [
      { text: "Not set", value: "" },
      { text: "Left", value: "left" },
      { text: "Center", value: "center" },
      { text: "Right", value: "right" }
    ]
  });

  alignmentContainer.add(alginLabel);
  alignmentContainer.add(alignListBox);
  // ================== Alignment =======================

  // ================== Fancy Box =======================
  let fancyBoxContainer = new tinymce.ui.Container({
    type: "container",
    layout: "flex",
    direction: "row"
  });

  let fancyBoxLabel = new tinymce.ui.Label({
    text: "Fancy Box:"
  });

  let fancyBoxListBox = new tinymce.ui.ListBox({
    name: "fancybox",
    values: [
      { text: "None", value: "" },
      { text: "Zoom", value: "imageZoom" },
      { text: "Gallery", value: "imageGallery" }
    ]
  });

  fancyBoxContainer.add(fancyBoxLabel);
  fancyBoxContainer.add(fancyBoxListBox);
  // ================== Fancy Box =======================

  // ================== Caption =========================
  let captionCheckBox = new tinymce.ui.Checkbox({
    label: "Caption:",
    name: "caption"
  });
  // ================== Caption =========================

  // ================== Dimension Box ===================
  let dimensionContainer = new tinymce.ui.Container({
    label: "Dimension",
    layout: "flex",
    direction: "row",
    align: "center",
    padding: 5,
    spacing: 15,
    margin: 5
  });

  let widthTextBox = new tinymce.ui.TextBox({
    name: "width",
    label: "Width"
  });

  let heightTextBox = new tinymce.ui.TextBox({
    name: "height",
    label: "Height"
  });

  dimensionContainer.add(widthTextBox);
  dimensionContainer.add({ type: "label", text: "X" });
  dimensionContainer.add(heightTextBox);
  // ================== Dimension Box ===================

  const win = editor.windowManager.open({
    title: "Insert/Modify Image",
    width: 800,
    height: 600,
    body: [
      fileChooseContainer,
      alternateTextBox,
      titleTextBox,
      alignmentContainer,
      fancyBoxContainer,
      captionCheckBox,
      dimensionContainer
    ],
    onsubmit: function() {
      let src = win.find("#file").value();
      let alternate = win.find("#alternate").value();
      let width = win.find("#width").value();
      let height = win.find("#height").value();
      let title = win.find("#title").value();
      let alignment = win.find("#alignment").value();
      let fancy_box = win.find("#fancybox").value();

      if (src != null) {
        let img =
          "<img src=" +
          src +
          ' alt="' +
          alternate +
          '" name="' +
          image_name +
          '" width="' +
          width +
          'px"' +
          ' height="' +
          height +
          'px"' +
          " />";

        let fancy_box_wrap =
          "<a class=" +
          fancy_box +
          '" href="' +
          src +
          '" title="' +
          title +
          '" data-mce-href="' +
          src +
          '"> ' +
          img +
          "</a>";
        let span = "";
        if (win.find("#caption").value()) {
          span =
            '<span class="caption" style="width: ' +
            width +
            'px;" data-mce-style="width: ' +
            width +
            'px;">' +
            image_name +
            "</span>";
        }
        let img_div =
          '<div class="image ' +
          alignment +
          '">' +
          fancy_box_wrap +
          span +
          "</div>";
        editor.insertContent(img_div);
      }
    }
  });
};

export default {
  open
};
