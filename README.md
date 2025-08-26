# critical-alerts

Plugin for  Critical Alerts   in IOS & ANDROID

## Install

```bash
npm install critical-alerts
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`requestPermission()`](#requestpermission)
* [`checkPermission()`](#checkpermission)
* [`openAppSettings()`](#openappsettings)
* [`checkDndAccess()`](#checkdndaccess)
* [`openDndSettings()`](#opendndsettings)
* [`createChannel(...)`](#createchannel)
* [`deleteAllChannel()`](#deleteallchannel)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------


### requestPermission()

```typescript
requestPermission() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### checkPermission()

```typescript
checkPermission() => Promise<{ authorized: boolean; criticalAlert: boolean; }>
```

**Returns:** <code>Promise&lt;{ authorized: boolean; criticalAlert: boolean; }&gt;</code>

--------------------


### openAppSettings()

```typescript
openAppSettings() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### checkDndAccess()

```typescript
checkDndAccess() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### openDndSettings()

```typescript
openDndSettings() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### createChannel(...)

```typescript
createChannel(channel: Channel) => Promise<void>
```

| Param         | Type                                        |
| ------------- | ------------------------------------------- |
| **`channel`** | <code><a href="#channel">Channel</a></code> |

--------------------


### deleteAllChannel()

```typescript
deleteAllChannel() => Promise<void>
```

--------------------


### Interfaces


#### Channel

| Prop              | Type                                              | Description                                                                                                                                                                                                                                                | Default          | Since |
| ----------------- | ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- | ----- |
| **`id`**          | <code>string</code>                               | The channel identifier.                                                                                                                                                                                                                                    |                  | 1.0.0 |
| **`name`**        | <code>string</code>                               | The human-friendly name of this channel (presented to the user).                                                                                                                                                                                           |                  | 1.0.0 |
| **`description`** | <code>string</code>                               | The description of this channel (presented to the user).                                                                                                                                                                                                   |                  | 1.0.0 |
| **`sound`**       | <code>string</code>                               | The sound that should be played for notifications posted to this channel. Notification channels with an importance of at least `3` should have a sound. The file name of a sound file should be specified relative to the android app `res/raw` directory. |                  | 1.0.0 |
| **`importance`**  | <code><a href="#importance">Importance</a></code> | The level of interruption for notifications posted to this channel.                                                                                                                                                                                        | <code>`3`</code> | 1.0.0 |
| **`visibility`**  | <code><a href="#visibility">Visibility</a></code> | The visibility of notifications posted to this channel. This setting is for whether notifications posted to this channel appear on the lockscreen or not, and if so, whether they appear in a redacted form.                                               |                  | 1.0.0 |
| **`lights`**      | <code>boolean</code>                              | Whether notifications posted to this channel should display notification lights, on devices that support it.                                                                                                                                               |                  | 1.0.0 |
| **`lightColor`**  | <code>string</code>                               | The light color for notifications posted to this channel. Only supported if lights are enabled on this channel and the device supports it. Supported color formats are `#RRGGBB` and `#RRGGBBAA`.                                                          |                  | 1.0.0 |
| **`vibration`**   | <code>boolean</code>                              | Whether notifications posted to this channel should vibrate.                                                                                                                                                                                               |                  | 1.0.0 |
| **`bypassDnd`**   | <code>boolean</code>                              | Whether notifications posted in dnd or doze mode .                                                                                                                                                                                                         |                  | 1.0.0 |


### Type Aliases


#### Importance

<code>1 | 2 | 3 | 4 | 5</code>


#### Visibility

<code>-1 | 0 | 1</code>

</docgen-api>
