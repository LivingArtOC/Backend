package livart;

import livart.common.Auth.JwtLoginProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JwtLoginProperties.class)
public class LivartApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivartApplication.class, args);
    }

}
