# Hermes Android

A native Android application that surfaces the Hermes AI agent runtime as a portable, local-first AI workstation. Communicates with Hermes running inside Termux via a local WebSocket/JSON-RPC gateway.

## Target Audience

Developers, AI power users, local-agent enthusiasts, and the Android hacking/modding community.

## Prerequisites

1. **Termux** installed from [F-Droid](https://f-droid.org/packages/com.termux/) (not Play Store)
2. **Hermes** installed inside Termux
3. Android 9+ (API 28+)

## Setup

### 1. Install Termux
```bash
# Download from F-Droid
# https://f-droid.org/packages/com.termux/
```

### 2. Allow External Apps in Termux
Open Termux and run:
```bash
echo "allow-external-apps = true" >> ~/.termux/termux.properties
```

### 3. Install Hermes in Termux
```bash
curl -fsSL https://raw.githubusercontent.com/.../install.sh | bash
```

### 4. Build & Install APK
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Architecture

```
Android App (Kotlin) ←→ WebSocket/JSON-RPC ←→ Hermes (Python in Termux)
```

- **UI**: Jetpack Compose
- **DI**: Hilt
- **DB**: Room (SQLite)
- **Networking**: OkHttp WebSocket
- **Security**: Android Keystore + EncryptedSharedPreferences

## Features (MVP)

- Streaming chat with Markdown rendering
- Session persistence and search
- Tool cards (running/success/error/approval)
- Auto-approve toggle for `execute_code`
- Full Termux filesystem access
- Memory event chips inline
- Skills browser
- Config editor
- Log viewer (WebSocket frames + agent log)
- Multi-provider support (OpenAI, Anthropic, OpenRouter, custom)
- Vision (gallery/camera attachments)
- Auto-reconnect with exponential backoff
- Foreground service for connection keepalive

## GitHub Actions

The included workflow builds debug and release APKs on every push. Configure these secrets for release signing:
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`

## Security

- API keys stored in Android Keystore, never plaintext
- WebSocket over loopback only (`127.0.0.1:7823`)
- No cloud backend, no telemetry
- Key redaction in tool output

## License

MIT
