# SecureVault – Human-Centred Encrypted Desktop

An editorial, premium Java Swing password manager and cryptographic vault designed with a disciplined black, white, and crimson red visual identity.

Inspired by high-end desktop productivity applications (such as Linear, Things 3, and Raycast), SecureVault avoids the generic AI aesthetic—forgoing excessive gradients, glowing borders, floating cards, and gratuitous rounded rectangles in favor of crisp typography, natural spacing, and rigorous cybersecurity engineering.

---

## Visual Design Identity

| Element | Specification | Hex Code | Purpose |
| :--- | :--- | :--- | :--- |
| **Jet Black** | Primary Dark Chrome | `#111111` | Sidebar, password focal display panel, strong headings |
| **Pure White** | Primary Light Surface | `#FFFFFF` | Content panels, inputs, cards |
| **Crimson Red** | Primary Action Accent | `#D93636` | Active navigation pill, primary action buttons, key highlights |
| **Warm Off-white** | Workspace Background | `#F3F1ED` | Main content backdrop for reduced eye strain |
| **Slate Grey** | Muted Metadata | `#777777` | Timestamps, technical descriptions, secondary hints |
| **Light Grey** | Structural Dividers | `#E8E7E4` | Crisp 1px borders, subtle separators |
| **Muted Teal** | Security Positive | `#277C70` | Verified strong entropy, secure health rating |
| **Soft Amber** | Security Warning | `#D89A35` | Moderate entropy, passwords older than 90 days |
| **Deep Burgundy** | Critical Alert | `#702B35` | Critical vulnerabilities, reused passwords, destructive actions |

---

## Architecture & Code Structure

The project is built entirely in native Java Swing (JDK 25 compatible, zero external dependencies):

```
password vault/
├── src/main/java/com/securevault/
│   ├── Main.java                          # Desktop application entry point
│   ├── crypto/
│   │   └── VaultEncryption.java           # AES-256-GCM + PBKDF2WithHmacSHA256 crypto engine
│   ├── model/
│   │   ├── Credential.java                # Secured account entity model
│   │   ├── Category.java                  # Categories (Logins, Banking, Cloud, Dev, Personal)
│   │   ├── VaultStats.java                # Dashboard metrics model
│   │   └── AuditReport.java               # Vulnerability and exposure audit model
│   ├── service/
│   │   ├── DatabaseManager.java           # Atomic encrypted binary persistent storage
│   │   └── VaultService.java              # Security orchestrator and password generator
│   └── ui/
│       ├── ThemeManager.java              # Central theme manager, anti-aliasing & styles
│       ├── AppColors.java                 # Exact hex color tokens
│       ├── Typography.java                # SF Pro / Inter / Segoe UI editorial typography
│       ├── MainFrame.java                 # Desktop-first shell and navigation coordinator
│       ├── LoginPanel.java                # Split-screen login & initialization view
│       ├── components/
│       │   ├── CustomButton.java          # Restrained button (Primary, Secondary, Ghost, Danger)
│       │   ├── CustomTextField.java       # Minimalist 1px border input with focus ring
│       │   ├── CustomPasswordField.java   # Password input with integrated eye toggle
│       │   ├── CustomToggle.java          # Minimalist toggle switch for generator options
│       │   ├── CircularScoreIndicator.java# Distinctive security health gauge
│       │   ├── HorizontalScoreIndicator.java# Real-time entropy & password strength bar
│       │   ├── ScrollablePanel.java       # Viewport-responsive panel for clean window resizing
│       │   ├── ToastNotification.java     # Microinteraction feedback overlay
│       │   └── Icons.java                 # Pure procedural Graphics2D vector icons
│       ├── dialogs/
│       │   ├── CredentialDialog.java      # Modal for adding & editing credentials
│       │   └── ConfirmationDialog.java    # Modal for deletion & destructive actions
│       └── panels/
│           ├── SidebarPanel.java          # Jet Black navigation with Crimson Red active strip
│           ├── DashboardPanel.java        # Human-centred dashboard with asymmetrical metrics
│           ├── VaultPanel.java            # Credential management table with search & filter pills
│           ├── GeneratorPanel.java        # Dedicated password generator centered on black display
│           ├── SecurityAuditPanel.java    # Full cryptographic audit with actionable recommendations
│           └── SettingsPanel.java         # Master key rotation, auto-lock & data export
├── run.sh                                 # Run script to compile & launch desktop UI
├── build.sh                               # Build script to verify tests & build dist/SecureVault.jar
└── dist/
    └── SecureVault.jar                    # Standalone executable JAR
```

---

## Key Features

1. **Split-Screen Authentication (`LoginPanel.java`)**:
   - Left side: Jet Black panel with geometric radial artwork, editorial typography ("Your passwords. Your control."), and zero-knowledge AES-256 badge.
   - Right side: Pure White minimalist authentication form with master password field, show/hide eye toggle, and Crimson Red unlock button.

2. **Editorial Dashboard (`DashboardPanel.java`)**:
   - Time-aware greeting ("Good morning, welcome back.").
   - Asymmetrical security health card with circular gauge (0–100%) and compact metrics (Total, Strong, Weak, Reused).
   - "Recently Updated Accounts" structured list with quick copy actions.

3. **Credential Management (`VaultPanel.java`)**:
   - Search filter field with instant keyword matching across service, username, and URL.
   - Filter pills (All, Logins, Finance, Cloud, Dev Keys, Personal).
   - Clean table rows with monogram initial, category tag, last rotated date, show/hide toggle, copy button, edit pencil, and delete trash actions.

4. **Dedicated Password Generator (`GeneratorPanel.java`)**:
   - Large Jet Black display panel (`#111111`) with white monospaced font and Crimson Red copy button.
   - Real-time entropy calculation (bits of entropy) and strength rating.
   - Character length slider (8 to 64) with live numeric badge.
   - Character selection toggles (Uppercase, Lowercase, Numbers, Symbols, Exclude Ambiguous).

5. **Security Audit (`SecurityAuditPanel.java`)**:
   - Evaluates overall repository health.
   - Categorizes weak passwords, reused passwords across accounts, stale passwords (>90 days), and incomplete profiles.
   - Color hierarchy: Teal for secure, Soft Amber for warnings, Crimson/Burgundy for critical issues.
   - Direct "Update Account" shortcuts to fix issues immediately.

6. **Settings & Cryptographic Storage (`SettingsPanel.java`)**:
   - Master password rotation (re-encrypts repository entries with a newly derived key).
   - Auto-lock timeout policy and automatic clipboard wipe after 45 seconds.
   - Cryptographic backup export and emergency data clear actions.

---

## How to Run & Build

### Run Directly:
```bash
./run.sh
```

### Build & Run the Standalone Executable JAR:
```bash
./build.sh
java -jar dist/SecureVault.jar
```
Default master password for the seeded demonstration vault:
```
MasterPassword2026!
```
*(Or create a new vault on first startup.)*

