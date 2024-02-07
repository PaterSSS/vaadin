package com.chelyapin.study.tryVaadin;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@PWA(name = "FullSizeGrid", shortName = "FullSizeGrid")
@Push
@SpringBootApplication
@Theme(value = "test-theme")
public class TryVaadinApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(TryVaadinApplication.class, args);
	}

}
