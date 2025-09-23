import SwiftUI
import shared

struct InsuranceListView: View {
    @StateObject private var viewModel = InsuranceListViewModel()
    @Environment(\.horizontalSizeClass) private var horizontalSizeClass
    @Environment(\.verticalSizeClass) private var verticalSizeClass
    @State private var showingDeleteAlert = false
    @State private var insuranceToDelete: Insurance?
    @State private var showingRenewInsurance: Insurance?
    @State private var showingEditInsurance: Insurance?
    @State private var showingAddInsurance = false

    private var isTablet: Bool {
        horizontalSizeClass == .regular && verticalSizeClass == .regular
    }

    private var columns: Int {
        if isTablet {
            return horizontalSizeClass == .regular ? 2 : 1
        }
        return 1
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if viewModel.isLoading {
                    ProgressView("Loading insurances...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if viewModel.insurances.isEmpty {
                    EmptyStateView()
                } else {
                    InsuranceList(
                        insurances: viewModel.insurances,
                        columns: columns,
                        isTablet: isTablet
                    )
                }
            }
            .navigationTitle("Insurance Reminders")
            .navigationBarTitleDisplayMode(.large)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Menu {
                        Button(action: { /* Add insurance */ }) {
                            Label("Add Insurance", systemImage: "plus.circle")
                        }
                        Button(action: { /* Profile */ }) {
                            Label("Profile", systemImage: "person.circle")
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                    }
                }

                ToolbarItem(placement: .navigationBarTrailing) {
                    Button(action: {
                        showingAddInsurance = true
                    }) {
                        Image(systemName: "plus")
                            .foregroundColor(.accentColor)
                    }
                }
            }
        }
        .navigationViewStyle(StackNavigationViewStyle())
        .onAppear {
            viewModel.loadInsurances()
        }
        .alert("Delete Insurance", isPresented: $showingDeleteAlert) {
            Button("Delete", role: .destructive) {
                if let insurance = insuranceToDelete {
                    viewModel.deleteInsurance(insurance)
                    insuranceToDelete = nil
                }
            }
            Button("Cancel", role: .cancel) {
                insuranceToDelete = nil
            }
        } message: {
            if let insurance = insuranceToDelete {
                Text("Are you sure you want to delete \(insurance.name)? This action cannot be undone.")
            }
        }
        .sheet(item: $showingEditInsurance) { insurance in
            InsuranceFormView(insurance: insurance) { updatedInsurance in
                viewModel.updateInsurance(updatedInsurance)
                showingEditInsurance = nil
            }
        }
        .sheet(item: $showingRenewInsurance) { insurance in
            RenewInsuranceView(insurance: insurance) { newDate, newPrice in
                // Create updated insurance with new expiry date and optional price
                let renewedInsurance = Insurance(
                    id: insurance.id,
                    name: insurance.name,
                    type: insurance.type,
                    expiryDate: newDate,
                    reminderDaysBefore: insurance.reminderDaysBefore,
                    isActive: insurance.isActive,
                    currentPrice: newPrice ?? insurance.currentPrice,
                    currency: insurance.currency,
                    policyFileUrl: insurance.policyFileUrl,
                    policyFileName: insurance.policyFileName,
                    companyName: insurance.companyName,
                    policyNumber: insurance.policyNumber,
                    userId: insurance.userId,
                    sharedWithUserId: insurance.sharedWithUserId,
                    createdAt: insurance.createdAt,
                    updatedAt: Date()
                )
                viewModel.updateInsurance(renewedInsurance)
                showingRenewInsurance = nil
            }
        }
        .sheet(isPresented: $showingAddInsurance) {
            InsuranceFormView { newInsurance in
                viewModel.addInsurance(newInsurance)
                showingAddInsurance = false
            }
        }
    }
}

struct InsuranceList: View {
    let insurances: [Insurance]
    let columns: Int
    let isTablet: Bool

    private var groupedInsurances: [InsuranceGrouping] {
        // Using shared logic from Kotlin Multiplatform
        return insurances.groupByCategory()
    }

    var body: some View {
        if columns == 1 {
            // Single column for iPhone
            List {
                ForEach(groupedInsurances, id: \.category) { group in
                    Section(header: CategoryHeader(title: group.category)) {
                        ForEach(group.insurances, id: \.id) { insurance in
                            InsuranceCardView(insurance: insurance, isCompact: false)
                                .onTapGesture {
                                    showingEditInsurance = insurance
                                }
                                .swipeActions(edge: .trailing) {
                                    Button("Delete", role: .destructive) {
                                        insuranceToDelete = insurance
                                        showingDeleteAlert = true
                                    }
                                    .tint(.red)
                                }
                                .swipeActions(edge: .leading) {
                                    Button("Renew") {
                                        viewModel.renewInsurance(insurance)
                                    }
                                    .tint(.blue)
                                }
                        }
                    }
                }
            }
            .listStyle(InsetGroupedListStyle())
        } else {
            // Grid layout for iPad
            ScrollView {
                LazyVStack(spacing: 20) {
                    ForEach(groupedInsurances, id: \.category) { group in
                        VStack(alignment: .leading, spacing: 12) {
                            CategoryHeader(title: group.category)
                                .padding(.horizontal)

                            LazyVGrid(
                                columns: Array(repeating: GridItem(.flexible(), spacing: 16), count: columns),
                                spacing: 16
                            ) {
                                ForEach(group.insurances, id: \.id) { insurance in
                                    InsuranceCardView(insurance: insurance, isCompact: true)
                                        .contextMenu {
                                            Button(action: {
                                                showingEditInsurance = insurance
                                            }) {
                                                Label("Edit", systemImage: "pencil")
                                            }
                                            Button(action: { viewModel.renewInsurance(insurance) }) {
                                                Label("Renew", systemImage: "arrow.clockwise")
                                            }
                                            Button(action: {
                                                insuranceToDelete = insurance
                                                showingDeleteAlert = true
                                            }, role: .destructive) {
                                                Label("Delete", systemImage: "trash")
                                            }
                                        }
                                }
                            }
                            .padding(.horizontal)
                        }
                    }
                }
                .padding(.vertical)
            }
        }
    }
}

struct InsuranceCardView: View {
    let insurance: Insurance
    let isCompact: Bool

    // Using shared theme mapping with iOS system colors
    private var typeColor: Color {
        let themeRole = InsuranceThemeMapping.shared.getThemeRole(type: insurance.type)

        switch themeRole {
        case .primary: return .accentColor
        case .secondary: return .orange
        case .success: return .green
        case .error: return .red
        case .warning: return .orange
        case .info: return .blue
        case .surface: return Color(.systemBackground)
        case .outline: return Color(.systemGray)
        default: return .accentColor
        }
    }

    private var statusColor: Color {
        switch insurance.status {
        case .active: return .green
        case .expiringSoon: return .orange
        case .expired: return .red
        }
    }

    private var daysUntilExpiry: Int32 {
        return insurance.daysUntilExpiry
    }

    var body: some View {
        if isCompact {
            // Compact card layout for iPad grid
            VStack(spacing: 12) {
                HStack {
                    ZStack {
                        Circle()
                            .fill(typeColor)
                            .frame(width: 40, height: 40)

                        Image(systemName: getSystemIcon(for: insurance.type))
                            .foregroundColor(.white)
                            .font(.title3)
                    }

                    Spacer()

                    Text("\(daysUntilExpiry)d")
                        .font(.caption)
                        .fontWeight(.medium)
                        .foregroundColor(statusColor)
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text(insurance.name)
                        .font(.headline)
                        .lineLimit(2)
                        .multilineTextAlignment(.leading)

                    Text(insurance.type.displayName)
                        .font(.subheadline)
                        .foregroundColor(.secondary)

                    if let companyName = insurance.companyName {
                        Text(companyName)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                if let price = insurance.currentPrice {
                    Text("€\(Int(price))")
                        .font(.title2)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)
                }
            }
            .padding()
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: .gray.opacity(0.2), radius: 4)
        } else {
            // Full row layout for iPhone list
            HStack {
                // Type icon with company logo overlay
                ZStack {
                    Circle()
                        .fill(typeColor)
                        .frame(width: 48, height: 48)

                    Image(systemName: getSystemIcon(for: insurance.type))
                        .foregroundColor(.white)
                        .font(.title2)

                    // Company logo overlay (if available)
                    if let logoUrl = insurance.companyLogoUrl {
                        AsyncImage(url: URL(string: logoUrl)) { image in
                            image
                                .resizable()
                                .aspectRatio(contentMode: .fill)
                        } placeholder: {
                            EmptyView()
                        }
                        .frame(width: 20, height: 20)
                        .clipShape(Circle())
                        .offset(x: 14, y: 14)
                    }
                }

                VStack(alignment: .leading, spacing: 4) {
                    Text(insurance.name)
                        .font(.headline)
                        .lineLimit(1)

                    Text(insurance.type.displayName)
                        .font(.subheadline)
                        .foregroundColor(.secondary)

                    if let companyName = insurance.companyName {
                        Text(companyName)
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }

                Spacer()

                VStack(alignment: .trailing, spacing: 4) {
                    // Price
                    if let price = insurance.currentPrice {
                        Text("€\(Int(price))")
                            .font(.headline)
                            .fontWeight(.bold)
                    }

                    // Status badge
                    Text(getStatusText(for: insurance))
                        .font(.caption)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(statusColor)
                        .foregroundColor(.white)
                        .clipShape(Capsule())
                }
            }
            .padding(.vertical, 4)
        }
    }

    private func getSystemIcon(for type: InsuranceType) -> String {
        // Using shared icon names from Kotlin Multiplatform
        let iconName = InsuranceUtils.shared.getInsuranceTypeIcon(type: type)

        // Map to iOS system icons
        switch iconName {
        case "directions_car": return "car.fill"
        case "directions_bike": return "bicycle"
        case "home": return "house.fill"
        case "local_hospital": return "cross.fill"
        case "favorite": return "heart.fill"
        case "pets": return "pawprint.fill"
        case "flight": return "airplane"
        case "category": return "folder.fill"
        default: return "questionmark.circle.fill"
        }
    }

    private func getStatusText(for insurance: Insurance) -> String {
        // Using shared business logic
        let daysUntilExpiry = insurance.daysUntilExpiry

        switch insurance.status {
        case .expired: return "EXPIRED"
        case .expiringSoon: return "\(daysUntilExpiry)d"
        case .active: return "\(daysUntilExpiry)d"
        }
    }
}

struct CategoryHeader: View {
    let title: String

    var body: some View {
        HStack {
            Text(title)
                .font(.headline)
                .fontWeight(.bold)
                .foregroundColor(.primary)

            Rectangle()
                .fill(Color.secondary.opacity(0.3))
                .frame(height: 1)
        }
        .padding(.vertical, 8)
    }
}

struct EmptyStateView: View {
    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: "shield.fill")
                .font(.system(size: 80))
                .foregroundColor(.blue.opacity(0.6))

            VStack(spacing: 8) {
                Text("No insurances yet")
                    .font(.title2)
                    .fontWeight(.medium)

                Text("Add your first insurance policy to get started with tracking and reminders")
                    .font(.body)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }

            Button("Add First Insurance") {
                // Navigate to add insurance
            }
            .buttonStyle(.borderedProminent)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

extension UIColor {
    convenience init(rgb: Int) {
        self.init(
            red: CGFloat((rgb >> 16) & 0xFF) / 255.0,
            green: CGFloat((rgb >> 8) & 0xFF) / 255.0,
            blue: CGFloat(rgb & 0xFF) / 255.0,
            alpha: 1.0
        )
    }
}

// ViewModel would use shared business logic
class InsuranceListViewModel: ObservableObject {
    @Published var insurances: [Insurance] = []
    @Published var isLoading = false
    @Published var error: String?

    // This would use shared InsuranceUseCases from Kotlin Multiplatform
    func loadInsurances() {
        // Implementation using shared code
    }

    func deleteInsurance(_ insurance: Insurance) {
        // Remove from local list immediately for UI responsiveness
        insurances.removeAll { $0.id == insurance.id }

        // Use shared Kotlin code to delete from repository
        // Implementation would call shared InsuranceUseCases.deleteInsurance(insurance.id)
    }

    func renewInsurance(_ insurance: Insurance) {
        // Show date picker to select new expiry date
        showingRenewInsurance = insurance
    }

    func addInsurance(_ insurance: Insurance) {
        // Add to local list immediately for UI responsiveness
        insurances.append(insurance)

        // Use shared Kotlin code to add to repository
        // Implementation would call shared InsuranceUseCases.addInsurance(insurance)
    }

    func updateInsurance(_ insurance: Insurance) {
        // Update in local list immediately for UI responsiveness
        if let index = insurances.firstIndex(where: { $0.id == insurance.id }) {
            insurances[index] = insurance
        }

        // Use shared Kotlin code to update in repository
        // Implementation would call shared InsuranceUseCases.updateInsurance(insurance)
    }
}