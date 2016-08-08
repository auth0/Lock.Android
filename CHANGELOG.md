# Change Log

## [1.17.0](https://github.com/auth0/Lock.Android/tree/1.17.0) (2016-08-18)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.16.1...1.17.0)

**Merged pull requests:**

- Send Accept-Language Header [\#312](https://github.com/auth0/Lock.Android/pull/312) ([lbalmaceda](https://github.com/lbalmaceda))
- Add Dropbox and Bitbucket social connections [\#316](https://github.com/auth0/Lock.Android/pull/316) ([lbalmaceda](https://github.com/lbalmaceda))
- Allow to cancel requests [\#318](https://github.com/auth0/Lock.Android/pull/318) ([lbalmaceda](https://github.com/lbalmaceda))

## [1.16.1](https://github.com/auth0/Lock.Android/tree/1.16.1) (2016-05-18)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.16.0...1.16.1)

## [1.16.0](https://github.com/auth0/Lock.Android/tree/1.16.0) (2016-05-18)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.15.0...1.16.0)

**Merged pull requests:**

- Add mention and link to magic-link docs [\#259](https://github.com/auth0/Lock.Android/pull/259) ([nikolaseu](https://github.com/nikolaseu))
- Active MFA support [\#261](https://github.com/auth0/Lock.Android/pull/261) ([lbalmaceda](https://github.com/lbalmaceda))

## [1.15.0](https://github.com/auth0/Lock.Android/tree/1.15.0) (2016-04-15)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.14.1...1.15.0)

**Merged pull requests:**

- Add OAuth2 callback [\#241](https://github.com/auth0/Lock.Android/pull/241) ([hzalaz](https://github.com/hzalaz))
- WebIdentityProvider for OAuth2 flow [\#240](https://github.com/auth0/Lock.Android/pull/240) ([hzalaz](https://github.com/hzalaz))
- Use PKCE on calls to /authorize [\#238](https://github.com/auth0/Lock.Android/pull/238) ([lbalmaceda](https://github.com/lbalmaceda))

## [1.14.1](https://github.com/auth0/Lock.Android/tree/1.14.1) (2016-04-04)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.14.0...1.14.1)

**Fixed bugs:**

- Fix UserIdentity properties [\#232](https://github.com/auth0/Lock.Android/issues/232)
- DB Signup with more than one connection fails [\#217](https://github.com/auth0/Lock.Android/issues/217)

**Merged pull requests:**

- Rework telemetry logic [\#235](https://github.com/auth0/Lock.Android/pull/235) ([hzalaz](https://github.com/hzalaz))
- Handle required and optional attribute for UserIdentity [\#234](https://github.com/auth0/Lock.Android/pull/234) ([hzalaz](https://github.com/hzalaz))

## [1.14.0](https://github.com/auth0/Lock.Android/tree/1.14.0) (2016-03-18)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.13.0...1.14.0)

**Closed issues:**

- License and Readme need fixing [\#162](https://github.com/auth0/Lock.Android/issues/162)

**Merged pull requests:**

- Set the proper connection when creating a new user [\#223](https://github.com/auth0/Lock.Android/pull/223) ([lbalmaceda](https://github.com/lbalmaceda))
- Change password: Remove old flow and layout [\#222](https://github.com/auth0/Lock.Android/pull/222) ([lbalmaceda](https://github.com/lbalmaceda))
- send the link to the email for password reset by default [\#220](https://github.com/auth0/Lock.Android/pull/220) ([lbalmaceda](https://github.com/lbalmaceda))
- handle M permissions on permission required providers [\#219](https://github.com/auth0/Lock.Android/pull/219) ([lbalmaceda](https://github.com/lbalmaceda))
- Bug fixing [\#198](https://github.com/auth0/Lock.Android/pull/198) ([lbalmaceda](https://github.com/lbalmaceda))
- Fix license and readme [\#163](https://github.com/auth0/Lock.Android/pull/163) ([aguerere](https://github.com/aguerere))

## [1.13.0](https://github.com/auth0/Lock.Android/tree/1.13.0) (2015-12-23)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.12.1...1.13.0)

**Closed issues:**

- Add an option to use Webview with ADFS connections [\#158](https://github.com/auth0/Lock.Android/issues/158)
- Add `au` region for CDN [\#152](https://github.com/auth0/Lock.Android/issues/152)

**Merged pull requests:**

- Add option to force webview on adfs connections [\#159](https://github.com/auth0/Lock.Android/pull/159) ([nikolaseu](https://github.com/nikolaseu))
- Add AU region CDN [\#156](https://github.com/auth0/Lock.Android/pull/156) ([nikolaseu](https://github.com/nikolaseu))

## [1.12.1](https://github.com/auth0/Lock.Android/tree/1.12.1) (2015-12-14)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.12.0...1.12.1)

**Implemented enhancements:**

- Log the body of the error responses [\#144](https://github.com/auth0/Lock.Android/issues/144)
- Support customizing "user is blocked" message [\#140](https://github.com/auth0/Lock.Android/issues/140)

**Fixed bugs:**

- In change password screen use "New Password" instead of "Password" [\#146](https://github.com/auth0/Lock.Android/issues/146)

**Closed issues:**

- Bug when Signing up new user [\#153](https://github.com/auth0/Lock.Android/issues/153)
- Error logging in from google+ [\#151](https://github.com/auth0/Lock.Android/issues/151)
- Send login\_hint parameter for all email/username requests [\#147](https://github.com/auth0/Lock.Android/issues/147)

**Merged pull requests:**

- Fix mixed signup parameters [\#155](https://github.com/auth0/Lock.Android/pull/155) ([hzalaz](https://github.com/hzalaz))
- Send 'login\_hint' parameter [\#150](https://github.com/auth0/Lock.Android/pull/150) ([nikolaseu](https://github.com/nikolaseu))
- Feature: Add option to customize the "user is blocked" message [\#149](https://github.com/auth0/Lock.Android/pull/149) ([nikolaseu](https://github.com/nikolaseu))
- Change password screen: use 'New password' instead of 'Password' [\#148](https://github.com/auth0/Lock.Android/pull/148) ([nikolaseu](https://github.com/nikolaseu))
- Log the exception when the request fails [\#145](https://github.com/auth0/Lock.Android/pull/145) ([nikolaseu](https://github.com/nikolaseu))

## [1.12.0](https://github.com/auth0/Lock.Android/tree/1.12.0) (2015-11-26)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.11.0...1.12.0)

**Implemented enhancements:**

- Update Microsoft Account logo and color [\#114](https://github.com/auth0/Lock.Android/issues/114)

**Closed issues:**

- Update Google+ social icon [\#131](https://github.com/auth0/Lock.Android/issues/131)

**Merged pull requests:**

- Update readme for v1.12 [\#143](https://github.com/auth0/Lock.Android/pull/143) ([nikolaseu](https://github.com/nikolaseu))
- Update sample app [\#142](https://github.com/auth0/Lock.Android/pull/142) ([nikolaseu](https://github.com/nikolaseu))
- Use 'Login with Microsoft' as title [\#141](https://github.com/auth0/Lock.Android/pull/141) ([hzalaz](https://github.com/hzalaz))
- Add \(and use\) new class LockContext to provide the Lock instance [\#139](https://github.com/auth0/Lock.Android/pull/139) ([nikolaseu](https://github.com/nikolaseu))
- deprecate lock-sms and lock-email in favor of lock-passwordless [\#138](https://github.com/auth0/Lock.Android/pull/138) ([nikolaseu](https://github.com/nikolaseu))
- Fixed typo [\#137](https://github.com/auth0/Lock.Android/pull/137) ([trydis](https://github.com/trydis))
- Feature: add lock-passwordless [\#136](https://github.com/auth0/Lock.Android/pull/136) ([nikolaseu](https://github.com/nikolaseu))
- Feature: add passwordless sms magic link [\#135](https://github.com/auth0/Lock.Android/pull/135) ([nikolaseu](https://github.com/nikolaseu))
- Feature add passwordless email magic link [\#134](https://github.com/auth0/Lock.Android/pull/134) ([nikolaseu](https://github.com/nikolaseu))

## [1.11.0](https://github.com/auth0/Lock.Android/tree/1.11.0) (2015-11-13)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.10.1...1.11.0)

**Fixed bugs:**

- com.auth0.api.APIClient﹕ Failed obtain delegation token info [\#117](https://github.com/auth0/Lock.Android/issues/117)
- Social authentication fails when using embedded WebView [\#116](https://github.com/auth0/Lock.Android/issues/116)

**Closed issues:**

- Add ImageView to background on login/reset password screens [\#126](https://github.com/auth0/Lock.Android/issues/126)

**Merged pull requests:**

- Update the minimum api level to 15 [\#133](https://github.com/auth0/Lock.Android/pull/133) ([nikolaseu](https://github.com/nikolaseu))
- Update google-oauth2 background color [\#132](https://github.com/auth0/Lock.Android/pull/132) ([nikolaseu](https://github.com/nikolaseu))
- update google support library to v23. this requires build sdk \>= 23 [\#130](https://github.com/auth0/Lock.Android/pull/130) ([nikolaseu](https://github.com/nikolaseu))
- use generic icon/colors for unknown strategies [\#129](https://github.com/auth0/Lock.Android/pull/129) ([nikolaseu](https://github.com/nikolaseu))
- add ImageView and styles to activity to have more control over backgr… [\#128](https://github.com/auth0/Lock.Android/pull/128) ([xorgate](https://github.com/xorgate))
- use drawables instead of font icons for the social strategies [\#127](https://github.com/auth0/Lock.Android/pull/127) ([nikolaseu](https://github.com/nikolaseu))
- Bugfix missing strategy type [\#125](https://github.com/auth0/Lock.Android/pull/125) ([nikolaseu](https://github.com/nikolaseu))
- Listen to event bus while activity is alive [\#122](https://github.com/auth0/Lock.Android/pull/122) ([hzalaz](https://github.com/hzalaz))
- Migrate to OkHttp [\#121](https://github.com/auth0/Lock.Android/pull/121) ([hzalaz](https://github.com/hzalaz))

## [1.10.1](https://github.com/auth0/Lock.Android/tree/1.10.1) (2015-09-28)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.10.0...1.10.1)

**Merged pull requests:**

- Use android-async-http:1.4.9 [\#120](https://github.com/auth0/Lock.Android/pull/120) ([hzalaz](https://github.com/hzalaz))

## [1.10.0](https://github.com/auth0/Lock.Android/tree/1.10.0) (2015-09-26)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.6...1.10.0)

**Merged pull requests:**

- Correctly handle sms and email as passwordless strategies [\#119](https://github.com/auth0/Lock.Android/pull/119) ([hzalaz](https://github.com/hzalaz))
- Email Passwordless Authentication [\#118](https://github.com/auth0/Lock.Android/pull/118) ([hzalaz](https://github.com/hzalaz))

## [1.9.6](https://github.com/auth0/Lock.Android/tree/1.9.6) (2015-07-28)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.5...1.9.6)

**Merged pull requests:**

- Allow custom UI to use IdP authentication classes [\#111](https://github.com/auth0/Lock.Android/pull/111) ([hzalaz](https://github.com/hzalaz))
- Add Auth0 class to handle basic account information [\#110](https://github.com/auth0/Lock.Android/pull/110) ([hzalaz](https://github.com/hzalaz))

## [1.9.5](https://github.com/auth0/Lock.Android/tree/1.9.5) (2015-07-08)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.4...1.9.5)

**Fixed bugs:**

- User's identities are not properly parsed from JSON [\#106](https://github.com/auth0/Lock.Android/issues/106)

**Merged pull requests:**

- Use new /passwordless/start endpoint [\#108](https://github.com/auth0/Lock.Android/pull/108) ([hzalaz](https://github.com/hzalaz))
- Fix class cast exception identities [\#107](https://github.com/auth0/Lock.Android/pull/107) ([hzalaz](https://github.com/hzalaz))

## [1.9.4](https://github.com/auth0/Lock.Android/tree/1.9.4) (2015-06-22)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.3...1.9.4)

**Implemented enhancements:**

- Better handling of app info fetch errors [\#88](https://github.com/auth0/Lock.Android/issues/88)

**Fixed bugs:**

- Email validation deeming email with + character as invalid [\#103](https://github.com/auth0/Lock.Android/issues/103)

**Merged pull requests:**

- Improve Loading app info error messages [\#105](https://github.com/auth0/Lock.Android/pull/105) ([hzalaz](https://github.com/hzalaz))
- Allow mails with `+` and `.` [\#104](https://github.com/auth0/Lock.Android/pull/104) ([hzalaz](https://github.com/hzalaz))

## [1.9.3](https://github.com/auth0/Lock.Android/tree/1.9.3) (2015-06-15)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.2...1.9.3)

**Implemented enhancements:**

- Add custom Auth0 BroadcastReceivers [\#96](https://github.com/auth0/Lock.Android/issues/96)

**Fixed bugs:**

- Email Signup does not close window on success  [\#101](https://github.com/auth0/Lock.Android/issues/101)
- Cursor does not show in Lollipop [\#98](https://github.com/auth0/Lock.Android/issues/98)

**Merged pull requests:**

- Fix for SignUp when success callback is not called  [\#102](https://github.com/auth0/Lock.Android/pull/102) ([hzalaz](https://github.com/hzalaz))
- Custom BroadcastReceivers [\#100](https://github.com/auth0/Lock.Android/pull/100) ([hzalaz](https://github.com/hzalaz))
- Invisible Cursor Fix [\#99](https://github.com/auth0/Lock.Android/pull/99) ([hzalaz](https://github.com/hzalaz))

## [1.9.2](https://github.com/auth0/Lock.Android/tree/1.9.2) (2015-06-03)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.1...1.9.2)

**Merged pull requests:**

- Allow Lock.Builder to be extended for custom Lock classes [\#95](https://github.com/auth0/Lock.Android/pull/95) ([hzalaz](https://github.com/hzalaz))

## [1.9.1](https://github.com/auth0/Lock.Android/tree/1.9.1) (2015-06-02)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.9.0...1.9.1)

**Fixed bugs:**

- Android Core minSdk should be API level 9 [\#94](https://github.com/auth0/Lock.Android/issues/94)

## [1.9.0](https://github.com/auth0/Lock.Android/tree/1.9.0) (2015-06-02)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.8.0...1.9.0)

**Fixed bugs:**

- Web flow url host should match account domain [\#83](https://github.com/auth0/Lock.Android/issues/83)

**Merged pull requests:**

- Send SDK info in query parameter [\#93](https://github.com/auth0/Lock.Android/pull/93) ([hzalaz](https://github.com/hzalaz))
- Send SDK Client Headers [\#92](https://github.com/auth0/Lock.Android/pull/92) ([hzalaz](https://github.com/hzalaz))
- Use domain for authorise url instead of tenant [\#91](https://github.com/auth0/Lock.Android/pull/91) ([hzalaz](https://github.com/hzalaz))
- Refactored Lock builder & new util methods [\#90](https://github.com/auth0/Lock.Android/pull/90) ([hzalaz](https://github.com/hzalaz))
- Introduce credential stores in Lock [\#89](https://github.com/auth0/Lock.Android/pull/89) ([hzalaz](https://github.com/hzalaz))

## [1.8.0](https://github.com/auth0/Lock.Android/tree/1.8.0) (2015-05-27)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.7.0...1.8.0)

**Implemented enhancements:**

- Move social integrations to an independent pod [\#77](https://github.com/auth0/Lock.Android/issues/77)
- Support disableSignupAction and disableChangePassword [\#75](https://github.com/auth0/Lock.Android/issues/75)

**Fixed bugs:**

- Handle null callback in App configuration [\#76](https://github.com/auth0/Lock.Android/issues/76)

**Closed issues:**

- java.lang.NoClassDefFoundError: com.auth0.identity.WebIdentityProvider [\#79](https://github.com/auth0/Lock.Android/issues/79)

**Merged pull requests:**

- Allow null callback url [\#85](https://github.com/auth0/Lock.Android/pull/85) ([hzalaz](https://github.com/hzalaz))
- Chore independent native integrations [\#84](https://github.com/auth0/Lock.Android/pull/84) ([hzalaz](https://github.com/hzalaz))
- Feature disable signup change password flags [\#82](https://github.com/auth0/Lock.Android/pull/82) ([hzalaz](https://github.com/hzalaz))

## [1.7.0](https://github.com/auth0/Lock.Android/tree/1.7.0) (2015-05-20)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.6.0...1.7.0)

**Fixed bugs:**

- Properly handle configuration URL [\#71](https://github.com/auth0/Lock.Android/issues/71)
- Switch to EU cdn when domain is from EU. [\#70](https://github.com/auth0/Lock.Android/issues/70)

**Closed issues:**

- Social Login - Google Plus\(non native\) fails to return from browser [\#69](https://github.com/auth0/Lock.Android/issues/69)

**Merged pull requests:**

- Unlink account in APIClient [\#74](https://github.com/auth0/Lock.Android/pull/74) ([hzalaz](https://github.com/hzalaz))
- Prefix resources with 'com\_auth0' [\#73](https://github.com/auth0/Lock.Android/pull/73) ([hzalaz](https://github.com/hzalaz))
- Lock configuration for EU [\#72](https://github.com/auth0/Lock.Android/pull/72) ([hzalaz](https://github.com/hzalaz))

## [1.6.0](https://github.com/auth0/Lock.Android/tree/1.6.0) (2015-05-01)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.5.1...1.6.0)

**Closed issues:**

- Show error message returned from a rule [\#57](https://github.com/auth0/Lock.Android/issues/57)

**Merged pull requests:**

- Show Rule error if its 'unauthorized' [\#68](https://github.com/auth0/Lock.Android/pull/68) ([hzalaz](https://github.com/hzalaz))

## [1.5.1](https://github.com/auth0/Lock.Android/tree/1.5.1) (2015-04-28)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.5.0...1.5.1)

**Merged pull requests:**

- Extract small social grid styles [\#67](https://github.com/auth0/Lock.Android/pull/67) ([hzalaz](https://github.com/hzalaz))

## [1.5.0](https://github.com/auth0/Lock.Android/tree/1.5.0) (2015-04-27)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.4.0...1.5.0)

**Implemented enhancements:**

- Native form for ADFS and WAAD connections [\#51](https://github.com/auth0/Lock.Android/issues/51)
- Handle `requires\_username` in DB connection [\#20](https://github.com/auth0/Lock.Android/issues/20)

**Closed issues:**

- Authentication Callback is showing invalid webpage for the callback url [\#46](https://github.com/auth0/Lock.Android/issues/46)

**Merged pull requests:**

- Connections waad and adfs calls /ro to authenticate [\#66](https://github.com/auth0/Lock.Android/pull/66) ([hzalaz](https://github.com/hzalaz))
- Enable requires\_username feature for DB connections [\#65](https://github.com/auth0/Lock.Android/pull/65) ([hzalaz](https://github.com/hzalaz))

## [1.4.0](https://github.com/auth0/Lock.Android/tree/1.4.0) (2015-04-16)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.3.3...1.4.0)

**Merged pull requests:**

- Feature fullscreen older devices [\#63](https://github.com/auth0/Lock.Android/pull/63) ([hzalaz](https://github.com/hzalaz))
- Add flag to only enable fullscreen in API level 16+ [\#62](https://github.com/auth0/Lock.Android/pull/62) ([hzalaz](https://github.com/hzalaz))

## [1.3.3](https://github.com/auth0/Lock.Android/tree/1.3.3) (2015-04-13)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.3.2...1.3.3)

## [1.3.2](https://github.com/auth0/Lock.Android/tree/1.3.2) (2015-04-12)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.3.1...1.3.2)

**Closed issues:**

- NPE when loading single `waad` connection [\#59](https://github.com/auth0/Lock.Android/issues/59)
- Refresh token null [\#52](https://github.com/auth0/Lock.Android/issues/52)

**Merged pull requests:**

- Avoid ResourceNotFoundException when using webview. [\#61](https://github.com/auth0/Lock.Android/pull/61) ([hzalaz](https://github.com/hzalaz))
- Fix NPE exception when no DB connection is enabled [\#60](https://github.com/auth0/Lock.Android/pull/60) ([hzalaz](https://github.com/hzalaz))
- Bugfix domain configuration [\#58](https://github.com/auth0/Lock.Android/pull/58) ([hzalaz](https://github.com/hzalaz))

## [1.3.1](https://github.com/auth0/Lock.Android/tree/1.3.1) (2015-03-27)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.3.0...1.3.1)

**Closed issues:**

- Use scope defined in Lock for web based authentication [\#54](https://github.com/auth0/Lock.Android/issues/54)
- Make the scope 'openid offline\_access' the default [\#53](https://github.com/auth0/Lock.Android/issues/53)
- There session authentication? how should I implement? [\#50](https://github.com/auth0/Lock.Android/issues/50)
- LockActivity is blank on Android 5.0.1 Lollipop [\#49](https://github.com/auth0/Lock.Android/issues/49)

**Merged pull requests:**

- Default scopes for LockBuilder. [\#56](https://github.com/auth0/Lock.Android/pull/56) ([hzalaz](https://github.com/hzalaz))
- Make WebIdentityProvider to use defined scope. [\#55](https://github.com/auth0/Lock.Android/pull/55) ([hzalaz](https://github.com/hzalaz))

## [1.3.0](https://github.com/auth0/Lock.Android/tree/1.3.0) (2015-03-12)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.2.0...1.3.0)

**Merged pull requests:**

- Filter Connections & Set default DB connection [\#48](https://github.com/auth0/Lock.Android/pull/48) ([hzalaz](https://github.com/hzalaz))
- SignUp only mode [\#47](https://github.com/auth0/Lock.Android/pull/47) ([hzalaz](https://github.com/hzalaz))

## [1.2.0](https://github.com/auth0/Lock.Android/tree/1.2.0) (2015-02-26)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.1.0...1.2.0)

**Fixed bugs:**

- Rendering problem with social login buttons [\#44](https://github.com/auth0/Lock.Android/issues/44)

## [1.1.0](https://github.com/auth0/Lock.Android/tree/1.1.0) (2015-01-27)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.0.1...1.1.0)

**Merged pull requests:**

- Extract IdP logic into a separate module [\#43](https://github.com/auth0/Lock.Android/pull/43) ([hzalaz](https://github.com/hzalaz))
- Hide cancel button for Enterprise screen when only AD is available [\#42](https://github.com/auth0/Lock.Android/pull/42) ([hzalaz](https://github.com/hzalaz))

## [1.0.1](https://github.com/auth0/Lock.Android/tree/1.0.1) (2015-01-26)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/1.0.0...1.0.1)

**Fixed bugs:**

- Enterprise Connection \(AD\) not working [\#40](https://github.com/auth0/Lock.Android/issues/40)

**Merged pull requests:**

- Fix issue when only AD connection is enabled. [\#41](https://github.com/auth0/Lock.Android/pull/41) ([hzalaz](https://github.com/hzalaz))
- Fix sample custom configuration with app info [\#39](https://github.com/auth0/Lock.Android/pull/39) ([hzalaz](https://github.com/hzalaz))

## [1.0.0](https://github.com/auth0/Lock.Android/tree/1.0.0) (2015-01-19)
**Implemented enhancements:**

- Native G+ Authentication [\#15](https://github.com/auth0/Lock.Android/issues/15)
- Enterprise Login [\#3](https://github.com/auth0/Lock.Android/issues/3)
- Allow to switch from email to username in DB auth [\#21](https://github.com/auth0/Lock.Android/issues/21)
- Change Password [\#14](https://github.com/auth0/Lock.Android/issues/14)
- DB Authentication Field Validation [\#12](https://github.com/auth0/Lock.Android/issues/12)
- DB Authentication [\#11](https://github.com/auth0/Lock.Android/issues/11)
- Proguard recommended config [\#10](https://github.com/auth0/Lock.Android/issues/10)
- Lock Theme support [\#7](https://github.com/auth0/Lock.Android/issues/7)
- Non-native Social Login [\#6](https://github.com/auth0/Lock.Android/issues/6)
- Native FB Login [\#4](https://github.com/auth0/Lock.Android/issues/4)
- Social Login screens [\#2](https://github.com/auth0/Lock.Android/issues/2)
- Lock options [\#1](https://github.com/auth0/Lock.Android/issues/1)

**Fixed bugs:**

- Trim username/email [\#34](https://github.com/auth0/Lock.Android/issues/34)

**Merged pull requests:**

- Readme updates :\) [\#38](https://github.com/auth0/Lock.Android/pull/38) ([mgonto](https://github.com/mgonto))
- Code Docs [\#37](https://github.com/auth0/Lock.Android/pull/37) ([hzalaz](https://github.com/hzalaz))
- SMS Authentication [\#36](https://github.com/auth0/Lock.Android/pull/36) ([hzalaz](https://github.com/hzalaz))
- Feature enterprise connections [\#35](https://github.com/auth0/Lock.Android/pull/35) ([hzalaz](https://github.com/hzalaz))
- Lock Activity public events. [\#33](https://github.com/auth0/Lock.Android/pull/33) ([hzalaz](https://github.com/hzalaz))
- useEmail flag [\#32](https://github.com/auth0/Lock.Android/pull/32) ([hzalaz](https://github.com/hzalaz))
- Lock Options [\#31](https://github.com/auth0/Lock.Android/pull/31) ([hzalaz](https://github.com/hzalaz))
- Proguard [\#30](https://github.com/auth0/Lock.Android/pull/30) ([hzalaz](https://github.com/hzalaz))
- Theme Support [\#29](https://github.com/auth0/Lock.Android/pull/29) ([hzalaz](https://github.com/hzalaz))
- Google+ Authentication [\#28](https://github.com/auth0/Lock.Android/pull/28) ([hzalaz](https://github.com/hzalaz))
- Facebook Native [\#27](https://github.com/auth0/Lock.Android/pull/27) ([hzalaz](https://github.com/hzalaz))
- Bring back support pack for Themes. [\#26](https://github.com/auth0/Lock.Android/pull/26) ([hzalaz](https://github.com/hzalaz))
- Social & DB screen [\#25](https://github.com/auth0/Lock.Android/pull/25) ([hzalaz](https://github.com/hzalaz))
- WebView authentication [\#24](https://github.com/auth0/Lock.Android/pull/24) ([hzalaz](https://github.com/hzalaz))
- Social Browser Authentication [\#23](https://github.com/auth0/Lock.Android/pull/23) ([hzalaz](https://github.com/hzalaz))
- Social Only Layout [\#22](https://github.com/auth0/Lock.Android/pull/22) ([hzalaz](https://github.com/hzalaz))
- Login Validation [\#19](https://github.com/auth0/Lock.Android/pull/19) ([hzalaz](https://github.com/hzalaz))
- Sign Up fixes [\#18](https://github.com/auth0/Lock.Android/pull/18) ([hzalaz](https://github.com/hzalaz))
- Styles refactor [\#17](https://github.com/auth0/Lock.Android/pull/17) ([hzalaz](https://github.com/hzalaz))
- Change Password [\#16](https://github.com/auth0/Lock.Android/pull/16) ([hzalaz](https://github.com/hzalaz))



\* *This Change Log was automatically generated by [github_changelog_generator](https://github.com/skywinder/Github-Changelog-Generator)*