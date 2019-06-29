const fs = require("fs");
const fsextra = require("fs-extra");
const path = require("path");
const webpack = require("webpack");

fs.copyFile("node_modules/tinymce/tinymce.min.js", "web/assets/ccm-cms-tinymce/tinymce.min.js", (error) => {
    if (error) {
        throw error;
    } else {
        console.log("TinyMCE copied to web directory.");
    }
});

webpack(
    {
        entry: {
            "ccm-cms-tinymce": "./ccm-cms-tinymce.js"
        },
        mode: "production",
        output: {
            path: path.resolve(__dirname, "web/assets/ccm-cms-tinymce"),
            filename: "[name].js"
        }
    },
    (error, stats) => {
        if (error) {
            console.error("webpack failed:");
            console.error(error.stack || error);
            if (error.details) {
                console.error("Error details:");
                console.error(error.details);
            }
            return;
        }

        const info = stats.toJson();
        if (stats.hasErrors()) {
            console.error("webpack failed:");
            console.error(info.errors);
            return;
        }

        if (stats.hasWarnings()) {
            console.warn(info.warnings);
        }

        console.log("webpack executed successfully.");
    }
);

fsextra.copy("node_modules/tinymce/skins", "web/assets/ccm-cms-tinymce/skins", (error) => {
    if (error) {
        throw error;
    } else {
        console.log("TinyMCE skins copied to web directory");
    }
});
fsextra.copy("node_modules/tinymce/themes", "web/assets/ccm-cms-tinymce/themes", (error) => {
    if (error) {
        throw error;
    } else {
        console.log("TinyMCE themes copied to web directory");
    }
});
