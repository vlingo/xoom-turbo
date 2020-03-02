package io.vlingo.xoom.config;

import io.micronaut.context.annotation.BootstrapContextCompatible;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.io.socket.SocketUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ServerConfiguration} class loads application properties from the application.yml file on the
 * classpath of the application.
 */
@ConfigurationProperties(ServerConfiguration.PREFIX)
@Primary
@BootstrapContextCompatible
@Context
public class ServerConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ServerConfiguration.class);
    public static final String PREFIX = "server";
    private Integer port = 8080;
    private String host;
    private String scheme = "http";
    private Integer maxBufferPoolSize = 100;
    private Integer maxMessageSize = 65535 * 2;
    private DispatchersConfiguration dispatchersConfiguration;
    private ProcessorsConfiguration processorsConfiguration;
    private ActorsConfiguration actorsConfiguration;

    public ServerConfiguration(DispatchersConfiguration dispatchersConfiguration,
                               ProcessorsConfiguration processorsConfiguration,
                               ActorsConfiguration actorsConfiguration) {
        this.dispatchersConfiguration = dispatchersConfiguration;
        this.processorsConfiguration = processorsConfiguration;
        this.actorsConfiguration = actorsConfiguration;
    }

    public Integer getPort() {
        if (port == -1) {
            port = SocketUtils.findAvailableTcpPort();
        }
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        if (!scheme.equals("http") && !scheme.equals("https")) {
            log.warn("The configured URL scheme must be either http or https. Defaulting to http.");
            this.scheme = "http";
        } else {
            this.scheme = scheme;
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getMaxBufferPoolSize() {
        return maxBufferPoolSize;
    }

    public void setMaxBufferPoolSize(Integer maxBufferPoolSize) {
        this.maxBufferPoolSize = maxBufferPoolSize;
    }

    public Integer getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(Integer maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public DispatchersConfiguration getDispatchersConfiguration() {
        return dispatchersConfiguration;
    }

    public void setDispatchersConfiguration(DispatchersConfiguration dispatchersConfiguration) {
        if (dispatchersConfiguration != null) {
            this.dispatchersConfiguration = dispatchersConfiguration;
        }
    }

    public ProcessorsConfiguration getProcessorsConfiguration() {
        return processorsConfiguration;
    }

    public void setProcessorsConfiguration(ProcessorsConfiguration processorsConfiguration) {
        if (processorsConfiguration != null) {
            this.processorsConfiguration = processorsConfiguration;
        }
    }

    public ActorsConfiguration getActorsConfiguration() {
        return actorsConfiguration;
    }

    public void setActorsConfiguration(ActorsConfiguration actorsConfiguration) {
        if (actorsConfiguration != null) {
            this.actorsConfiguration = actorsConfiguration;
        }
    }

    @ConfigurationProperties(DispatchersConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Context
    public static class DispatchersConfiguration {

        public static final String PREFIX = "dispatchers";

        private Float factor = 1.5f;
        private Integer count = 16;
        private Integer throttlingCount = 16;
        private Integer poolSize = 10;

        public Float getFactor() {
            return factor;
        }

        public void setFactor(Float factor) {
            this.factor = factor;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getThrottlingCount() {
            return throttlingCount;
        }

        public void setThrottlingCount(Integer throttlingCount) {
            this.throttlingCount = throttlingCount;
        }

        public Integer getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
    }

    @ConfigurationProperties(ProcessorsConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Context
    public static class ProcessorsConfiguration {

        public static final String PREFIX = "processors";

        private Integer poolSize = 10;

        public Integer getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(Integer poolSize) {
            this.poolSize = poolSize;
        }
    }

    @ConfigurationProperties(ActorsConfiguration.PREFIX)
    @BootstrapContextCompatible
    @Context
    public static class ActorsConfiguration {

        public static final String PREFIX = "actors";

        private Integer probeInterval = 2;
        private Integer probeTimeout = 3;
        private Integer requestMissingTimeout = 100;

        public Integer getProbeInterval() {
            return probeInterval;
        }

        public void setProbeInterval(Integer probeInterval) {
            this.probeInterval = probeInterval;
        }

        public Integer getProbeTimeout() {
            return probeTimeout;
        }

        public void setProbeTimeout(Integer probeTimeout) {
            this.probeTimeout = probeTimeout;
        }

        public Integer getRequestMissingTimeout() {
            return requestMissingTimeout;
        }

        public void setRequestMissingTimeout(Integer requestMissingTimeout) {
            this.requestMissingTimeout = requestMissingTimeout;
        }
    }

    private static final String banner = "\n" +
            "            _|_|_      _ _                    \n" +
            "            _|_|_     | (_)                   \n" +
            " |_|_|   _|_|_  __   _| |_ _ __   __ _  ___   \n" +
            " |_|_|   _|_|_  \\ \\ / / | | '_ \\ / _` |/ _ \\\n" +
            "     |_|_|       \\ V /| | | | | | (_| | (_) |\n" +
            "      |_|         \\_/ |_|_|_| |_|\\__, |\\___/\n" +
            "                                  __/ | Xoom v0.1.0\n" +
            "                                 |___/";

    public static String getBanner() {
        return banner;
    }
}
