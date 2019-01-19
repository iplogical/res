package com.inspirationlogical.receipt.waiter.application;

import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.inspirationlogical.receipt.corelib.repository")
@ComponentScan(basePackages = {
        "com.inspirationlogical.receipt.corelib",
        "com.inspirationlogical.receipt.waiter"
})
@EntityScan("com.inspirationlogical.receipt.corelib.model.entity")
public class WaiterApp extends AbstractJavaFxApplicationSupport {

    @Setter
    private static boolean testApplication = false;

    final private static Logger logger = LoggerFactory.getLogger(WaiterApp.class);

    public static void main(String[] args) {
        launch(WaiterApp.class, RestaurantFxmlView.class, args);
    }

//    private static void defaultExceptionHandler(Thread t, Throwable e) {
//        logger.error("Unhandled exception in WaiterApp. On thread: " + t.getName(), e);
//        ErrorMessage.showErrorMessageLong(stage.getScene().getRoot(), WaiterResources.WAITER.getString("UnhandledException"));
//    }
}

