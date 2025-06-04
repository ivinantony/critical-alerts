export interface CriticalAlertsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
