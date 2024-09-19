package de.fraunhofer.iese.cognac.ads.ads_platform.configuration;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.basyx.aas.aggregator.AASAggregator;
import org.eclipse.basyx.aas.aggregator.restapi.AASAggregatorProvider;
import org.eclipse.basyx.aas.registration.memory.InMemoryRegistry;
import org.eclipse.basyx.aas.registration.proxy.AASRegistryProxy;
import org.eclipse.basyx.aas.registration.restapi.AASRegistryModelProvider;
import org.eclipse.basyx.vab.protocol.http.server.BaSyxContext;
import org.eclipse.basyx.vab.protocol.http.server.VABHTTPInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServlet;

@Profile({"embedded-basyx", "test"})
@Configuration
public class EmbeddedBaSyxConfig implements InitializingBean, DisposableBean {
  private static final Logger logger = LoggerFactory.getLogger(EmbeddedBaSyxConfig.class);
  private BaSyxHTTPServer aasServer;
  private BaSyxHTTPServer aasRegistry;

  @Override
  public void afterPropertiesSet() throws Exception {
    startAASRegistry();
    startAASServer();
  }

  private void startAASServer() {
    logger.info("Starting local AAS Server");
    final BaSyxContext context = new BaSyxContext("", System.getProperty("java.io.tmpdir"), "localhost", 4001);
    context.addServletMapping("/*", new VABHTTPInterface<>(new AASAggregatorProvider(new AASAggregator(new AASRegistryProxy("http://localhost:4000")))));
    this.aasServer = new BaSyxHTTPServer(context);
    this.aasServer.start();
    logger.info("Started local AAS Server");
  }

  private void startAASRegistry() {
    logger.info("Starting local AAS Registry");
    final BaSyxContext context = new BaSyxContext("", System.getProperty("java.io.tmpdir"), "localhost", 4000);
    context.addServletMapping("/*", new VABHTTPInterface<>(new AASRegistryModelProvider(new InMemoryRegistry())));
    this.aasRegistry = new BaSyxHTTPServer(context);
    this.aasRegistry.start();
    logger.info("Started local AAS Registry");
  }

  @Override
  public void destroy() throws Exception {
    stopAASServer();
    stopAASRegistry();
  }

  private void stopAASRegistry() {
    logger.info("Stopping local AAS Registry");
    this.aasRegistry.shutdown();
    this.aasRegistry = null;
    logger.info("Stopped local AAS Registry");
  }

  private void stopAASServer() {
    logger.info("Stopping local AAS Server");
    this.aasServer.shutdown();
    this.aasServer = null;
    logger.info("Stopped local AAS Server");
  }

  public static class BaSyxHTTPServer {
    private static final Logger logger = LoggerFactory.getLogger(BaSyxHTTPServer.class);

    static {
      // Enable coding of forward slash in tomcat
      System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    }

    private final Tomcat tomcat;

    public BaSyxHTTPServer(BaSyxContext context) {
      // Instantiate and setup Tomcat server
      tomcat = new Tomcat();
      tomcat.setPort(context.getPort());
      tomcat.getConnector();

      tomcat.getEngine().setName(UUID.randomUUID().toString());


      tomcat.setHostname("localhost");
      tomcat.getHost().setAppBase(".");

      // Create servlet context
      // - Base path for resource files
      File docBase = new File(System.getProperty("java.io.tmpdir"));
      // - Create context for servlets
      Context rootCtx = tomcat.addContext("", docBase.getAbsolutePath());

      // Iterate all servlets in context
      Iterator<Map.Entry<String, HttpServlet>> it = context.entrySet().iterator();
      while (it.hasNext()) {
        // Servlet entry
        Map.Entry<String, HttpServlet> entry = it.next();

        // Servlet mapping
        String mapping = entry.getKey();
        HttpServlet servlet = entry.getValue();

        // Add new Servlet and Mapping to tomcat environment
        Tomcat.addServlet(rootCtx, Integer.toString(servlet.hashCode()), servlet);
        rootCtx.addServletMappingDecoded(mapping, Integer.toString(servlet.hashCode()));
      }
    }

    public void start() {
      logger.trace("Starting Tomcat.....");

      Thread serverThread = new Thread(() -> {
        try {
          tomcat.getServer().addLifecycleListener(new LifecycleListener() {
            @Override
            public void lifecycleEvent(LifecycleEvent event) {
              if (event.getLifecycle().getState() == LifecycleState.STARTED) {
                synchronized (tomcat) {
                  tomcat.notifyAll();
                }
              }
            }
          });
          tomcat.start();
          // Keeps the server thread alive until the server is shut down
          tomcat.getServer().await();
        } catch (LifecycleException e) {
          logger.error("Exception in start", e);
        }
      });
      serverThread.start();

      synchronized (tomcat) {
        try {
          tomcat.wait();
        } catch (InterruptedException e) {
          logger.error("Exception in start", e);
        }
      }
    }

    public void shutdown() {
      try {
        tomcat.stop();
        tomcat.destroy();
      } catch (LifecycleException e) {
        logger.error("Exception in shutdown", e);
      }
    }


  }
}
