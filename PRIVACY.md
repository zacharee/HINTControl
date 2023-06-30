# Privacy Policy
HINT Control collects no personal information from your device. Any data that is collected is non-sensitive and anonymous.

## Crash Reports
HINT Control uses Bugsnag to collect crash reports on Desktop, Android, and iOS. Bugsnag reports are completely anonymous and only contain stack traces and basic device information needed for debugging.

Information such as your name or email address is not included.

## Gateway Password
HINT Control can optionally store your gateway login and automatically authenticate on subsequent app launches.

- On Android and iOS, the password is stored in the app's private data, which is inaccessible to anything but the system and root.
- On Desktop, the password is stored in plaintext.

On all platforms, the password is only stored locally and never uploaded or transmitted except directly to the gateway for authentication.

### Password Leaks
Even if HINT Control did upload your password, you shouldn't need to worry. The T-Mobile gateways are only accessible from the local network side, not through the internet.

Even if someone has the public IP (v6 only, since there is no public v4 addressing on T-Mobile) of your gateway, they won't be able to connect, whether or not they know your password.
