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
        CAPPluginMethod(name: "deleteChannel", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "deleteAllChannels", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "getToken", returnType: CAPPluginReturnPromise),
    ]

    @objc func requestPermission(_ call: CAPPluginCall) {
        let options: UNAuthorizationOptions = [.alert, .sound, .badge, .criticalAlert]
        UNUserNotificationCenter.current().requestAuthorization(options: options) { granted, error in
            if let error = error {
                DispatchQueue.main.async { call.reject("Request failed", nil, error) }
                return
            }
            UNUserNotificationCenter.current().getNotificationSettings { settings in
                DispatchQueue.main.async {
                    call.resolve([
                        "granted": granted,
                        "criticalAlert": settings.criticalAlertSetting == .enabled
                    ])
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
    @objc func deleteChannel(_ call: CAPPluginCall) {
        call.resolve()
    }

    // Notification channels are an Android concept; no-op on iOS.
    @objc func deleteAllChannels(_ call: CAPPluginCall) {
        call.resolve()
    }

    // FCM token retrieval is handled by the Firebase iOS SDK, not this plugin.
    // Integrate FirebaseMessaging in your app and call Messaging.messaging().fcmToken.
    @objc func getToken(_ call: CAPPluginCall) {
        call.reject("getToken is not supported on iOS. Use the Firebase iOS SDK: Messaging.messaging().fcmToken")
    }
}
