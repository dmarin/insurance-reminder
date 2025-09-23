import SwiftUI
import shared

struct RenewInsuranceView: View {
    let insurance: Insurance
    let onRenew: (Date, Double?) -> Void

    @State private var newExpiryDate: Date
    @State private var newPrice: String = ""
    @Environment(\.dismiss) private var dismiss

    init(insurance: Insurance, onRenew: @escaping (Date, Double?) -> Void) {
        self.insurance = insurance
        self.onRenew = onRenew

        // Start with a date one year from current expiry
        let currentExpiry = insurance.expiryDate ?? Date()
        let calendar = Calendar.current
        let oneYearLater = calendar.date(byAdding: .year, value: 1, to: currentExpiry) ?? Date()
        _newExpiryDate = State(initialValue: oneYearLater)
        _newPrice = State(initialValue: insurance.currentPrice?.description ?? "")
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // Insurance info
                VStack(spacing: 12) {
                    Text("Renewing Insurance")
                        .font(.title2)
                        .fontWeight(.bold)

                    Text(insurance.name)
                        .font(.headline)
                        .foregroundColor(.secondary)

                    if let currentExpiry = insurance.expiryDate {
                        Text("Current expiry: \(currentExpiry, formatter: dateFormatter)")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)

                // Quick +1 year button
                Button(action: {
                    let calendar = Calendar.current
                    if let currentExpiry = insurance.expiryDate,
                       let oneYearLater = calendar.date(byAdding: .year, value: 1, to: currentExpiry) {
                        newExpiryDate = oneYearLater
                    }
                }) {
                    HStack {
                        Image(systemName: "plus")
                        Text("Add 1 Year")
                    }
                    .font(.headline)
                    .foregroundColor(.blue)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue.opacity(0.1))
                    .cornerRadius(12)
                }

                // Date picker
                VStack(alignment: .leading, spacing: 8) {
                    Text("New Expiry Date")
                        .font(.headline)

                    DatePicker(
                        "Select new expiry date",
                        selection: $newExpiryDate,
                        in: Date()...,
                        displayedComponents: .date
                    )
                    .datePickerStyle(WheelDatePickerStyle())
                }

                // Price field
                VStack(alignment: .leading, spacing: 8) {
                    Text("Update Price (Optional)")
                        .font(.headline)

                    HStack {
                        Image(systemName: "eurosign.circle")
                            .foregroundColor(.secondary)
                        TextField("0.00", text: $newPrice)
                            .keyboardType(.decimalPad)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                }

                Spacer()

                // Action buttons
                VStack(spacing: 12) {
                    Button(action: {
                        let priceValue = Double(newPrice)
                        onRenew(newExpiryDate, priceValue)
                    }) {
                        Text("Renew Insurance")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(12)
                    }

                    Button("Cancel") {
                        dismiss()
                    }
                    .foregroundColor(.secondary)
                }
                .padding(.horizontal)
            }
            .padding()
            .navigationTitle("Renew Insurance")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }

    private var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        return formatter
    }
}