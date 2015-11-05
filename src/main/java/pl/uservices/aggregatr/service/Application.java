package pl.uservices.aggregatr.service;

import com.ofg.config.BasicProfiles;
import com.ofg.infrastructure.environment.EnvironmentSetupVerifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new EnvironmentSetupVerifier(BasicProfiles.all()));
        application.run(args);
    }
}
