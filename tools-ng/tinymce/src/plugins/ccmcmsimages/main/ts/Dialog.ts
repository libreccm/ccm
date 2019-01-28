declare var tinymce: any;
declare var window: any;

export default function (editor) {
  function getImageData(editor) {
    const elem = editor.selection.getNode();
    const imgDiv = editor.dom.getParent(elem, 'div.image');
    const img = editor.dom.select('img', imgDiv)[0];
    if (imgDiv != null) {
      const imageData = {
        file: img.getAttribute('src'),
        width: img.getAttribute('width').slice(0, -2),
        height: img.getAttribute('height').slice(0, -2),
        alt: img.getAttribute('alt'),
        align: imgDiv.classList[1],
        fancy: imgDiv.childNodes[0].classList[0].slice(0, -1),
        title: imgDiv.childNodes[0].title,
        caption: imgDiv.childNodes[1].localName === 'span',
        parent: imgDiv
      };
      return imageData;
    } else {
      return null;
    }
  }

  function open() {
    const imageData = getImageData(editor);
    let image_name = '';

    // ================== File Chooser ====================
    const fileChooseContainer = new tinymce.ui.Container({
      type: 'container',
      layout: 'flex',
      direction: 'row',
      align: 'center',
      padding: 5,
      spacing: 15,
      margin: 5
    });

    const imagePathTextBox = new tinymce.ui.TextBox({
      name: 'file',
      label: 'File:',
      disabled: true
    });

    fileChooseContainer.add(imagePathTextBox);

    const browseButton = new tinymce.ui.Button({
      name: 'browse_images',
      text: 'Browse Images',
      onclick() {
        const baseURL = window.location.href;
        const offset = baseURL.lastIndexOf('/');
        const destURL = baseURL.slice(0, offset + 1) + 'image_select.jsp';
        window.open(
          destURL,
          '_blank',
          'scrollbars=yes,directories=no,toolbar=no,width=800,height=600,status=no,menubar=no'
        );
        (<any> window).openCCM = new Object();
        (<any> window).openCCM.imageSet = (selectedImage) => {
          imagePathTextBox.text(selectedImage.src);
          win
            .find('#file')
            .value(selectedImage.src)
            .fire('change');
          win
            .find('#width')
            .value(selectedImage.width)
            .fire('change');
          win
            .find('#height')
            .value(selectedImage.height)
            .fire('change');
          image_name = selectedImage.name;
          return true;
        };
      }
    });
    fileChooseContainer.add(browseButton);
    // ================== File Chooser ====================

    // ================== Alternate Text ==================
    const alternateTextBox = new tinymce.ui.TextBox({
      name: 'alternate',
      label: 'Alternate:'
    });
    // ================== Alternate Text ==================

    // ================== Title Text ======================
    const titleTextBox = new tinymce.ui.TextBox({
      name: 'title',
      label: 'Title:'
    });
    // ================== Title Text ======================

    // ================== Alignment =======================
    const alignmentContainer = new tinymce.ui.Container({
      type: 'container',
      layout: 'flex',
      direction: 'row'
    });

    const alginLabel = new tinymce.ui.Label({
      text: 'Alignment:'
    });

    const alignListBox = new tinymce.ui.ListBox({
      name: 'alignment',
      values: [
        { text: 'Not set', value: '' },
        { text: 'Left', value: 'left' },
        { text: 'Center', value: 'center' },
        { text: 'Right', value: 'right' }
      ]
    });

    alignmentContainer.add(alginLabel);
    alignmentContainer.add(alignListBox);
    // ================== Alignment =======================

    // ================== Fancy Box =======================
    const fancyBoxContainer = new tinymce.ui.Container({
      type: 'container',
      layout: 'flex',
      direction: 'row'
    });

    const fancyBoxLabel = new tinymce.ui.Label({
      text: 'Fancy Box:'
    });

    const fancyBoxListBox = new tinymce.ui.ListBox({
      name: 'fancybox',
      values: [
        { text: 'None', value: '' },
        { text: 'Zoom', value: 'imageZoom' },
        { text: 'Gallery', value: 'imageGallery' }
      ]
    });

    fancyBoxContainer.add(fancyBoxLabel);
    fancyBoxContainer.add(fancyBoxListBox);
    // ================== Fancy Box =======================

    // ================== Caption =========================
    const captionCheckBox = new tinymce.ui.Checkbox({
      label: 'Caption:',
      name: 'caption'
    });
    // ================== Caption =========================

    // ================== Dimension Box ===================
    const dimensionContainer = new tinymce.ui.Container({
      label: 'Dimension',
      layout: 'flex',
      direction: 'row',
      align: 'center',
      padding: 5,
      spacing: 15,
      margin: 5
    });

    const widthTextBox = new tinymce.ui.TextBox({
      name: 'width',
      label: 'Width'
    });

    const heightTextBox = new tinymce.ui.TextBox({
      name: 'height',
      label: 'Height'
    });

    dimensionContainer.add(widthTextBox);
    dimensionContainer.add({ type: 'label', text: 'X' });
    dimensionContainer.add(heightTextBox);
    // ================== Dimension Box ===================

    const win = editor.windowManager.open({
      title: 'Insert/Modify Image',
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
      onsubmit() {
        const src = win.find('#file').value();
        const alternate = win.find('#alternate').value();
        const width = win.find('#width').value();
        const height = win.find('#height').value();
        const title = win.find('#title').value();
        const alignment = win.find('#alignment').value();
        const fancy_box = win.find('#fancybox').value();

        if (src != null) {
          const img =
            '<img src=' +
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
            ' />';

          const fancy_box_wrap =
            '<a class=' +
            fancy_box +
            '" href="' +
            src +
            '" title="' +
            title +
            '" data-mce-href="' +
            src +
            '"> ' +
            img +
            '</a>';
          let span = '';
          if (win.find('#caption').value()) {
            span =
              '<span class="caption" style="width: ' +
              width +
              'px;" data-mce-style="width: ' +
              width +
              'px;">' +
              image_name +
              '</span>';
          }
          const img_div =
            '<div class="image ' +
            alignment +
            '">' +
            fancy_box_wrap +
            span +
            '</div>';
          if (imageData != null) {
            editor.dom.replace(
              editor.dom.createFragment(img_div),
              imageData.parent
            );
          } else {
            editor.insertContent(img_div);
          }
        }
      }
    });
    // ================== Fill with selection =============
    if (imageData != null) {
      win
        .find('#file')
        .value(imageData.file)
        .fire('change');
      win
        .find('#alternate')
        .value(imageData.alt)
        .fire('change');
      win
        .find('#width')
        .value(imageData.width)
        .fire('change');
      win
        .find('#height')
        .value(imageData.height)
        .fire('change');
      if (imageData.align !== undefined) {
        win
          .find('#alignment')
          .value(imageData.align)
          .fire('change');
      }
      if (imageData.fancy !== undefined) {
        win
          .find('#fancybox')
          .value(imageData.fancy)
          .fire('change');
      }
      win
        .find('#title')
        .value(imageData.title)
        .fire('change');
      win
        .find('#caption')
        .value(imageData.caption)
        .fire('change');
    }
    // ================== Fill with selection =============
  }

  return {
    open
  };
}
