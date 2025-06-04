import { WebPlugin } from '@capacitor/core';

import type { CriticalAlertsPlugin } from './definitions';

export class CriticalAlertsWeb extends WebPlugin implements CriticalAlertsPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
