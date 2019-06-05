# Freemarker functions for Image Attachments

Provides functions for dealing with image attachments of a content item.

Import path
: `/ccm-cms-assets-imagestep.ftl"`

Example usage:

```
<#import "/ccm-cms-assets-imagestep.ftl" as Images>

<#list Images.getImageAttachments(item)>
    <div class="image-attachments">
        <#items as image>
            <figure>
                <div>
                    <a data-fancybox="gallery">
                        <img src="${Images.getImageUrl(image)}"
                             width="100%" 
                             height="auto" />
                    </a>
                </div>
                <figcaption>
                    ${Images.getImageCaption(image)}
                </figcaption>
            </figure>
        </#items>
    </div>
</#list>
```

## `getImageAttachments(item: Node): Sequence<Node>`

Get the image attachments of the provided content item.

## `getImageId(image: Node): String`

Gets the ID of the provided image.

## `getImageName(image: Node): String`

Gets the name of the provided image.

## `getImageCaption(image: Node): String`

Gets the caption of the provided image.

#### `getImageSortKey(image: Node): String`

Gets the sort key of the provided image.

## `getImageWidth(image: Node): String`

Gets the width of the provided image.

#### `getImageHeight(image: Node): String`

Gets the height of the provided image.

## `getImageUrl(image: Node): String`

Gets the URL of the provided image.

