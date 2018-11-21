tinymce.init({
  plugins: "ccm-cms-images code lists nonbreaking noneditable paste searchreplace table template visualblocks wordcount",
  selector: ".tinymce",
  templates: [],
  content_css: ['./editor.css'],
  toolbar: "undo redo | styleselect | bold italic | alignleft aligncenter alignright alignjustify | numlist bullist | outdent indent"
});