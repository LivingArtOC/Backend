package livart;

import livart.common.Auth.JwtLoginProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "livart")
@EnableJpaRepositories(basePackages = "livart.common")
@EntityScan(basePackages = "livart.common")
@ComponentScan(basePackages = {"livart.shop", "livart.common"})
@EnableScheduling
@EnableConfigurationProperties(JwtLoginProperties.class)
public class ShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopApplication.class, args);
    }

}
