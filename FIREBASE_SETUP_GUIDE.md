# SMART REF PRO - Enterprise Deployment & Firebase Setup Guide

## 1. Architecture Overview
**SMART REF PRO** is architected with an offline-first Room local persistence layer coupled with production Firebase Cloud Firestore zero-trust role-based access control (RBAC).

Three distinct user tiers use the **SAME Android APK**:
1. **Owner / Super Admin**: Unrestricted administrative access, revenue & profit insights, job distribution, payroll execution, invoice approval, and branding overrides.
2. **Technician**: Access restricted strictly to assigned service jobs, work reports, spare parts entry, attendance punching, and personal salary slips.
3. **Helper**: Simplified workflow restricted to joint work with lead technicians, attendance punches, and personal salary records.

---

## 2. Step-by-Step Firebase Backend Setup

### Step 1: Create Firebase Project
1. Navigate to [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** and name it `Smart-Ref-Pro`.
3. Disable or enable Google Analytics according to company preference.

### Step 2: Register Android Application
1. Click the **Android** icon in Project Overview.
2. Enter the Package Name:
   ```
   com.aistudio.smartrefpro.kxmpzq
   ```
3. Enter App Nickname: `SMART REF PRO`.
4. Download the generated `google-services.json` file.
5. Place `google-services.json` into the `/app/` directory of this Android project.

### Step 3: Enable Firebase Authentication
1. In Firebase Console, go to **Build > Authentication > Sign-in method**.
2. Enable **Phone** (for OTP authentication) or **Google Sign-In**.
3. In Phone numbers for testing (optional for dev), add:
   - Phone: `+91 9876543210`, Test OTP: `123456` (Owner)
   - Phone: `+91 9812345671`, Test OTP: `123456` (Ramesh Kumar - Lead Tech)

### Step 4: Enable Cloud Firestore
1. In Firebase Console, go to **Build > Firestore Database**.
2. Click **Create Database** and choose your region (e.g., `asia-south1` for India).
3. Select **Start in production mode**.
4. Go to the **Rules** tab and paste the exact rules from the included `firestore.rules` file.
5. Click **Publish**.

---

## 3. Cloud Firestore Database Schema

- `company_settings/{settingId}`: Company metadata, logo paths, GSTIN, invoice prefix, terms.
- `employees/{employeeId}`: Staff profiles, roles (`OWNER`, `TECHNICIAN`, `HELPER`), salary structure.
- `jobs/{jobId}`: Service tickets, appliance brand/model, address, stage status, technician assignments.
- `invoices/{invoiceId}`: Line items, spare parts used, GST calculations, payment methods.
- `attendance/{attendanceId}`: Punch-in/out timestamps, overtime hours, approval status.
- `salaries/{salaryId}`: Confidential monthly pay slips, net wage, incentive calculations.
- `spare_parts/{partId}`: Stock inventory, cost price, selling price, low-stock threshold.
- `customers/{customerId}`: Client address, service history, appliances serviced.
- `feedbacks/{feedbackId}`: 1-5 star customer ratings and written reviews.

---

## 4. APK Build Instructions

To build a release or debug APK locally:

### Option A: Standard Debug APK
```bash
gradle :app:assembleDebug
```
The generated APK will be at:
`app/build/outputs/apk/debug/app-debug.apk`

### Option B: Production Release APK
```bash
gradle :app:assembleRelease
```
The output APK will be at:
`app/build/outputs/apk/release/app-release.apk`
