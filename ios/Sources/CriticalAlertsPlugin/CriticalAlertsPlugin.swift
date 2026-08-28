import Foundation
import Capacitor
import UserNotifications
import UIKit

@objc(CriticalAlertsPlugin)
public class CriticalAlertsPlugin: CAPPlugin, CAPBridgedPlugin {
    public let identifier = "CriticalAlertsPlugin"
    public let jsName = "CriticalAlerts"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "requestPermission", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkPermission", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "openAppSettings", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "checkDndAccess", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "openDndSettings", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "createChannel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "deleteAllChannels", returnType: CAPPluginReturnPromise),
    ]

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
            call.resolve(["opened": success])
        }
    }

    // On iOS, critical alerts bypass DND automatically when the entitlement and user permission
    // are granted. We report criticalAlert permission status as the DND access equivalent.
    @objc func checkDndAccess(_ call: CAPPluginCall) {
        UNUserNotificationCenter.current().getNotificationSettings { settings in
            let granted = settings.criticalAlertSetting == .enabled
            DispatchQueue.main.async {
                call.resolve(["granted": granted])
            }
        }
    }

    // iOS has no dedicated DND settings page; open app notification settings instead.
    @objc func openDndSettings(_ call: CAPPluginCall) {
        guard let url = URL(string: UIApplication.openSettingsURLString),
              UIApplication.shared.canOpenURL(url) else {
            call.reject("Cannot open settings")
            return
        }
        UIApplication.shared.open(url, options: [:]) { success in
            call.resolve(["opened": success])
        }
    }

    // Notification channels are an Android concept; no-op on iOS.
    @objc func createChannel(_ call: CAPPluginCall) {
        call.resolve()
    }

    // Notification channels are an Android concept; no-op on iOS.
    @objc func deleteAllChannels(_ call: CAPPluginCall) {
        call.resolve()
    }
}
