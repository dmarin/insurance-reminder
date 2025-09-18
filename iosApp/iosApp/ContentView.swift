import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = InsuranceViewModel()
    @Environment(\.colorScheme) var colorScheme

    var body: some View {
        NavigationView {
            VStack {
                if viewModel.isLoading {
                    ProgressView("Cargando seguros...")
                        .padding()
                } else if viewModel.insurances.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "shield.fill")
                            .font(.system(size: 60))
                            .foregroundColor(materialBlue)

                        Text("No hay seguros añadidos")
                            .font(.title2)
                            .fontWeight(.semibold)

                        Text("Toca + para añadir tu primer seguro")
                            .font(.body)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    List {
                        ForEach(viewModel.insurances, id: \.id) { insurance in
                            InsuranceRow(insurance: insurance)
                        }
                        .onDelete(perform: deleteInsurance)
                    }
                }
            }
            .navigationTitle("Recordatorio de Seguros")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        // Add insurance action
                    }) {
                        Image(systemName: "plus")
                            .foregroundColor(.white)
                    }
                }
            }
            .toolbarBackground(materialBlue, for: .navigationBar)
            .toolbarBackground(.visible, for: .navigationBar)
            .toolbarColorScheme(.dark, for: .navigationBar)
        }
        .preferredColorScheme(nil)
        .onAppear {
            viewModel.loadInsurances()
        }
    }

    private var materialBlue: Color {
        colorScheme == .dark ? Color(red: 0.565, green: 0.792, blue: 0.976) : Color(red: 0.098, green: 0.463, blue: 0.824)
    }

    private func deleteInsurance(at offsets: IndexSet) {
        // Delete insurance implementation
    }
}

struct InsuranceRow: View {
    let insurance: Insurance

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                VStack(alignment: .leading, spacing: 4) {
                    Text(insurance.name)
                        .font(.headline)

                    Text(insurance.type.displayName)
                        .font(.subheadline)
                        .foregroundColor(.secondary)

                    if let company = insurance.companyName {
                        Text(company)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 4) {
                    if let price = insurance.currentPrice {
                        Text("€\(String(format: "%.2f", price))")
                            .font(.title3)
                            .fontWeight(.semibold)
                    }

                    if insurance.policyFileUrl != nil {
                        Image(systemName: "paperclip")
                            .foregroundColor(.blue)
                    }
                }
            }

            let daysUntilExpiry = DateUtils().daysUntilExpiry(expiryDate: insurance.expiryDate)
            let isExpired = DateUtils().isExpired(expiryDate: insurance.expiryDate)
            let isExpiringSoon = DateUtils().isExpiringSoon(
                expiryDate: insurance.expiryDate,
                reminderDaysBefore: Int32(insurance.reminderDaysBefore)
            )

            VStack(alignment: .leading, spacing: 2) {
                Text("Expira: \(formatDate(insurance.expiryDate))")
                    .font(.caption)

                Text(expiryText(daysUntilExpiry: daysUntilExpiry, isExpired: isExpired, isExpiringSoon: isExpiringSoon))
                    .font(.caption)
                    .foregroundColor(expiryColor(isExpired: isExpired, isExpiringSoon: isExpiringSoon))
            }

            if let policyNumber = insurance.policyNumber {
                Text("Póliza: \(policyNumber)")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
    }

    private func formatDate(_ date: kotlinx_datetime.LocalDate) -> String {
        return "\(date.monthNumber)/\(date.dayOfMonth)/\(date.year)"
    }

    private func expiryText(daysUntilExpiry: Int32, isExpired: Bool, isExpiringSoon: Bool) -> String {
        if isExpired {
            return "EXPIRADO"
        } else if isExpiringSoon {
            return "Expira en \(daysUntilExpiry) días"
        } else {
            return "\(daysUntilExpiry) días restantes"
        }
    }

    private func expiryColor(isExpired: Bool, isExpiringSoon: Bool) -> Color {
        if isExpired {
            return .red
        } else if isExpiringSoon {
            return .orange
        } else {
            return materialBlue
        }
    }

    private var materialBlue: Color {
        Color(red: 0.098, green: 0.463, blue: 0.824)
    }
}

class InsuranceViewModel: ObservableObject {
    @Published var insurances: [Insurance] = []
    @Published var isLoading = false
    @Published var error: String?

    func loadInsurances() {
        isLoading = true
        // Implementation would use shared Kotlin code
        // For now, just simulate loading
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.isLoading = false
            // Sample data would come from shared module
            self.insurances = []
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}