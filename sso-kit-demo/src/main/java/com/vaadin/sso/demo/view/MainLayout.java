package com.vaadin.sso.demo.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.sso.demo.components.nav.Nav;
import com.vaadin.sso.demo.components.nav.NavItem;

/**
 * The main layout used by other application views.
 */
public class MainLayout extends AppLayout {

    private final H2 viewTitle = new H2();

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassNames("view-toggle");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle.addClassNames("view-title");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("view-header");
        return header;
    }

    private Component createDrawerContent() {
        H1 appName = new H1("Auth Demo");
        appName.addClassNames("app-name");

        Div section = new Div(appName, createNavigation(), createFooter());
        section.addClassNames("drawer-section");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("app-nav");

        nav.addItem(new NavItem("Home", HomeView.class, "la la-home"));
        nav.addItem(new NavItem("Profile", ProfileView.class, "la la-user"));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("app-nav-footer");

        return layout;
    }

    private void setViewTitle(String title) {
        viewTitle.setText(title);
    }

    @Override
    public void showRouterLayoutContent(HasElement content) {
        super.showRouterLayoutContent(content);
        Class<? extends HasElement> contentClass = content.getClass();
        if (contentClass.isAnnotationPresent(PageTitle.class)) {
            String title = contentClass.getAnnotation(PageTitle.class).value();
            setViewTitle(title);
        }
    }
}
