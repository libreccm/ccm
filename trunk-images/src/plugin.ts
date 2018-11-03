import Dialog from "./Dialog";

const plugin = (editor: any, url: String) => {
  console.log("Trunk-Images loaded");
  editor.addMenuItem("trunk-images", {
    icon: false,
    text: "Insert Trunk-Images",
    onclick: function() {
      Dialog.open(editor);
    },
    context: "insert"
  });
  return {};
};
export default plugin;
