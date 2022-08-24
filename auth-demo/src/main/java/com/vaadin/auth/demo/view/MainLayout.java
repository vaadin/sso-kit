package com.vaadin.auth.demo.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;

/**
 * The main layout used by other application views.
 */
public class MainLayout extends AppLayout {

    private final H1 appTitle = new H1();

    private final H2 viewTitle = new H2();

    private final HorizontalLayout wrapper;

    private final VerticalLayout viewHeader;

    public MainLayout() {
        appTitle.setText("Auth Demo");

        // @formatter:off
        appTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("line-height", "var(--lumo-size-l)")
                .set("margin", "0 var(--lumo-space-m)");
        // @formatter:on

        Tabs views = getPrimaryNavigation();

        DrawerToggle toggle = new DrawerToggle();

        // @formatter:off
        viewTitle.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        // @formatter:on

        this.wrapper = new HorizontalLayout(toggle, viewTitle);
        wrapper.setAlignItems(Alignment.CENTER);
        wrapper.setSpacing(false);
        wrapper.setWidthFull();

        this.viewHeader = new VerticalLayout(wrapper);
        viewHeader.setPadding(false);
        viewHeader.setSpacing(false);

        addToDrawer(appTitle, views);
        addToNavbar(viewHeader);

        setPrimarySection(Section.DRAWER);
    }

    private Tabs getPrimaryNavigation() {
        Tabs tabs = new Tabs();
        tabs.add(createTab(new Icon("vaadin", "home"), "Home", HomeView.class));
        tabs.add(createTab(new Icon("vaadin", "user"), "Profile",
                ProfileView.class));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSelectedIndex(1);
        return tabs;
    }

    private Tab createTab(Icon icon, String viewName,
            Class<? extends Component> viewClass) {
        // @formatter:off
        icon.getStyle()
                .set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");
        // @formatter:on

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));

        link.setRoute(viewClass);
        link.setTabIndex(-1);

        return new Tab(link);
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
