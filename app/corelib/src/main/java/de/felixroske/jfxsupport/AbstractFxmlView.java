package de.felixroske.jfxsupport;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.util.ResourceBundle.getBundle;

/**
 * Base class for fxml-based view classes.
 * <p>
 * It is derived from Adam Bien's
 * <a href="http://afterburner.adam-bien.com/">afterburner.fx</a> project.
 * <p>
 * {@link AbstractFxmlView} is a stripped down version of <a href=
 * "https://github.com/AdamBien/afterburner.fx/blob/02f25fdde9629fcce50ea8ace5dec4f802958c8d/src/main/java/com/airhacks/afterburner/views/FXMLView.java"
 * >FXMLView</a> that provides DI for Java FX Controllers via Spring.
 * </p>
 * <p>
 * Supports annotation driven creation of FXML based view beans with {@link FXMLView}
 * </p>
 *
 * @author Thomas Darimont
 * @author Felix Roske
 * @author Andreas Jay
 */
public abstract class AbstractFxmlView implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFxmlView.class);

    private final ObjectProperty<Object> presenterProperty;

    private final Optional<ResourceBundle> bundle;

    private final URL resource;

    private final FXMLView annotation;

    private FXMLLoader fxmlLoader;

    private ApplicationContext applicationContext;

    private String fxmlRoot;

    public AbstractFxmlView() {
        LOGGER.debug("AbstractFxmlView construction");
        fxmlRoot = PropertyReaderHelper.determineFilePathFromPackageName(getClass());
        annotation = getFXMLAnnotation();
        resource = getURLResource(annotation);
        presenterProperty = new SimpleObjectProperty<>();
        bundle = getResourceBundle(getBundleName());
    }

    private URL getURLResource(final FXMLView annotation) {
        if (annotation != null && !annotation.value().equals("")) {
            return getClass().getResource(annotation.value());
        } else {
            return getClass().getResource(getFxmlPath());
        }
    }

    private FXMLView getFXMLAnnotation() {
        final Class<? extends AbstractFxmlView> theClass = this.getClass();
        final FXMLView annotation = theClass.getAnnotation(FXMLView.class);
        return annotation;
    }

    private Object createControllerForType(final Class<?> type) {
        return applicationContext.getBean(type);
    }


    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {

        if (this.applicationContext != null) {
            return;
        }

        this.applicationContext = applicationContext;
    }


    private FXMLLoader loadSynchronously(final URL resource, final Optional<ResourceBundle> bundle) throws IllegalStateException {

        final FXMLLoader loader = new FXMLLoader(resource, bundle.orElse(null));
        loader.setControllerFactory(this::createControllerForType);

        try {
            loader.load();
        } catch (final IOException | IllegalStateException e) {
            throw new IllegalStateException("Cannot load " + getConventionalName(), e);
        }

        return loader;
    }

    public Parent getView() {
        ensureFxmlLoaderInitialized();

        final Parent parent = fxmlLoader.getRoot();
        addCSSIfAvailable(parent);
        return parent;
    }

    private void ensureFxmlLoaderInitialized() {
        if (fxmlLoader != null) {
            return;
        }
        fxmlLoader = loadSynchronously(resource, bundle);
        presenterProperty.set(fxmlLoader.getController());
    }

    Object getController() {
        return presenterProperty.get();
    }

    private void addCSSIfAvailable(final Parent parent) {

        final List<String> list = PropertyReaderHelper.get(applicationContext.getEnvironment(), "javafx.css");
        if (!list.isEmpty()) {
            list.forEach(css -> parent.getStylesheets().add(getClass().getResource(css).toExternalForm()));
        }

        addCSSFromAnnotation(parent);

        final URL uri = getClass().getResource(getStyleSheetName());
        if (uri == null) {
            return;
        }

        final String uriToCss = uri.toExternalForm();
        parent.getStylesheets().add(uriToCss);
    }

    private void addCSSFromAnnotation(final Parent parent) {
        if (annotation != null && annotation.css().length > 0) {
            for (final String cssFile : annotation.css()) {
                final URL uri = getClass().getResource(cssFile);
                if (uri != null) {
                    final String uriToCss = uri.toExternalForm();
                    parent.getStylesheets().add(uriToCss);
                    LOGGER.debug("css file added to parent: {}", cssFile);
                } else {
                    LOGGER.warn("referenced {} css file could not be located", cssFile);
                }
            }
        }
    }

    private String getStyleSheetName() {
        return fxmlRoot + getConventionalName(".css");
    }

    private String getConventionalName(final String ending) {
        return getConventionalName() + ending;
    }

    private String getConventionalName() {
        return stripEnding(getClass().getSimpleName().toLowerCase());
    }

    private String getBundleName() {
        if (StringUtils.isEmpty(annotation.bundle())) {
            final String lbundle = getClass().getPackage().getName() + "." + getConventionalName();
            LOGGER.debug("Bundle: {} based on conventional name.", lbundle);
            return lbundle;
        }

        final String lbundle = annotation.bundle();
        LOGGER.debug("Annotated bundle: {}", lbundle);
        return lbundle;
    }

    private static String stripEnding(final String clazz) {

        if (!clazz.endsWith("view")) {
            return clazz;
        }

        return clazz.substring(0, clazz.lastIndexOf("view"));
    }

    private String getFxmlPath() {
        final String fxmlPath = fxmlRoot + getConventionalName(".fxml");
        LOGGER.debug("Determined fxmlPath: " + fxmlPath);
        return fxmlPath;
    }

    private Optional<ResourceBundle> getResourceBundle(final String name) {
        try {
            LOGGER.debug("Resource bundle: " + name);
            return Optional.of(getBundle(name,
                    new ResourceBundleControl(getResourceBundleCharset())));
        } catch (final MissingResourceException ex) {
            LOGGER.debug("No resource bundle could be determined: " + ex.getMessage());
            return Optional.empty();
        }
    }

    private Charset getResourceBundleCharset() {
        return Charset.forName(annotation.encoding());
    }

    @Override
    public String toString() {
        return "AbstractFxmlView [presenterProperty=" + presenterProperty + ", bundle=" + bundle + ", resource="
                + resource + ", fxmlRoot=" + fxmlRoot + "]";
    }

}
