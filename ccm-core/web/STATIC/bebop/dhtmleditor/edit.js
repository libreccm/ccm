window.onload=fnInit;

var dFormName = "";
var dElementName = "";
var isWysiwyg = true;

function showProperties(object) {
  var result = "";
  for (var i in object) {
    result += i + " = " + object[i] + ";";
  }
  alert(result);
}

function getEditorText() {

  if (isWysiwyg) {
    return wysiwygEditorDiv.innerHTML;
  } else {
     return document.forms[dFormName].elements[dElementName].innerText;
  }
}

function editorSubmit() {

  if (isWysiwyg) {
    var textArea = document.forms[0].elements[dElementName];
    textArea.innerText = wysiwygEditorDiv.innerHTML;
  }
}

function toggleEditor() {

  var textArea = document.forms[0].elements[dElementName];
  if (isWysiwyg) {
    textArea.innerText = wysiwygEditorDiv.innerHTML;
    wysiwygEditor.style.display="none";
    sourceEditor.style.display="block";
    textArea.focus();
    isWysiwyg = false;
  } else {
    wysiwygEditorDiv.innerHTML = textArea.innerText;
    sourceEditor.style.display="none";
    wysiwygEditor.style.display="block";
    wysiwygEditorDiv.focus();
    isWysiwyg = true;
  }
}

function fnInit() {
 
  for (i = 0; i < document.all.length; i++) {
    document.all(i).unselectable = "on";
  }

  wysiwygEditorDiv.unselectable = "off";
  for (i = 0; i < wysiwygEditorDiv.all.length; i++) {
    if (wysiwygEditorDiv.all(i).removeAttribute) 
      wysiwygEditorDiv.all(i).removeAttribute("unselectable", 0);
  }
 
  var textArea = document.forms[0].elements[dElementName];
  textArea.unselectable = "off";
  textArea.innerText = wysiwygEditorDiv.innerHTML;
}

function toolOver() {
  window.event.srcElement.style.borderStyle = "outset";
}

function toolOut() {	
  window.event.srcElement.style.borderStyle = "solid";
}

function toolDown(command, display, param) {
  document.execCommand(command, display, param);
  window.event.srcElement.style.borderStyle = "solid";
}

function format(command, display, param) {
  document.execCommand(command, display, param);
  window.event.srcElement.style.borderStyle = "inset";
}

function formatOut(command) {	

  if (document.queryCommandState(command)) {
    window.event.srcElement.style.borderStyle = "inset";
  } else {
    window.event.srcElement.style.borderStyle = "solid";
  }
}

function link(command, display, param) {
  range = document.selection.createRange();
  if (range.htmlText == "") {
    alert("Please select some text before creating a hyperlink.");
    return;
  }
  document.execCommand(command, display, param);
  document.selection.empty();
  alert("Hyperlink created.");
  window.event.srcElement.style.borderStyle = "solid";
}

function updateFormat() {

  if (document.queryCommandState("Bold")) {
    document.bold.style.borderStyle = "inset";
  } else {
    document.bold.style.borderStyle = "solid";
  }

  if (document.queryCommandState("Italic")) {
    document.italic.style.borderStyle = "inset";
  } else {
    document.italic.style.borderStyle = "solid";
  }

  if (document.queryCommandState("Underline")) {
    document.underline.style.borderStyle = "inset";
  } else {
    document.underline.style.borderStyle = "solid";
  }
}

function createEditor(formName, elementName) {

  dFormName = formName;
  dElementName = elementName;

  document.forms[dFormName].onsubmit = editorSubmit;
}
