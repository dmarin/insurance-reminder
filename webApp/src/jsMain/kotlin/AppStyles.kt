import org.jetbrains.compose.web.css.*

object AppStyles : StyleSheet() {
    init {
        // Define CSS custom properties for Material 3 blue theming
        ":root" style {
            property("--color-primary", "#1976D2")
            property("--color-on-primary", "#FFFFFF")
            property("--color-primary-container", "#D1E4FF")
            property("--color-on-primary-container", "#001D36")
            property("--color-secondary", "#455A64")
            property("--color-on-secondary", "#FFFFFF")
            property("--color-secondary-container", "#E1E2E1")
            property("--color-on-secondary-container", "#161D1D")
            property("--color-tertiary", "#0288D1")
            property("--color-on-tertiary", "#FFFFFF")
            property("--color-tertiary-container", "#BEEAF7")
            property("--color-on-tertiary-container", "#001F24")
            property("--color-error", "#D32F2F")
            property("--color-on-error", "#FFFFFF")
            property("--color-surface", "#FEFBFF")
            property("--color-on-surface", "#1A1C1E")
            property("--color-surface-variant", "#DFE2EB")
            property("--color-on-surface-variant", "#43474E")
            property("--color-background", "#FEFBFF")
            property("--color-on-background", "#1A1C1E")
            property("--color-outline", "#73777F")
            property("--color-outline-variant", "#C3C7CF")
            property("--shadow", "0px 2px 8px rgba(0, 0, 0, 0.1)")
        }

        // Material 3 Dark theme support
        "@media (prefers-color-scheme: dark)" style {
            ":root" style {
                property("--color-primary", "#90CAF9")
                property("--color-on-primary", "#003F5C")
                property("--color-primary-container", "#1976D2")
                property("--color-on-primary-container", "#D1E4FF")
                property("--color-secondary", "#B0BEC5")
                property("--color-on-secondary", "#1D1D1D")
                property("--color-secondary-container", "#455A64")
                property("--color-on-secondary-container", "#E1E2E1")
                property("--color-tertiary", "#81D4FA")
                property("--color-on-tertiary", "#00363F")
                property("--color-tertiary-container", "#0288D1")
                property("--color-on-tertiary-container", "#BEEAF7")
                property("--color-error", "#F28B82")
                property("--color-on-error", "#FFFFFF")
                property("--color-surface", "#0F1419")
                property("--color-on-surface", "#E1E2E1")
                property("--color-surface-variant", "#40484C")
                property("--color-on-surface-variant", "#C0C7CD")
                property("--color-background", "#0F1419")
                property("--color-on-background", "#E1E2E1")
                property("--color-outline", "#8A9297")
                property("--color-outline-variant", "#40484C")
                property("--shadow", "0px 2px 8px rgba(0, 0, 0, 0.3)")
            }
        }
    }

    val container by style {
        maxWidth(1200.px)
        margin(0.px, auto)
        padding(0.px)
        fontFamily("'Segoe UI', 'Roboto', sans-serif")
        lineHeight(1.6)
        color(Color("var(--color-on-background)"))
        backgroundColor(Color("var(--color-background)"))
        minHeight(100.vh)
    }

    val header by style {
        textAlign("center")
        marginBottom(40.px)
        paddingBottom(20.px)
        borderBottom(2.px, LineStyle.Solid, Color("#e0e0e0"))
    }

    val subtitle by style {
        color(Color("#666"))
        fontSize(18.px)
        margin(10.px, 0.px)
    }

    val authCard by style {
        maxWidth(400.px)
        margin(0.px, auto)
        padding(30.px)
        backgroundColor(Color("#f9f9f9"))
        borderRadius(12.px)
        boxShadow(0.px, 4.px, 20.px, Color("rgba(0,0,0,0.1)"))
    }

    val formCard by style {
        maxWidth(600.px)
        margin(0.px, auto)
        padding(30.px)
        backgroundColor(Color("#f9f9f9"))
        borderRadius(12.px)
        boxShadow(0.px, 4.px, 20.px, Color("rgba(0,0,0,0.1)"))
    }

    val profileCard by style {
        maxWidth(800.px)
        margin(0.px, auto)
        padding(30.px)
        backgroundColor(Color("#f9f9f9"))
        borderRadius(12.px)
        boxShadow(0.px, 4.px, 20.px, Color("rgba(0,0,0,0.1)"))
    }

    val formGroup by style {
        marginBottom(20.px)

        self + " label" style {
            display(DisplayStyle.Block)
            marginBottom(8.px)
            fontWeight("600")
            color(Color("#444"))
        }
    }

    val formInput by style {
        width(100.percent)
        padding(12.px)
        border(1.px, LineStyle.Solid, Color("#ddd"))
        borderRadius(6.px)
        fontSize(16.px)
        property("box-sizing", "border-box")

        focus {
            borderColor(Color("#007bff"))
            outline("none")
            boxShadow(0.px, 0.px, 0.px, 3.px, Color("rgba(0,123,255,0.1)"))
        }
    }

    val formSelect by style {
        width(100.percent)
        padding(12.px)
        border(1.px, LineStyle.Solid, Color("#ddd"))
        borderRadius(6.px)
        fontSize(16.px)
        backgroundColor(Color("white"))
        property("box-sizing", "border-box")

        focus {
            borderColor(Color("#007bff"))
            outline("none")
        }
    }

    val primaryButton by style {
        width(100.percent)
        padding(12.px, 24.px)
        backgroundColor(Color("var(--color-primary, #007bff)"))
        color(Color("white"))
        border(0.px)
        borderRadius(6.px)
        fontSize(16.px)
        fontWeight("600")
        cursor("pointer")
        marginBottom(10.px)

        hover {
            backgroundColor(Color("var(--color-primary-variant, #0056b3)"))
        }
    }

    val secondaryButton by style {
        width(100.percent)
        padding(12.px, 24.px)
        backgroundColor(Color("#6c757d"))
        color(Color("white"))
        border(0.px)
        borderRadius(6.px)
        fontSize(16.px)
        fontWeight("600")
        cursor("pointer")
        marginBottom(10.px)

        hover {
            backgroundColor(Color("#545b62"))
        }
    }

    val textButton by style {
        backgroundColor(Color("transparent"))
        color(Color("#007bff"))
        border(0.px)
        padding(8.px)
        fontSize(14.px)
        cursor("pointer")
        textDecoration("underline")

        hover {
            color(Color("#0056b3"))
        }
    }

    val iconButton by style {
        backgroundColor(Color("transparent"))
        border(1.px, LineStyle.Solid, Color("#ddd"))
        borderRadius(6.px)
        padding(8.px, 12.px)
        fontSize(18.px)
        cursor("pointer")
        marginRight(10.px)

        hover {
            backgroundColor(Color("#f8f9fa"))
        }
    }

    val toolbar by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.Center)
        marginBottom(30.px)
        paddingBottom(15.px)
        borderBottom(1.px, LineStyle.Solid, Color("#e0e0e0"))
    }

    val toolbarActions by style {
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
    }

    val emptyState by style {
        textAlign("center")
        padding(60.px, 20.px)
        color(Color("#666"))
    }

    val emptyIcon by style {
        fontSize(60.px)
        marginBottom(20.px)
    }

    val description by style {
        fontSize(12.px)
        color(Color("#666"))
        textAlign("center")
        marginTop(15.px)
        lineHeight(1.4)
    }

    val tierComparison by style {
        display(DisplayStyle.Grid)
        property("grid-template-columns", "1fr 1fr")
        gap(20.px)
        marginTop(20.px)
    }

    val tierCard by style {
        padding(20.px)
        backgroundColor(Color("white"))
        border(1.px, LineStyle.Solid, Color("#ddd"))
        borderRadius(8.px)

        self + " ul" style {
            listStyleType("none")
            padding(0.px)
            margin(0.px)
        }

        self + " li" style {
            padding(5.px, 0.px)
            color(Color("#555"))
        }
    }

    val premiumCard by style {
        borderColor(Color("#007bff"))
        position(Position.Relative)

        before {
            content("\"RECOMENDADO\"".quoted)
            position(Position.Absolute)
            top((-10).px)
            right(10.px)
            backgroundColor(Color("#007bff"))
            color(Color("white"))
            padding(4.px, 8.px)
            fontSize(10.px)
            borderRadius(4.px)
            fontWeight("bold")
        }
    }

    // Insurance list styles
    val insuranceGrid by style {
        display(DisplayStyle.Grid)
        property("grid-template-columns", "repeat(auto-fill, minmax(300px, 1fr))")
        gap(20.px)
        marginTop(20.px)

        media(mediaMaxWidth(768.px)) {
            self style {
                property("grid-template-columns", "1fr")
                gap(16.px)
            }
        }

        media(mediaMinWidth(1200.px)) {
            self style {
                property("grid-template-columns", "repeat(auto-fill, minmax(350px, 1fr))")
                gap(24.px)
            }
        }
    }

    val categorySection by style {
        marginBottom(40.px)
    }

    val categoryHeader by style {
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        marginBottom(16.px)

        self + " h3" style {
            margin(0.px)
            fontSize(20.px)
            fontWeight("600")
            color(Color("#333"))
        }

        self + "::after" style {
            content("\"\"".quoted)
            flexGrow(1)
            height(1.px)
            backgroundColor(Color("#e0e0e0"))
            marginLeft(16.px)
        }
    }

    val insuranceCard by style {
        backgroundColor(Color("var(--color-surface, white)"))
        borderRadius(12.px)
        padding(20.px)
        boxShadow(0.px, 2.px, 8.px, Color("rgba(0,0,0,0.1)"))
        border(1.px, LineStyle.Solid, Color("var(--color-outline, #f0f0f0)"))
        transition("all", 0.2.s)
        position(Position.Relative)
        overflow("hidden")
        color(Color("var(--color-on-surface, #333)"))

        hover {
            boxShadow(0.px, 4.px, 16.px, Color("rgba(0,0,0,0.15)"))
            transform { translateY((-2).px) }
        }
    }

    val cardHeader by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.FlexStart)
        marginBottom(16.px)
    }

    val typeIcon by style {
        width(48.px)
        height(48.px)
        borderRadius(50.percent)
        display(DisplayStyle.Flex)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
        fontSize(24.px)
        color(Color("white"))
        fontWeight("bold")
    }

    val statusBadge by style {
        fontSize(12.px)
        fontWeight("600")
        padding(4.px, 8.px)
        borderRadius(12.px)
        color(Color("white"))
    }

    val cardContent by style {
        marginBottom(16.px)

        self + " h4" style {
            margin(0.px, 0.px, 8.px, 0.px)
            fontSize(18.px)
            fontWeight("600")
            color(Color("#333"))
            lineHeight(1.3)
        }

        self + " p" style {
            margin(4.px, 0.px)
            fontSize(14.px)
            color(Color("#666"))
        }
    }

    val cardActions by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.Center)
    }

    val priceText by style {
        fontSize(20.px)
        fontWeight("700")
        color(Color("#333"))
    }

    val actionButtons by style {
        display(DisplayStyle.Flex)
        gap(8.px)
    }

    val actionButton by style {
        padding(8.px, 12.px)
        border(1.px, LineStyle.Solid, Color("#ddd"))
        borderRadius(6.px)
        backgroundColor(Color("white"))
        cursor("pointer")
        fontSize(12.px)
        fontWeight("500")
        transition("all", 0.2.s)

        hover {
            backgroundColor(Color("#f8f9fa"))
            borderColor(Color("#007bff"))
        }
    }

    val renewButton by style {
        color(Color("#007bff"))
        borderColor(Color("#007bff"))

        hover {
            backgroundColor(Color("#007bff"))
            color(Color("white"))
        }
    }

    val deleteButton by style {
        color(Color("#dc3545"))
        borderColor(Color("#dc3545"))

        hover {
            backgroundColor(Color("#dc3545"))
            color(Color("white"))
        }
    }

    // Simplified theme-based insurance colors
    val primaryInsurance by style {
        backgroundColor(Color("var(--color-primary)"))
    }

    val secondaryInsurance by style {
        backgroundColor(Color("var(--color-secondary)"))
    }

    val successInsurance by style {
        backgroundColor(Color("var(--color-success)"))
    }

    val errorInsurance by style {
        backgroundColor(Color("var(--color-error)"))
    }

    val warningInsurance by style {
        backgroundColor(Color("var(--color-secondary)"))
    }

    val infoInsurance by style {
        backgroundColor(Color("var(--color-info)"))
    }

    val outlineInsurance by style {
        backgroundColor(Color("var(--color-outline)"))
    }
}