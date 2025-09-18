// Firebase Admin SDK import script for insurance companies
// Run this with Node.js after installing firebase-admin
// npm install firebase-admin
// node firebase_import_script.js

const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

// Initialize Firebase Admin (you'll need to add your service account key)
// Download service account key from Firebase Console -> Project Settings -> Service Accounts
const serviceAccount = require('./path/to/your/serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  // Add your project ID here
  projectId: 'your-project-id'
});

const db = admin.firestore();

async function importInsuranceCompanies() {
  try {
    // Read the JSON data
    const jsonData = fs.readFileSync(path.join(__dirname, 'insurance_companies_data.json'), 'utf8');
    const companies = JSON.parse(jsonData);

    console.log(`Importing ${companies.length} insurance companies...`);

    const batch = db.batch();
    const collection = db.collection('insurance_companies');

    companies.forEach(company => {
      const docRef = collection.doc(company.id);
      batch.set(docRef, company);
    });

    await batch.commit();
    console.log('Successfully imported all insurance companies!');

    // Verify the import
    const snapshot = await collection.get();
    console.log(`Total documents in collection: ${snapshot.size}`);

  } catch (error) {
    console.error('Error importing insurance companies:', error);
  } finally {
    admin.app().delete();
  }
}

// Run the import
importInsuranceCompanies();