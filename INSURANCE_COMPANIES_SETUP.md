# Insurance Companies Collection Setup

This guide explains how to set up the insurance companies collection that provides a public database of insurance companies with their logos for the app.

## Overview

The insurance companies collection allows users to select from a curated list of known insurance companies instead of manually typing company names. This improves data consistency and provides a better user experience with company logos.

## Files Created

### Data Model
- `shared/src/commonMain/kotlin/com/insurancereminder/shared/model/InsuranceCompany.kt` - Data model for insurance companies
- `shared/src/commonMain/kotlin/com/insurancereminder/shared/repository/InsuranceCompanyRepository.kt` - Repository for managing companies
- `shared/src/commonMain/kotlin/com/insurancereminder/shared/service/InsuranceCompanyService.kt` - Business logic service

### UI Components
- `androidApp/src/main/kotlin/com/insurancereminder/android/ui/components/InsuranceCompanyPicker.kt` - Company picker component

### Data Files
- `insurance_companies_data.json` - JSON file with 20+ insurance companies from Spain, US, UK
- `firebase_import_script.js` - Node.js script to import companies to Firebase
- `firestore_rules_addition.txt` - Firestore security rules for public read access

## Setup Instructions

### 1. Firebase Firestore Setup

Add the rules from `firestore_rules_addition.txt` to your `firestore.rules` file:

```javascript
// Insurance companies are publicly readable by all users
match /insurance_companies/{companyId} {
  allow read: if true;
  allow write: if false; // Only admins should write
}
```

### 2. Import Insurance Companies Data

#### Option A: Using Firebase Console (Recommended for small datasets)
1. Open Firebase Console → Firestore Database
2. Create a new collection called `insurance_companies`
3. Manually add documents using the data from `insurance_companies_data.json`

#### Option B: Using Admin SDK Script
1. Install Node.js and firebase-admin:
   ```bash
   npm install firebase-admin
   ```

2. Download your service account key from Firebase Console:
   - Go to Project Settings → Service Accounts
   - Generate new private key
   - Save as `serviceAccountKey.json`

3. Update `firebase_import_script.js`:
   - Set the correct path to your service account key
   - Set your Firebase project ID

4. Run the import script:
   ```bash
   node firebase_import_script.js
   ```

### 3. Logo URLs

The current data uses placeholder URLs. For production, you should:

1. **Host company logos** in Firebase Storage or a CDN
2. **Update logo URLs** in the data to point to your hosted images
3. **Ensure proper image optimization** (WebP format, multiple sizes)
4. **Consider copyright** - use official logos only with permission

Example Firebase Storage structure:
```
gs://your-app.appspot.com/
├── company-logos/
│   ├── mapfre.svg
│   ├── sanitas.svg
│   ├── axa.svg
│   └── ...
```

## How It Works

### 1. Data Flow
1. App fetches companies from Firestore on startup
2. Companies are filtered by country and insurance type
3. User selects from dropdown with logos
4. Selected company info is stored with the insurance policy

### 2. User Experience
1. **Smart Filtering**: Only shows companies that support the selected insurance type
2. **Country-based**: Currently set to Spain ("ES") but can be made dynamic
3. **Logo Display**: Shows company logos for better recognition
4. **Fallback Option**: "Other" option allows manual company entry
5. **Real-time**: Updates automatically when new companies are added

### 3. Features
- **Public Access**: All users (including guests) can access the company list
- **Offline Support**: Can be cached for offline use
- **Search**: Can be extended with search functionality
- **Localization**: Supports multiple countries and languages

## Customization

### Adding New Companies
Add new entries to `insurance_companies_data.json` following this structure:

```json
{
  "id": "unique_company_id",
  "name": "company_name_slug",
  "displayName": "Display Name",
  "logoUrl": "https://example.com/logo.svg",
  "country": "COUNTRY_CODE",
  "supportedTypes": ["AUTO", "HOME", "HEALTH"],
  "websiteUrl": "https://company-website.com",
  "isActive": true
}
```

### Customizing for Different Countries
Update the `getCompaniesByCountryAndType` call in `AddInsuranceScreen.kt`:

```kotlin
// Instead of hardcoded "ES"
val availableCompanies by insuranceCompanyService
    .getCompaniesByCountryAndType(userCountry, selectedType)
    .collectAsState(initial = emptyList())
```

### Adding Search Functionality
Extend `InsuranceCompanyService` with search methods:

```kotlin
suspend fun searchCompanies(query: String): List<InsuranceCompany> {
    // Implement search logic
}
```

## Cross-Platform Implementation

The company picker needs to be implemented for each platform:

### iOS (Compose Multiplatform)
The `InsuranceCompanyPicker` component can be reused directly.

### Web
Create a web-specific dropdown component using HTML/CSS that follows the same interface.

### Desktop
The Compose component will work on desktop with minor adaptations for mouse interaction.

## Performance Considerations

1. **Caching**: Implement proper caching to avoid repeated network requests
2. **Lazy Loading**: Load company logos lazily as needed
3. **Pagination**: Consider pagination for large company lists
4. **Offline Support**: Cache company data locally

## Security Notes

- **Read-Only Access**: Companies collection is read-only for client apps
- **Admin-Only Writes**: Only server-side admin SDK can write to companies
- **Logo Validation**: Validate logo URLs to prevent XSS attacks
- **Rate Limiting**: Consider rate limiting for company data requests

## Maintenance

1. **Regular Updates**: Keep company information current
2. **Logo Updates**: Update logos when companies rebrand
3. **Dead Link Monitoring**: Monitor and fix broken logo URLs
4. **User Feedback**: Allow users to report missing companies

This setup provides a solid foundation for a professional insurance company selection experience while maintaining data consistency and a polished UI.