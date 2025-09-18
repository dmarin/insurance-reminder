# Insurance Reminder App Icons

This directory contains app icons for all platforms in various sizes.

## Icon Design

The icon represents:
- **Blue gradient background**: Trust and reliability
- **Green shield with checkmark**: Insurance protection and security
- **Orange clock**: Reminder functionality
- **Red notification dot**: Alert/notification indicator
- **White document lines**: Insurance policies/documents

## File Structure

```
icons/
├── android/          # Android launcher icons
│   ├── ic_launcher.png      # 192x192 (xxxhdpi)
│   ├── ic_launcher_144.png  # 144x144 (xxhdpi)
│   ├── ic_launcher_96.png   # 96x96 (xhdpi)
│   ├── ic_launcher_72.png   # 72x72 (hdpi)
│   └── ic_launcher_48.png   # 48x48 (mdpi)
├── ios/              # iOS app icons
│   ├── AppIcon-180.png      # 180x180 (iPhone 3x)
│   ├── AppIcon-120.png      # 120x120 (iPhone 2x)
│   ├── AppIcon-87.png       # 87x87 (iPhone 3x settings)
│   ├── AppIcon-80.png       # 80x80 (iPhone 2x settings)
│   └── AppIcon-60.png       # 60x60 (iPhone 1x settings)
├── web/              # Web/PWA icons
│   ├── icon-192.png         # 192x192 (PWA)
│   ├── icon-512.png         # 512x512 (PWA)
│   ├── apple-touch-icon.png # 180x180 (Apple devices)
│   └── icon.svg             # Vector version
├── app_icon_512.png  # High-res version
└── README.md         # This file
```

## Integration Instructions

### Android
1. Copy `android/ic_launcher*.png` files to `androidApp/src/main/res/mipmap-*/`
2. Update `androidApp/src/main/AndroidManifest.xml`:
   ```xml
   <application
       android:icon="@mipmap/ic_launcher"
       ... >
   ```

### iOS
1. Open `iosApp.xcodeproj` in Xcode
2. Navigate to Assets.xcassets → AppIcon
3. Drag the appropriate `ios/AppIcon-*.png` files to their corresponding slots

### Web/PWA
1. Copy `web/` files to `webApp/src/jsMain/resources/`
2. Update `webApp/src/jsMain/resources/index.html`:
   ```html
   <link rel="icon" type="image/svg+xml" href="/icon.svg">
   <link rel="apple-touch-icon" href="/apple-touch-icon.png">
   ```
3. Update web app manifest:
   ```json
   {
     "icons": [
       {
         "src": "/icon-192.png",
         "sizes": "192x192",
         "type": "image/png"
       },
       {
         "src": "/icon-512.png",
         "sizes": "512x512",
         "type": "image/png"
       }
     ]
   }
   ```

## Source Files

- Original SVG: `../app_icon.svg`
- Generated using: `rsvg-convert` from the SVG source
- License: Same as project (educational/personal use)

## Customization

To modify the icon:
1. Edit `../app_icon.svg`
2. Regenerate all sizes using the commands in the project build scripts
3. Follow the integration instructions above for each platform