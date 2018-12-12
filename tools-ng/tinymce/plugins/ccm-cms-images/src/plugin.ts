import Dialog from "./Dialog";
declare var tinymce: any;

const plugin = (editor: any, url: String) => {
  editor.addButton("ccm-cms-images-button", {
    icon: "image",
    tooltip: "Insert/Edit image",
    onlick: Dialog(editor).open,
    stateSelector: "div.image"
  });

  editor.addMenuItem("ccm-cms-images-menu", {
    icon: "image",
    text: "Insert/Edit Images",
    onclick: Dialog(editor).open,
    stateSelector: "image",
    context: "insert",
    prependToContext: true
  });
};
export default plugin;
