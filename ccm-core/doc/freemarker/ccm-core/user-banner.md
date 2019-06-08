# Freemarker functions for retrieving data from the user banner component.

Import Path
: `/ccm-core/user-banner.ftl`

### `getGreeting(): String`

Retrieves to the greeting value provided by the _UserBanner_ component.

### `isLoggedIn(): boolean`

Return `true` if the current user is logged and `false` otherwise. 

### `isNotLoggedIn(): boolean`

Return `true` if the current user is *not* logged and `false` otherwise. 

### `getChangePasswordUrl(): String`

Returns the URL where a authenticated user can change his or her password.

### `getLoginLink(): String`

Returns the URL of the login page.

### `getLogoutLink(): String`

Returns the URL for logging out.

### `getScreenName(): String`

Returns the username of the current user. If the user is not authenticated the will return an empty string.

### `getUserGivenName(): String`

Returns the given of the current user, if availabe. If the user is not authenticated the will return an empty string.

### `getUserFamilyName(): String`

Returns the given of the current user, if availabe. If the user is not authenticated the will return an empty string.

