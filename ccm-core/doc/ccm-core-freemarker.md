# Freemarker functions provided by ccm-themedirector

## User Banner

Import Path
: `/ccm-core/user-banner.ftl`

### `String getGreeting()`

Retrieves to the greeting value provided by the _UserBanner_ component.

### `boolean isLoggedIn()`

Return `true` if the current user is logged and `false` otherwise. 

### `boolean isNotLoggedIn()`

Return `true` if the current user is *not* logged and `false` otherwise. 

### `String getChangePasswordUrl()`

Returns the URL where a authenticated user can change his or her password.

### `String getLoginLink()`

Returns the URL of the login page.

### `String getLogoutLink()`

Returns the URL for logging out.

### `String getScreenName()`

Returns the username of the current user. If the user is not authenticated the will return an empty string.

### `String getUserGivenName()`

Returns the given of the current user, if availabe. If the user is not authenticated the will return an empty string.

### `String getUserFamilyName()`

Returns the given of the current user, if availabe. If the user is not authenticated the will return an empty string.

