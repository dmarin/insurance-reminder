# Firebase Permissions Fix Guide

## Problem
The app is crashing with `PERMISSION_DENIED: Missing or insufficient permissions` when trying to add new insurance policies.

## Root Cause
The Firestore security rules are not properly configured to allow authenticated users to read/write their own insurance data.

## Solution

### 1. Update Firestore Rules
Replace your current `firestore.rules` file with the content from the `firestore.rules` file in this project:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Insurance companies are publicly readable by all users (including anonymous)
    match /insurance_companies/{companyId} {
      allow read: if true;
      allow write: if false; // Only admins should write companies via admin SDK
    }

    // User's insurance data - only accessible by the owner
    match /insurances/{insuranceId} {
      // Allow read if user is authenticated and the document belongs to them
      allow read: if request.auth != null &&
                     (resource == null || resource.data.userId == request.auth.uid);

      // Allow write if user is authenticated and they're setting their own userId
      allow write: if request.auth != null &&
                      (resource == null || resource.data.userId == request.auth.uid) &&
                      request.resource.data.userId == request.auth.uid;

      // Allow create if user is authenticated and setting their own userId
      allow create: if request.auth != null &&
                       request.resource.data.userId == request.auth.uid;

      // Allow update if user is authenticated and owns the document
      allow update: if request.auth != null &&
                       resource.data.userId == request.auth.uid &&
                       request.resource.data.userId == request.auth.uid;

      // Allow delete if user is authenticated and owns the document
      allow delete: if request.auth != null &&
                       resource.data.userId == request.auth.uid;
    }

    // User profiles
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 2. Deploy Rules to Firebase
```bash
firebase deploy --only firestore:rules
```

### 3. Test Authentication
Make sure the user is properly authenticated before trying to add insurance:

1. Check that Firebase Auth is properly initialized
2. Verify the user is logged in before accessing Firestore
3. Ensure the `userId` field is properly set on new documents

### 4. Debugging Steps
If the issue persists:

1. **Check Firebase Console**:
   - Go to Firestore → Rules
   - Verify the rules are deployed correctly
   - Check the timestamp of last deployment

2. **Test Rules Simulator**:
   - In Firestore console, use the Rules Playground
   - Test with your user ID and document structure

3. **Check User Authentication**:
   ```kotlin
   // Add this debug code to verify auth state
   Firebase.auth.currentUser?.let { user ->
       Log.d("Auth", "User ID: ${user.uid}")
       Log.d("Auth", "User Email: ${user.email}")
   } ?: Log.d("Auth", "No user authenticated")
   ```

4. **Verify Document Structure**:
   Make sure the insurance document includes the `userId` field:
   ```kotlin
   val insurance = Insurance(
       // ... other fields
       userId = Firebase.auth.currentUser?.uid // This is critical
   )
   ```

### 5. Alternative Quick Fix (Development Only)
If you need a quick fix for development, you can temporarily use more permissive rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

**⚠️ WARNING**: Never use this in production as it allows any authenticated user to access all data.

## What's Fixed

With the proper rules:
- ✅ Authenticated users can create insurance documents with their userId
- ✅ Users can only read/write their own insurance data
- ✅ Insurance companies collection is publicly readable
- ✅ Users can manage their own profile data
- ✅ Proper security boundaries are maintained

## Next Steps

After fixing the permissions:
1. Test adding a new insurance policy
2. Verify the insurance appears in the list
3. Test editing and deleting existing policies
4. Import the insurance companies data using the provided script