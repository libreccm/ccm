# TinyMCE Integration module for ccm-cms

## Prepare 

Install modules via `npm`

    npm install

Build CCM plugins for TinyMCE And copy all required files to web directory:

    npm run build

After that the normal build (`ant deploy` or similar) will pick up the files 
and put them into the correct places.
