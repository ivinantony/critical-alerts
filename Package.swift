// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CriticalAlerts",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "CriticalAlerts",
            targets: ["CriticalAlertsPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "CriticalAlertsPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/CriticalAlertsPlugin"),
        .testTarget(
            name: "CriticalAlertsPluginTests",
            dependencies: ["CriticalAlertsPlugin"],
            path: "ios/Tests/CriticalAlertsPluginTests")
    ]
)