# Change Log

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