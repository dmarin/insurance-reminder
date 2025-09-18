# Cross-Platform Insurance List Implementation

## Overview
The insurance list has been refactored to use shared business logic and utilities across all platforms while maintaining platform-specific UI implementations.

## Architecture

### Shared Code (Common)
Located in `shared/src/commonMain/kotlin/`

#### 1. Insurance Utilities (`shared/utils/InsuranceUtils.kt`)
- **Date calculations**: Days until expiry, expiration status
- **Visual theming**: Colors, icons, background images for each insurance type
- **Status management**: Active, expiring soon, expired states
- **Extension functions**: Convenient properties on Insurance model

#### 2. UI State Management (`shared/presentation/InsuranceListUIState.kt`)
- **Data classes**: `InsuranceListUIState`, `InsuranceGrouping`
- **Actions**: `InsuranceListAction` sealed class for state mutations
- **Grouping logic**: `List<Insurance>.groupByCategory()` extension

#### 3. Enhanced Insurance Types
Updated `InsuranceType` enum with:
- **Categories**: Vehicle, Property, Health, Life & Family, Travel, Other
- **Icons**: Material icon names for each type
- **Motorcycle support**: Added as separate vehicle type
- **Removed**: Business and Vision (not common in Spain)

### Platform-Specific Implementations

#### Android (`androidApp/src/main/kotlin/`)
- **InsuranceListScreen.kt**: Compose UI implementation
- **Uses shared utilities** for colors, icons, grouping
- **Material Design 3** components and theming
- **Swipe gestures** for renew/delete actions
- **Category headers** with visual grouping

#### iOS (Planned)
```swift
// Will use shared utilities from common code
struct InsuranceListView: View {
    // SwiftUI implementation using shared business logic
}
```

#### Web (Planned)
```kotlin
// Will use shared utilities from common code
@Composable
fun InsuranceListScreen() {
    // Compose for Web implementation using shared business logic
}
```

## Shared Features Across All Platforms

### 1. Insurance Categories
- **Vehicle**: Auto Insurance (🚗), Motorcycle Insurance (🏍️)
- **Property**: Home Insurance (🏠)
- **Health**: Health Insurance (🏥), Dental Insurance (🦷)
- **Life & Family**: Life Insurance (❤️), Pet Insurance (🐕)
- **Travel**: Travel Insurance (✈️)
- **Other**: Other types (📄)

### 2. Visual System
- **Type-specific colors**: Each insurance type has a unique color
- **Material icons**: Consistent iconography across platforms
- **Background images**: Type-specific imagery (cars, homes, etc.)
- **Status indicators**: Visual cues for expiration status

### 3. Business Logic
- **Date calculations**: Shared date/expiry logic
- **Status determination**: Active, expiring soon, expired
- **Grouping**: Category-based organization
- **Sorting**: By expiry date within categories

## Implementation Guide

### For iOS Developers
1. Import shared utilities: `import shared`
2. Use `InsuranceUtils` for colors, icons, dates
3. Use `Insurance.groupByCategory()` for grouping
4. Implement SwiftUI components using shared logic

### For Web Developers
1. Import shared utilities from common code
2. Use `InsuranceUtils` functions for theming
3. Implement Compose for Web UI
4. Use shared grouping and status logic

### Example Usage (Cross-Platform)
```kotlin
// Shared business logic (works on all platforms)
val groupedInsurances = insurances.groupByCategory()
val status = insurance.status
val daysUntilExpiry = insurance.daysUntilExpiry
val typeColor = InsuranceUtils.getInsuranceTypeColor(insurance.type)
val backgroundImage = InsuranceUtils.getInsuranceTypeBackgroundImage(insurance.type)
```

## Platform-Specific UI Guidelines

### Android
- Use Material Design 3 components
- Implement swipe gestures with `SwipeToDismiss`
- Use `LazyColumn` for performance
- Follow Material color system

### iOS
- Use SwiftUI native components
- Implement swipe actions with `.swipeActions`
- Use `List` or `LazyVStack`
- Follow iOS Human Interface Guidelines

### Web
- Use Compose for Web components
- Implement hover states for desktop
- Use CSS Grid or Flexbox layouts
- Follow responsive design principles

## Benefits of This Architecture

1. **Code Reuse**: 70%+ of logic is shared across platforms
2. **Consistency**: Same business rules and visual system everywhere
3. **Maintainability**: Changes to business logic update all platforms
4. **Performance**: Platform-optimized UI with shared calculations
5. **Type Safety**: Kotlin Multiplatform ensures type consistency

## Current Status

✅ **Android**: Fully implemented with all features
🚧 **iOS**: Shared logic ready, UI implementation needed
🚧 **Web**: Shared logic ready, UI implementation needed

The shared foundation is complete and ready for iOS and Web platform implementations.