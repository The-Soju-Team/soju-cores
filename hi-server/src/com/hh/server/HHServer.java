/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hh.server;

import akka.actor.ActorSystem;
import com.hh.action.HttpFilter;
import com.hh.action.ReturnFilter;
import com.hh.connector.server.Server;
import com.hh.net.httpserver.*;
import com.hh.util.ConfigUtils;
import com.hh.util.FileUtils;
import com.hh.web.HttpSession;
import com.hh.web.RamSession;
import com.hh.web.RedisSession;
import com.hh.webservice.websocket.IWebsocketConnection;
import com.hh.webservice.ws.Endpoint;
import com.typesafe.config.ConfigFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Date;

/**
 * @author vtsoft
 */
public class HHServer {

    public static org.apache.log4j.Logger mainLogger = org.apache.log4j.Logger.getLogger(HHServer.class.getSimpleName());
    public static ActorSystem system;
    public static ConfigUtils config;
    public BaseHandler handler = new BaseHandler();
    public HttpsServer httpsServer;
    public HttpServer httpServer;
    public RestImpl restImpl;
    public SoapImpl soapImpl;
    public WebImpl webImpl = new WebImpl();

    public HHServer() throws IOException {
        if (!(HttpSession.getInstance() instanceof RamSession)
                && !(HttpSession.getInstance() instanceof RedisSession)) {
            HttpSession.setConnector(RamSession.getInstance());
        }
        if (system == null) {
            String cpuNumber = config.getConfig("cpu-number");
            int intCpu = 8;
            if (cpuNumber != null && !cpuNumber.isEmpty()) {
                intCpu = Integer.parseInt(cpuNumber);
            }
            system = ActorSystem.create("HHServer", ConfigFactory.parseString(
                    "hh-dispatcher {\n"
                            + "  fork-join-executor {\n"
                            + "    parallelism-min = " + intCpu + "\n"
                            + "    parallelism-factor = 1\n"
                            + "    parallelism-max = " + intCpu + "\n"
                            + "  }\n"
                            + " }\n"
                            + " akka {\n"
                            + "  jvm-exit-on-fatal-error = false\n"
                            + " }"
            ));
        }
    }

    public static void setConfig(ConfigUtils cf) {
        config = cf;
    }

    public static void reloadBrowserCache() {
        long version = (new Date()).getTime();
        File appFolder = new File("../app/");
        FileUtils fu = new FileUtils();
        for (final File contextFolder : appFolder.listFiles()) {
            if (contextFolder.isDirectory()) {
                for (final File shareFolder : contextFolder.listFiles()) {
                    if ("share".equals(shareFolder.getName())) {
                        for (final File cacheFolder : shareFolder.listFiles()) {
                            if ("cache".equals(cacheFolder.getName())) {
                                for (final File cacheFile : cacheFolder.listFiles()) {
                                    if (cacheFile.getName().contains(".appcache")) {
                                        try {
                                            String content = fu.readFileToString(cacheFile, FileUtils.UTF_8);
                                            int startIndex = content.indexOf("version: ") + 9;
                                            String endString = content.substring(startIndex);
                                            endString = endString.substring(endString.indexOf("\n"));
                                            content = content.substring(0, startIndex) + version + endString;
                                            fu.writeStringToFile(content, cacheFile.getAbsolutePath(), FileUtils.UTF_8);
                                        } catch (Exception ex) {
                                            mainLogger.error("Error when reload browser cache", ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public HHServer setFilter(Class<? extends HttpFilter> filterClass) {
        handler.setFilter(filterClass.getName());
        return this;
    }

    public HHServer setReturnFilter(Class<? extends ReturnFilter> filterClass) {
        handler.setReturnFilter(filterClass.getName());
        return this;
    }

    public HHServer setWebsocketConnection(Class<? extends IWebsocketConnection> wsConnectionClass) {
        handler.setWebsocketConnection(wsConnectionClass.getName());
        return this;
    }

    public HHServer setRestImpl(RestImpl restImpl) {
        this.restImpl = restImpl;
        return this;
    }

    public HHServer setSoapImpl(SoapImpl soapImpl) {
        this.soapImpl = soapImpl;
        return this;
    }

    public HHServer setWebImpl(WebImpl webImpl) {
        this.webImpl = webImpl;
        return this;
    }

    public HHServer setConnector(Server server) {
        handler.setConnector(server);
        return this;
    }

    public HHServer setSessionProvider(HttpSession session) {
        HttpSession.setConnector(session);
        return this;
    }

    public void addRsAplication(String path, Class app) {
        handler.addRestHandler("/rs" + path, new ResourceConfig(app));
    }

    public void addSoapAplication(String soapBinding, String path, Class app) throws Exception {
        HttpContext context = null;
        if (config.getConfig("http-ssl").equals("true")) {
            context = httpsServer.createContext("/ws" + path);
        } else {
            context = httpServer.createContext("/ws" + path);
        }
        Endpoint endpoint = Endpoint.create(soapBinding, app.newInstance());
        endpoint.publish(context);
    }

    public void start() throws Exception {
        mainLogger.info("Server is starting ... ");
        // setup the socket address
        if (config.getConfig("http-ssl") == null) {
            HHServer.mainLogger.info("SSL must be defined in ../etc/server.conf ");
            return;
        }
        int port = Integer.parseInt(config.getConfig("http-port"));
        InetSocketAddress address = new InetSocketAddress(port);

        if ("true".equals(config.getConfig("use-browser-cache")) &&
                "true".equals(config.getConfig("reload-browser-cache-on-restart"))) {
            reloadBrowserCache();
        }

        if (config.getConfig("http-ssl").equals("true")) {
            // initialise the HTTPS server
            httpsServer = HttpsServer.create(address, 0);
            String fileName = System.getProperty("user.dir")
                    + File.separator + "config" + File.separator + config.getConfig("KeyStore");
            final SSLContext sslContext = getSslContext(fileName, config.getConfig("KeyPass"));
            final HttpsConfigurator configurator = getConfigurator(sslContext);
            httpsServer.setHttpsConfigurator(configurator);
            if (restImpl != null) restImpl.initRestService(this);
            if (webImpl != null) webImpl.initWebHandler();
            httpsServer.createContext("/", handler);
            httpsServer.setExecutor(null); // creates a default executor
            httpsServer.start();
        } else {
            httpServer = HttpServer.create(address, 0);
            if (restImpl != null) restImpl.initRestService(this);
            if (webImpl != null) webImpl.initWebHandler();
            httpServer.createContext("/", handler);
            httpServer.setExecutor(null); // creates a default executor
            httpServer.start();
        }
        Thread.sleep(15000);
        if (soapImpl != null) soapImpl.initSoapService(this);
        mainLogger.info("Server is run on " + port);
    }

    public void stop() {
        if (config.getConfig("http-ssl").equals("true")) {
            httpsServer.stop(1);
        } else {
            httpServer.stop(1);
        }
        HHServer.mainLogger.info("Shutdown server success");
    }

    public void restart() throws Exception {
        stop();
        start();
    }

    private SSLContext getSslContext(String keyStoreFile, String passPhrase) throws FileNotFoundException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("TLSv1.1");
        try (FileInputStream keyStoreStream = new FileInputStream(keyStoreFile);) {
            final KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(keyStoreStream, passPhrase.toCharArray());
            final KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, passPhrase.toCharArray());
            final TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        }
        return sslContext;
    }

    private HttpsConfigurator getConfigurator(final SSLContext sslContext) {
        return new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters params) {
                final SSLContext context = getSSLContext();
                final SSLParameters sslParams = context.getDefaultSSLParameters();
                params.setNeedClientAuth(false);
                params.setSSLParameters(sslParams);
            }
        };
    }

}
