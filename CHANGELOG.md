# Change Log
All notable changes to this project will be documented in this file.

##master

## 1.1.0 - 2015-01-27
### Changed
- Extracted Identity Provider (IdP) logic to a independent module (only requires core lib).
- Fixed issue when AD login fragment is the main screen.
- Fixed NPE when authentication using a `WebView` is cancelled by the user

### Changed
- Deprecated old delegation methods in `A0APIClient`

## 1.0.1 - 2015-01-26
### Changed
- Fixed issue when application has only one connection and is AD. [#40](issues/40)

## 1.0.0 - 2015-01-19
### Added
- Initial release of Lock for Android
- Database + Social + Enterprise authentication
- Native integration with *Facebook* and *Google+*
- Lock UI implemented in `LockActivity`
- Authentication API Java client
- SMS Authentication (with `LockSMSActivity`)
