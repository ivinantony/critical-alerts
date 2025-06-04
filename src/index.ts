import { registerPlugin } from '@capacitor/core';

import type { CriticalAlertsPlugin } from './definitions';

const CriticalAlerts = registerPlugin<CriticalAlertsPlugin>('CriticalAlerts', {
  web: () => import('./web').then((m) => new m.CriticalAlertsWeb()),
  
});

export * from './definitions';
export { CriticalAlerts };
