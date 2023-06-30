# Privacy Policy
HINT Control collects no personal information from your device. Any data that is collected is non-sensitive and anonymous.

## Crash Reports
HINT Control uses Bugsnag to collect crash reports on Desktop, Android, and iOS. Bugsnag reports are completely anonymous and only contain basic information needed for debugging.

This information usually includes:
- Stack traces (where a crash or error occurred in the source code).
- Device information such as OS version and model.
- HTTP information such as the content of requests and responses (stripped to remove credentials *before* being uploaded).
- Breadcrumbs (where the app navigated and when it went to the background/foreground leading up to the error).

Reports do not include any personal information, including, but not limited to:
- Name.
- Email address.
- Phone number.
- IMEI.
- SIM ID.
- Location.
- Statistics from your gateway, such as Cell ID or connected bands.

## Gateway Password
HINT Control can optionally store your gateway login and automatically authenticate on subsequent app launches.

- On Android and iOS, the password is stored in the app's private data, which is inaccessible to anything but the system and root.
- On Desktop, the password is stored in plaintext.

On all platforms, the password is only stored locally and never uploaded or transmitted except directly to the gateway for authentication.

### Password Leaks
Even if HINT Control did upload your password, you shouldn't need to worry. The T-Mobile gateways are only accessible from the local network side, not through the internet.

Even if someone has the public IP (v6 only, since there is no public v4 addressing on T-Mobile) of your gateway, they won't be able to connect, whether or not they know your password.
