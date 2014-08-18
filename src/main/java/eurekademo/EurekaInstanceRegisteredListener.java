package eurekademo;

import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.platform.netflix.eureka.event.EurekaInstanceRegisteredEvent;
import org.springframework.web.client.RestTemplate;

import java.net.URL;

/**
 * @author Spencer Gibb
 */
public class EurekaInstanceRegisteredListener implements ApplicationListener<EurekaInstanceRegisteredEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaInstanceRegisteredListener.class);

    @Autowired
    private Environment env;

    @Override
    public void onApplicationEvent(EurekaInstanceRegisteredEvent event) {
        try {
            InstanceInfo info = event.getInstanceInfo();
            String id = info.getAppName();
            int port = info.getPort();
            String hostName = info.getHostName();
            String adminUrl = env.getProperty("spring.boot.admin.url");
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            // register the application with the used URL and port
            String protocol = "http"; //TODO: support https
            String url = new URL(protocol, hostName, port, "").toString();
            Application app = new Application();
            app.setId(id);
            app.setUrl(url);
            template.postForObject(adminUrl + "/api/applications", app, String.class);
            LOGGER.info("Application registered itself at the admin application with ID '{}' and URL '{}'", id, url);
        } catch (Exception e) {
            LOGGER.warn("Failed to register application at spring-boot-admin", e);
        }
    }

    private static class Application {
        private String id;

        private String url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Application that = (Application) o;

            if (!id.equals(that.id)) return false;
            if (!url.equals(that.url)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + url.hashCode();
            return result;
        }
    }
}
