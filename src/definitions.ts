export interface CriticalAlertsPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  requestPermission(): Promise<{ granted: boolean }>;
  checkPermission(): Promise<{ authorized: boolean; criticalAlert: boolean }>;
  checkDndAccess(): Promise<{ granted: boolean }>;
  openDndSettings(): Promise<{ granted: boolean }>;
}
