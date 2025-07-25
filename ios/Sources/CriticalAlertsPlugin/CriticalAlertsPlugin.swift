import Foundation
import Capacitor
import UserNotifications
import UIKit
/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CriticalAlertsPlugin)
public class CriticalAlertsPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CriticalAlertsPlugin"
    public let jsName = "CriticalAlerts"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "echo", returnType: CAPPluginReturnPromise),
         CAPPluginMethod(name: "requestPermission", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkPermission", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "openAppSettings", returnType: CAPPluginReturnPromise)
        
    ]
    private let implementation = CriticalAlerts()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }
     @objc func requestPermission(_ call: CAPPluginCall) {
        let options: UNAuthorizationOptions = [.alert, .sound, .badge, .criticalAlert]
        UNUserNotificationCenter.current().requestAuthorization(options: options) { granted, error in
            DispatchQueue.main.async {
                if let error = error {
                    call.reject("Request failed", nil, error)
                } else {
                    call.resolve(["granted": granted])
                }
            }
        }
    }

    @objc func checkPermission(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            let authorized = settings.authorizationStatus == .authorized
            let criticalEnabled = settings.criticalAlertSetting == .enabled

            DispatchQueue.main.async {
                call.resolve([
                    "authorized": authorized,
                    "criticalAlert": criticalEnabled
                ])
            }
        }
    }

   @objc func openAppSettings(_ call: CAPPluginCall) {
    guard let url = URL(string: UIApplication.openSettingsURLString),
          UIApplication.shared.canOpenURL(url) else {
        call.reject("Cannot open settings")
        return
    }

    UIApplication.shared.open(url, options: [:]) { success in
        if success {
            call.resolve(["opened": true])
        } else {
            call.reject("Failed to open settings")
        }
    }
}


}
