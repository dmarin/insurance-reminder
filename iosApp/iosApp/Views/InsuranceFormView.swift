import SwiftUI
import shared

struct InsuranceFormView: View {
    let insurance: Insurance?
    let onSave: (Insurance) -> Void

    @State private var name: String
    @State private var selectedType: InsuranceType
    @State private var expiryDate: Date
    @State private var reminderDays: String
    @State private var currentPrice: String
    @State private var companyName: String
    @State private var policyNumber: String

    @Environment(\.dismiss) private var dismiss

    private var isEditing: Bool {
        insurance != nil
    }

    init(insurance: Insurance? = nil, onSave: @escaping (Insurance) -> Void) {
        self.insurance = insurance
        self.onSave = onSave

        // Initialize state with insurance data or defaults
        _name = State(initialValue: insurance?.name ?? "")
        _selectedType = State(initialValue: insurance?.type ?? .auto)
        _expiryDate = State(initialValue: insurance?.expiryDate ?? Date())
        _reminderDays = State(initialValue: String(insurance?.reminderDaysBefore ?? 30))
        _currentPrice = State(initialValue: insurance?.currentPrice?.description ?? "")
        _companyName = State(initialValue: insurance?.companyName ?? "")
        _policyNumber = State(initialValue: insurance?.policyNumber ?? "")
    }

    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Basic Information")) {
                    TextField("Insurance Name", text: $name)

                    Picker("Type", selection: $selectedType) {
                        ForEach(InsuranceType.allCases, id: \.self) { type in
                            Text(type.displayName).tag(type)
                        }
                    }

                    DatePicker("Expiry Date", selection: $expiryDate, displayedComponents: .date)

                    TextField("Reminder Days", text: $reminderDays)
                        .keyboardType(.numberPad)
                }

                Section(header: Text("Details")) {
                    TextField("Current Price (€)", text: $currentPrice)
                        .keyboardType(.decimalPad)

                    TextField("Company Name", text: $companyName)

                    TextField("Policy Number", text: $policyNumber)
                }
            }
            .navigationTitle(isEditing ? "Edit Insurance" : "Add Insurance")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }

                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Save") {
                        saveInsurance()
                    }
                    .disabled(name.isEmpty)
                }
            }
        }
    }

    private func saveInsurance() {
        let updatedInsurance = Insurance(
            id: insurance?.id ?? UUID().uuidString,
            name: name,
            type: selectedType,
            expiryDate: expiryDate,
            reminderDaysBefore: Int32(reminderDays) ?? 30,
            isActive: true,
            currentPrice: Double(currentPrice),
            currency: "EUR",
            policyFileUrl: insurance?.policyFileUrl,
            policyFileName: insurance?.policyFileName,
            companyName: companyName.isEmpty ? nil : companyName,
            policyNumber: policyNumber.isEmpty ? nil : policyNumber,
            userId: insurance?.userId ?? "guest",
            sharedWithUserId: insurance?.sharedWithUserId,
            createdAt: insurance?.createdAt ?? Date(),
            updatedAt: Date()
        )

        onSave(updatedInsurance)
        dismiss()
    }
}

// Extension to make InsuranceType work with picker
extension InsuranceType: CaseIterable {
    public static var allCases: [InsuranceType] {
        return [.auto, .motorcycle, .home, .health, .dental, .life, .pet, .travel, .other]
    }

    var displayName: String {
        switch self {
        case .auto: return "Auto"
        case .motorcycle: return "Motorcycle"
        case .home: return "Home"
        case .health: return "Health"
        case .dental: return "Dental"
        case .life: return "Life"
        case .pet: return "Pet"
        case .travel: return "Travel"
        case .other: return "Other"
        }
    }
}

// Make Insurance identifiable for sheet presentation
extension Insurance: Identifiable {
}