# critical-alerts

Capacitor plugin for critical alerts and DND-bypassing push notifications on iOS and Android.

- **iOS**: Requests the `criticalAlert` entitlement so notifications play sound even when the device is silenced or Do Not Disturb is on.
- **Android**: Uses FCM data-only payloads with a notification channel configured to bypass DND (`setBypassDnd`) and `USAGE_ALARM` audio attributes.

## Install

```bash
npm install critical-alerts
npx cap sync
```

## Setup

### iOS — Critical Alert entitlement

Critical alerts require explicit Apple approval. Add the entitlement to your app's `.entitlements` file:

```xml
<key>com.apple.developer.usernotifications.critical-alerts</key>
<true/>
```

Without this entitlement `requestPermission()` still succeeds for normal notifications, but `criticalAlert` will be `false`.

### Android — permissions

The plugin declares these in its own `AndroidManifest.xml`, but verify they are present after `cap sync`:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
<uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY"/>
```

`POST_NOTIFICATIONS` is required at runtime on Android 13+ (Tiramisu). Call `requestPermission()` before sending any notification.

`ACCESS_NOTIFICATION_POLICY` allows DND bypass. The user must grant this manually — call `checkDndAccess()` and redirect to `openDndSettings()` if not granted.

### Android — FCM data-only payload

The plugin's `FirebaseMessagingService` only handles **data-only** payloads (no `notification` key). Your FCM message must look like:

```json
{
  "message": {
    "token": "<device-token>",
    "data": {
      "title": "Alert",
      "body": "This is a critical alert",
      "sound": "alarm.wav",
      "android_channel_id": "critical_channel"
    }
  }
}
```

Sound files go in `android/app/src/main/res/raw/`. The file extension is stripped automatically, so `"alarm.wav"` resolves to `res/raw/alarm`.

---

## Usage

### 1. Request permission at app start

```typescript
import { CriticalAlerts } from 'critical-alerts';

const { granted, criticalAlert } = await CriticalAlerts.requestPermission();

if (!granted) {
  // User denied notifications — show rationale and redirect
  await CriticalAlerts.openAppSettings();
  return;
}

// iOS only: criticalAlert is false if the entitlement is missing or user denied it
if (!criticalAlert) {
  console.warn('Critical alert permission not granted');
}
```

### 2. Check and request DND bypass (Android)

```typescript
const { granted } = await CriticalAlerts.checkDndAccess();

if (!granted) {
  // Opens the system "Do Not Disturb access" settings page on Android.
  // On iOS this opens app notification settings instead (iOS manages DND
  // bypass automatically via the criticalAlert permission).
  await CriticalAlerts.openDndSettings();
}
```

### 3. Create a notification channel before any push arrives (Android)

Call this at app startup — **before** the first FCM message can arrive. If a push arrives before a channel is created, the plugin auto-creates one, but pre-creating lets you control every setting including DND bypass and custom sound.

```typescript
await CriticalAlerts.createChannel({
  id: 'critical_channel',
  name: 'Critical Alerts',
  description: 'High-priority alerts that bypass Do Not Disturb',
  importance: 5,          // IMPORTANCE_HIGH (5) or IMPORTANCE_MAX (not mapped, use 5)
  sound: 'alarm.wav',     // file in res/raw/, extension optional
  bypassDnd: true,        // requires ACCESS_NOTIFICATION_POLICY to be granted
  vibration: true,
  lights: true,
  lightColor: '#FF0000',
  visibility: 1,          // VISIBILITY_PUBLIC
});
```

> On iOS `createChannel` is a no-op and resolves immediately.

### 4. Get the FCM token (Android)

```typescript
// Android only — use the Firebase iOS SDK on iOS
const { token } = await CriticalAlerts.getToken();
console.log('FCM token:', token);
```

On iOS this call rejects. Use `Messaging.messaging().fcmToken` from the Firebase iOS SDK instead.

The token is also persisted in `SharedPreferences` on every `onNewToken` callback, so it survives token rotation.

### 5. Delete channels

```typescript
// Delete a specific channel by ID
await CriticalAlerts.deleteChannel({ id: 'critical_channel' });

// Delete every channel (useful during dev/reset)
await CriticalAlerts.deleteAllChannels();
```

> Both are no-ops on iOS.

### 6. Full recommended setup flow

```typescript
async function setupNotifications() {
  // Step 1 — runtime notification permission
  const perm = await CriticalAlerts.requestPermission();
  if (!perm.granted) {
    await CriticalAlerts.openAppSettings();
    return;
  }

  // Step 2 — DND bypass access (Android only, harmless on iOS)
  const dnd = await CriticalAlerts.checkDndAccess();
  if (!dnd.granted) {
    await CriticalAlerts.openDndSettings();
    // user must manually grant; re-check after they return
    return;
  }

  // Step 3 — pre-create the channel with DND bypass
  await CriticalAlerts.createChannel({
    id: 'critical_channel',
    name: 'Critical Alerts',
    importance: 5,
    sound: 'alarm',
    bypassDnd: true,
    vibration: true,
  });

  // Step 4 — get the FCM token to register with your backend
  try {
    const { token } = await CriticalAlerts.getToken();
    await registerTokenWithBackend(token);
  } catch {
    // iOS — handle token via Firebase iOS SDK
  }
}
```

---

## API

<docgen-index>

* [`requestPermission()`](#requestpermission)
* [`checkPermission()`](#checkpermission)
* [`openAppSettings()`](#openappsettings)
* [`checkDndAccess()`](#checkdndaccess)
* [`openDndSettings()`](#opendndsettings)
* [`createChannel(...)`](#createchannel)
* [`deleteChannel(...)`](#deletechannel)
* [`deleteAllChannels()`](#deleteallchannels)
* [`getToken()`](#gettoken)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>

### requestPermission()

```typescript
requestPermission() => Promise<{ granted: boolean; criticalAlert: boolean }>
```

Requests notification permission from the user.

- **iOS**: Requests `.alert`, `.sound`, `.badge`, and `.criticalAlert`. `criticalAlert` is `true` only if the app has the entitlement and the user approved it.
- **Android 13+**: Triggers the `POST_NOTIFICATIONS` runtime permission dialog.
- **Android < 13**: Resolves immediately with the current enabled state.

**Returns:** `Promise<{ granted: boolean; criticalAlert: boolean }>`

| Field | Description |
|---|---|
| `granted` | `true` if the user approved notifications |
| `criticalAlert` | `true` if critical alert permission is active (iOS only; always `false` on Android) |

--------------------

### checkPermission()

```typescript
checkPermission() => Promise<{ authorized: boolean; criticalAlert: boolean }>
```

Returns the current notification permission state without prompting the user.

**Returns:** `Promise<{ authorized: boolean; criticalAlert: boolean }>`

| Field | Description |
|---|---|
| `authorized` | `true` if notifications are enabled |
| `criticalAlert` | `true` if critical alerts are enabled (iOS only) |

--------------------

### openAppSettings()

```typescript
openAppSettings() => Promise<{ opened: boolean }>
```

Opens the app's notification settings page in the system Settings app.

**Returns:** `Promise<{ opened: boolean }>`

--------------------

### checkDndAccess()

```typescript
checkDndAccess() => Promise<{ granted: boolean }>
```

Checks whether the app has permission to bypass Do Not Disturb.

- **Android**: Checks `isNotificationPolicyAccessGranted()`. Required for `bypassDnd: true` channels to actually bypass DND.
- **iOS**: Returns `true` if `criticalAlertSetting == .enabled`. Critical alerts bypass DND automatically when this is granted — no separate DND permission exists.

**Returns:** `Promise<{ granted: boolean }>`

--------------------

### openDndSettings()

```typescript
openDndSettings() => Promise<{ opened: boolean }>
```

Opens the system page where the user can grant DND access.

- **Android**: Opens `ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS`. Requires API 23+; rejects on older versions.
- **iOS**: Opens the app's notification settings (no dedicated DND page on iOS).

**Returns:** `Promise<{ opened: boolean }>`

--------------------

### createChannel(...)

```typescript
createChannel(channel: Channel) => Promise<void>
```

Creates (or recreates) an Android notification channel. If a channel with the same `id` already exists it is deleted and recreated so all settings take effect.

**iOS**: No-op — resolves immediately.

| Param | Type |
|---|---|
| **`channel`** | [`Channel`](#channel) |

--------------------

### deleteChannel(...)

```typescript
deleteChannel(options: { id: string }) => Promise<void>
```

Deletes a single notification channel by its `id`.

**iOS**: No-op — resolves immediately.

| Param | Type |
|---|---|
| **`options`** | `{ id: string }` |

--------------------

### deleteAllChannels()

```typescript
deleteAllChannels() => Promise<void>
```

Deletes all notification channels on the device.

**iOS**: No-op — resolves immediately.

--------------------

### getToken()

```typescript
getToken() => Promise<{ token: string }>
```

Returns the current FCM registration token.

- **Android**: Calls `FirebaseMessaging.getInstance().getToken()`. The token is also cached in `SharedPreferences` on every `onNewToken` callback.
- **iOS**: **Rejects** — use `Messaging.messaging().fcmToken` from the Firebase iOS SDK instead.

**Returns:** `Promise<{ token: string }>`

--------------------

### Interfaces

#### Channel

| Prop | Type | Description | Default | Since |
|---|---|---|---|---|
| **`id`** | `string` | Unique channel identifier. | | 1.0.0 |
| **`name`** | `string` | Human-readable channel name shown in system settings. | | 1.0.0 |
| **`description`** | `string` | Optional description shown in system settings. | | 1.0.0 |
| **`sound`** | `string` | Sound file in `res/raw/`. Extension is optional (`"alarm"` or `"alarm.wav"` both work). | | 1.0.0 |
| **`importance`** | [`Importance`](#importance) | Interrupt level: `1` min … `5` max. Channels with importance ≥ `3` should have a sound. | `3` | 1.0.0 |
| **`visibility`** | [`Visibility`](#visibility) | Lockscreen visibility: `-1` secret, `0` private, `1` public. | | 1.0.0 |
| **`lights`** | `boolean` | Enable notification light on supported devices. | | 1.0.0 |
| **`lightColor`** | `string` | Notification light color (`#RRGGBB` or `#RRGGBBAA`). Requires `lights: true`. | | 1.0.0 |
| **`vibration`** | `boolean` | Enable vibration for this channel. | | 1.0.0 |
| **`bypassDnd`** | `boolean` | Allow notifications on this channel to play sound even when DND is active. Requires `ACCESS_NOTIFICATION_POLICY` to be granted. Sets `USAGE_ALARM` audio attribute. | | 1.0.0 |

### Type Aliases

#### Importance

```
1 | 2 | 3 | 4 | 5
```

`1` = min, `2` = low, `3` = default, `4` = high, `5` = max (maps to `IMPORTANCE_HIGH`).

#### Visibility

```
-1 | 0 | 1
```

`-1` = secret (hide content), `0` = private (show icon only), `1` = public (show full content on lockscreen).

</docgen-api>
