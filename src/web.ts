import { WebPlugin } from '@capacitor/core';

import type { Channel, CriticalAlertsPlugin } from './definitions';

export class CriticalAlertsWeb extends WebPlugin implements CriticalAlertsPlugin {
  async requestPermission(): Promise<{ granted: boolean; criticalAlert: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false, criticalAlert: false };
  }

  async checkPermission(): Promise<{ authorized: boolean; criticalAlert: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { authorized: false, criticalAlert: false };
  }

  async openAppSettings(): Promise<{ opened: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { opened: false };
  }

  async checkDndAccess(): Promise<{ granted: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { granted: false };
  }

  async openDndSettings(): Promise<{ opened: boolean }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { opened: false };
  }

  async createChannel(_channel: Channel): Promise<void> {
    console.warn('CriticalAlerts plugin not available on web');
  }

  async deleteChannel(_options: { id: string }): Promise<void> {
    console.warn('CriticalAlerts plugin not available on web');
  }

  async deleteAllChannels(): Promise<void> {
    console.warn('CriticalAlerts plugin not available on web');
  }

  async getToken(): Promise<{ token: string }> {
    console.warn('CriticalAlerts plugin not available on web');
    return { token: '' };
  }
}
