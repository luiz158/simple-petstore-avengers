package org.testinfected.petstore;

import org.testinfected.petstore.decoration.HtmlDocumentProcessor;
import org.testinfected.petstore.decoration.HtmlPageSelector;
import org.testinfected.petstore.decoration.LayoutTemplate;
import org.testinfected.petstore.decoration.PageCompositor;
import org.testinfected.petstore.middlewares.ApacheCommonLogger;
import org.testinfected.petstore.middlewares.ConnectionManager;
import org.testinfected.petstore.middlewares.Failsafe;
import org.testinfected.petstore.middlewares.FileServer;
import org.testinfected.petstore.middlewares.HttpMethodOverride;
import org.testinfected.petstore.middlewares.MiddlewareStack;
import org.testinfected.petstore.middlewares.ServerHeaders;
import org.testinfected.petstore.middlewares.SiteMesh;
import org.testinfected.petstore.middlewares.StaticAssets;
import org.testinfected.petstore.util.PlainFormatter;
import org.testinfected.time.lib.SystemClock;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class PetStore {

    public static final String PAGES_DIR = "app/pages";
    public static final String LAYOUT_DIR = "app/layout";
    public static final String PUBLIC_DIR = "public";

    private static final String LOGGER_NAME = "access";

    private final File context;
    private final DataSource dataSource;
    private final Logger logger = makeLogger();
    private final SystemClock clock = new SystemClock();

    private Charset outputEncoding = Charset.defaultCharset();
    private FailureReporter failureReporter = FailureReporter.IGNORE;

    public PetStore(File context, DataSource dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    public void encodeOutputAs(String charsetName) {
        encodeOutputAs(Charset.forName(charsetName));
    }

    public void encodeOutputAs(Charset encoding) {
        this.outputEncoding = encoding;
    }

    public void reportErrorsTo(FailureReporter failureReporter) {
        this.failureReporter = failureReporter;
    }

    public void logTo(Handler handler) {
        handler.setFormatter(new PlainFormatter());
        logger.addHandler(handler);
    }

    public void start(Server server) throws IOException {
        server.run(new MiddlewareStack() {{
            use(new ApacheCommonLogger(logger, clock));
            use(new Failsafe(failureReporter));
            use(new ServerHeaders(clock));
            use(new HttpMethodOverride());
            use(staticAssets());
            use(siteMesh());
            use(new ConnectionManager(dataSource));
            run(new Routing(new MustacheRendering(new File(context, PAGES_DIR)), outputEncoding));
        }});
    }

    private SiteMesh siteMesh() {
        RenderingEngine renderer = new MustacheRendering(new File(context, LAYOUT_DIR));
        SiteMesh siteMesh = new SiteMesh(new HtmlPageSelector());
        siteMesh.map("/", new PageCompositor(new HtmlDocumentProcessor(), new LayoutTemplate("main", renderer)));
        return siteMesh;
    }

    private static Logger makeLogger() {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        return logger;
    }

    private StaticAssets staticAssets() {
        final StaticAssets assets = new StaticAssets(new FileServer(new File(context, PUBLIC_DIR)));
        assets.serve("/favicon.ico", "/images", "/stylesheets", "/photos");
        return assets;
    }
}
