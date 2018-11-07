import Dialog from "./Dialog";
declare var tinymce: any;

const plugin = (editor: any, url: String) => {
  editor.addButton("trunk-images-button", {
    icon: "image",
    onlick: Dialog(editor).open,
    stateSelector: "div.image"
  });

  editor.addMenuItem("trunk-images", {
    icon: "image",
    text: "Insert Images",
    onclick: Dialog(editor).open,
    stateSelector: "image",
    context: "insert",
    prependToContext: true
  });
};
export default plugin;
