### ccm-cms-assets-imagestep

Provides functions for dealing with image attachments of a content item.

Import path: `<#import "/ccm-cms-assets-imagestep.ftl" as Images>`

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

#### getImageAttachments

    getImageAttachments(item)

Get the image attachments of a content item

##### Parameters

`item` The content item.

##### Returns

A sequence of the image attachments of the provided content item.

#### getImageId

    getImageId(image)

Get the ID of the provided image.

##### Parameters

`image` The image.

##### Returns

The id of the image.

#### getImageName

    getImageName(image)

Gets the name of the provided image.

##### Parameters

`image` The image.

##### Returns

The name of the image.

#### getImageCaption

    getImageCaption(image)

Gets the caption of the provided image.

##### Parameters

`image` The image.

##### Returns

The caption of the image.

#### getImageSortKey

    getImageSortKey(image)

Gets the sort key of the provided image.

##### Parameters

`image` The image.

##### Returns

The sort key of the provided image.

#### getImageWidth

    getImageWidth(image)

Gets the width of the provided image.

##### Parameters

`image` The image.

##### Returns

The width of the provided image.

#### getImageHeight

    getImageHeight(image)

Gets the height of the provided image.

##### Parameters

`image` The image.

##### Returns

The height of the provided image.

#### getImageUrl

    getImageUrl(image)

Gets the URL of the provided image.

##### Parameters

`image` The image.

##### Returns

The URL of the provided image.

