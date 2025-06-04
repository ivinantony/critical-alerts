import Foundation

@objc public class CriticalAlerts: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
