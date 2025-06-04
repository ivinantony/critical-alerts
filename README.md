# critical-alerts

Plugin for iOS Critical Alerts

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

</docgen-api>
