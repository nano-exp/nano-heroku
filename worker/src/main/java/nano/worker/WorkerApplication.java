package nano.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Worker Application
 *
 * @author cbdyzj
 * @since 2020.9.5
 */
@SpringBootApplication(proxyBeanMethods = false)
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

}
