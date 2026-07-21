import Foundation
import GoogleSignIn
import Security
import Shared
import UIKit

/// Add GoogleSignIn 9.x to the iOS host with Swift Package Manager, then pass this bridge to
/// `MainViewController(apiBaseUrl:authBridge:)`. Configure the reversed client ID URL scheme
/// and keep the server client ID equal to `GOOGLE_CLIENT_IDS` on the backend.
final class AuthPlatformBridge: NSObject, IosAuthBridge {
    private weak var presentingViewController: UIViewController?
    private let keychainService = "org.speaksimpleapp.auth"
    private let keychainAccount = "current-session-v1"
    private let installMarker = "org.speaksimpleapp.auth.install-marker-v1"

    init(
        iosClientID: String,
        serverClientID: String
    ) {
        super.init()
        GIDSignIn.sharedInstance.configuration = GIDConfiguration(
            clientID: iosClientID,
            serverClientID: serverClientID
        )
        if !UserDefaults.standard.bool(forKey: installMarker) {
            SecItemDelete(baseKeychainQuery() as CFDictionary)
            UserDefaults.standard.set(true, forKey: installMarker)
        }
    }

    func attach(presentingViewController: UIViewController) {
        self.presentingViewController = presentingViewController
    }

    func signInWithGoogle(
        nonce: String,
        completion: @escaping (String?, String?) -> Void
    ) {
        // GoogleSignIn for iOS does not expose an OIDC nonce parameter. The backend therefore
        // consumes the ID-token fingerprint once, atomically with its own one-time challenge.
        guard let presenter = presentingViewController else {
            completion(nil, "configuration")
            return
        }
        GIDSignIn.sharedInstance.signIn(withPresenting: presenter) { result, error in
            if let error = error as NSError? {
                let cancelled = error.domain == kGIDSignInErrorDomain &&
                    error.code == GIDSignInError.canceled.rawValue
                completion(nil, cancelled ? "cancelled" : "google_error")
                return
            }
            guard let token = result?.user.idToken?.tokenString else {
                completion(nil, "google_error")
                return
            }
            completion(token, nil)
        }
    }

    func signOutFromGoogle() {
        GIDSignIn.sharedInstance.signOut()
    }

    func readSecureSession(completion: @escaping (String?, String?) -> Void) {
        var query = baseKeychainQuery()
        query[kSecReturnData as String] = true
        query[kSecMatchLimit as String] = kSecMatchLimitOne
        var result: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        switch status {
        case errSecSuccess:
            let value = (result as? Data).flatMap { String(data: $0, encoding: .utf8) }
            completion(value, value == nil ? "Invalid Keychain data" : nil)
        case errSecItemNotFound:
            completion(nil, nil)
        default:
            completion(nil, SecCopyErrorMessageString(status, nil) as String? ?? "Keychain read failed")
        }
    }

    func writeSecureSession(value: String, completion: @escaping (String?) -> Void) {
        guard let data = value.data(using: .utf8) else {
            completion("Unable to encode session")
            return
        }
        let query = baseKeychainQuery()
        let attributes = [kSecValueData as String: data]
        let status: OSStatus
        if SecItemCopyMatching(query as CFDictionary, nil) == errSecSuccess {
            status = SecItemUpdate(query as CFDictionary, attributes as CFDictionary)
        } else {
            var insert = query
            insert[kSecValueData as String] = data
            insert[kSecAttrAccessible as String] = kSecAttrAccessibleAfterFirstUnlockThisDeviceOnly
            status = SecItemAdd(insert as CFDictionary, nil)
        }
        completion(status == errSecSuccess ? nil :
            (SecCopyErrorMessageString(status, nil) as String? ?? "Keychain write failed"))
    }

    func clearSecureSession(completion: @escaping (String?) -> Void) {
        let status = SecItemDelete(baseKeychainQuery() as CFDictionary)
        completion(status == errSecSuccess || status == errSecItemNotFound ? nil :
            (SecCopyErrorMessageString(status, nil) as String? ?? "Keychain delete failed"))
    }

    private func baseKeychainQuery() -> [String: Any] {
        [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: keychainService,
            kSecAttrAccount as String: keychainAccount,
        ]
    }
}
