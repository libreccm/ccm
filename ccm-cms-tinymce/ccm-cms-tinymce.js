var tinymce = require("tinymce/tinymce");

// A theme is also required
require('tinymce/themes/silver');

require("tinymce/plugins/code");
require("tinymce/plugins/lists");
require("tinymce/plugins/nonbreaking");
require("tinymce/plugins/noneditable");
require("tinymce/plugins/paste");
require("tinymce/plugins/searchreplace");
require("tinymce/plugins/table");
require("tinymce/plugins/template");
require("tinymce/plugins/visualblocks");
require("tinymce/plugins/wordcount");

// Initialize the app
tinymce.init({
    plugins: ["code", 
              "lists", 
              "nonbreaking", 
              "noneditable", 
              "paste", 
              "searchreplace", 
              "table", 
              "template", 
              "visualblocks", 
              "wordcount"],
    selector: ".tinymce",
    templates: [],
    content_css: ["./editor.css"],
    toolbar: "undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | numlist bullist | outdent indent"
});
