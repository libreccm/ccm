/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// identity function for calling harmony imports with the correct context
/******/ 	__webpack_require__.i = function(value) { return value; };
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 2);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

exports.__esModule = true;
var Dialog_1 = __webpack_require__(1);
var plugin = function (editor, url) {
    editor.addButton("ccm-cms-images-button", {
        icon: "image",
        tooltip: "Insert/Edit image",
        onlick: Dialog_1["default"](editor).open,
        stateSelector: "div.image"
    });
    editor.addMenuItem("ccm-cms-images-menu", {
        icon: "image",
        text: "Insert Images",
        onclick: Dialog_1["default"](editor).open,
        stateSelector: "image",
        context: "insert",
        prependToContext: true
    });
};
exports["default"] = plugin;


/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

exports.__esModule = true;
function default_1(editor) {
    function getImageData(editor) {
        var elem = editor.selection.getNode();
        var imgDiv = editor.dom.getParent(elem, "div.image");
        var img = editor.dom.select("img", imgDiv)[0];
        if (imgDiv != null) {
            var imageData = {
                file: img.getAttribute("src"),
                width: img.getAttribute("width").slice(0, -2),
                height: img.getAttribute("height").slice(0, -2),
                alt: img.getAttribute("alt"),
                parent: imgDiv
            };
            return imageData;
        }
        else {
            return null;
        }
    }
    function open() {
        var imageData = getImageData(editor);
        var image_name = "";
        var fileChooseContainer = new tinymce.ui.Container({
            type: "container",
            layout: "flex",
            direction: "row",
            align: "center",
            padding: 5,
            spacing: 15,
            margin: 5
        });
        var imagePathTextBox = new tinymce.ui.TextBox({
            name: "file",
            label: "File:",
            disabled: true
        });
        fileChooseContainer.add(imagePathTextBox);
        var browseButton = new tinymce.ui.Button({
            name: "browse_images",
            text: "Browse Images",
            onclick: function () {
                var baseURL = window.location.href;
                var offset = baseURL.lastIndexOf("/");
                var destURL = baseURL.slice(0, offset + 1) + "image_select.jsp";
                var selectWindow = window.open(destURL, "_blank", "scrollbars=yes,directories=no,toolbar=no,width=800,height=600,status=no,menubar=no");
                window.openCCM = new Object();
                window.openCCM.imageSet = function (selectedImage) {
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
        var alternateTextBox = new tinymce.ui.TextBox({
            name: "alternate",
            label: "Alternate:"
        });
        var titleTextBox = new tinymce.ui.TextBox({
            name: "title",
            label: "Title:"
        });
        var alignmentContainer = new tinymce.ui.Container({
            type: "container",
            layout: "flex",
            direction: "row"
        });
        var alginLabel = new tinymce.ui.Label({
            text: "Alignment:"
        });
        var alignListBox = new tinymce.ui.ListBox({
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
        var fancyBoxContainer = new tinymce.ui.Container({
            type: "container",
            layout: "flex",
            direction: "row"
        });
        var fancyBoxLabel = new tinymce.ui.Label({
            text: "Fancy Box:"
        });
        var fancyBoxListBox = new tinymce.ui.ListBox({
            name: "fancybox",
            values: [
                { text: "None", value: "" },
                { text: "Zoom", value: "imageZoom" },
                { text: "Gallery", value: "imageGallery" }
            ]
        });
        fancyBoxContainer.add(fancyBoxLabel);
        fancyBoxContainer.add(fancyBoxListBox);
        var captionCheckBox = new tinymce.ui.Checkbox({
            label: "Caption:",
            name: "caption"
        });
        var dimensionContainer = new tinymce.ui.Container({
            label: "Dimension",
            layout: "flex",
            direction: "row",
            align: "center",
            padding: 5,
            spacing: 15,
            margin: 5
        });
        var widthTextBox = new tinymce.ui.TextBox({
            name: "width",
            label: "Width"
        });
        var heightTextBox = new tinymce.ui.TextBox({
            name: "height",
            label: "Height"
        });
        dimensionContainer.add(widthTextBox);
        dimensionContainer.add({ type: "label", text: "X" });
        dimensionContainer.add(heightTextBox);
        var win = editor.windowManager.open({
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
            onsubmit: function () {
                var src = win.find("#file").value();
                var alternate = win.find("#alternate").value();
                var width = win.find("#width").value();
                var height = win.find("#height").value();
                var title = win.find("#title").value();
                var alignment = win.find("#alignment").value();
                var fancy_box = win.find("#fancybox").value();
                if (src != null) {
                    var img = "<img src=" +
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
                    var fancy_box_wrap = "<a class=" +
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
                    var span = "";
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
                    var img_div = '<div class="image ' +
                        alignment +
                        '">' +
                        fancy_box_wrap +
                        span +
                        "</div>";
                    if (imageData != null) {
                        editor.dom.replace(editor.dom.createFragment(img_div + "<br/>"), imageData.parent);
                    }
                    else {
                        editor.insertContent(img_div + "<br/>");
                    }
                }
            }
        });
        if (imageData != null) {
            win
                .find("#file")
                .value(imageData.file)
                .fire("change");
            win
                .find("#alternate")
                .value(imageData.alt)
                .fire("change");
            win
                .find("#width")
                .value(imageData.width)
                .fire("change");
            win
                .find("#height")
                .value(imageData.height)
                .fire("change");
        }
    }
    return {
        open: open
    };
}
exports["default"] = default_1;


/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

exports.__esModule = true;
var plugin_1 = __webpack_require__(0);
tinymce.PluginManager.add("ccm-cms-images", plugin_1["default"]);


/***/ })
/******/ ]);