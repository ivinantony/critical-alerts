export interface CriticalAlertsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  requestPermission(): Promise<{ granted: boolean }>;
  checkPermission(): Promise<{ authorized: boolean; criticalAlert: boolean }>;
  checkDndAccess(): Promise<{ granted: boolean }>;
  openDndSettings(): Promise<{ granted: boolean }>;
 createChannel(channel: Channel): Promise<void>;
}



export interface Channel {
    /**
     * The channel identifier.
     *
     * @since 1.0.0
     */
    id: string;
    /**
     * The human-friendly name of this channel (presented to the user).
     *
     * @since 1.0.0
     */
    name: string;
    /**
     * The description of this channel (presented to the user).
     *
     * @since 1.0.0
     */
    description?: string;
    /**
     * The sound that should be played for notifications posted to this channel.
     *
     * Notification channels with an importance of at least `3` should have a
     * sound.
     *
     * The file name of a sound file should be specified relative to the android
     * app `res/raw` directory.
     *
     * @since 1.0.0
     * @example "jingle.wav"
     */
    sound?: string;
    /**
     * The level of interruption for notifications posted to this channel.
     *
     * @default `3`
     * @since 1.0.0
     */
    importance?: Importance;
    /**
     * The visibility of notifications posted to this channel.
     *
     * This setting is for whether notifications posted to this channel appear on
     * the lockscreen or not, and if so, whether they appear in a redacted form.
     *
     * @since 1.0.0
     */
    visibility?: Visibility;
    /**
     * Whether notifications posted to this channel should display notification
     * lights, on devices that support it.
     *
     * @since 1.0.0
     */
    lights?: boolean;
    /**
     * The light color for notifications posted to this channel.
     *
     * Only supported if lights are enabled on this channel and the device
     * supports it.
     *
     * Supported color formats are `#RRGGBB` and `#RRGGBBAA`.
     *
     * @since 1.0.0
     */
    lightColor?: string;
    /**
     * Whether notifications posted to this channel should vibrate.
     *
     * @since 1.0.0
     */
    vibration?: boolean;
    /**
     * Whether notifications posted in dnd or doze mode .
     *
     * @since 1.0.0
     */
    bypassDnd?: boolean;
}
export declare type Importance = 1 | 2 | 3 | 4 | 5;
export declare type Visibility = -1 | 0 | 1;