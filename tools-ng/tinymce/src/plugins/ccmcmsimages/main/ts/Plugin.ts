import PluginManager from 'tinymce/core/api/PluginManager';
import Dialog from './Dialog';

PluginManager.add('ccmcmsimages', function (editor) {
  editor.addButton('ccm-cms-images-button', {
    icon: 'image',
    tooltip: 'Insert/Edit image',
    onclick: Dialog(editor).open,
    stateSelector: 'image'
  });

  editor.addMenuItem('ccm-cms-images-menu', {
    icon: 'image',
    text: 'Insert/Edit Images',
    onclick: Dialog(editor).open,
    stateSelector: 'image',
    context: 'insert',
    prependToContext: true
  });
});

export default function () {}
