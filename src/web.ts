import { WebPlugin } from '@capacitor/core';

import type { CriticalAlertsPlugin } from './definitions';

export class CriticalAlertsWeb extends WebPlugin implements CriticalAlertsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  async requestPermission(): Promise<{ granted: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false };
  }

  async checkPermission(): Promise<{ authorized: boolean; criticalAlert: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { authorized: false, criticalAlert: false };
  }
  async openAppSettings(): Promise<{ granted: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false };
  }
  
  async checkDndAccess(): Promise<{ granted: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false };
  }
  async openDndSettings(): Promise<{ granted: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false };
  }
  async createChannel(): Promise<void> {
    console.warn('CriticalAlerts plugin not available on web');
  }
}
