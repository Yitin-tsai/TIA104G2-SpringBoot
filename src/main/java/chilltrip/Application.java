package chilltrip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@ComponentScan(basePackages = {
	    "chilltrip",
	    "chilltrip.test",        
	    "chilltrip.test.controller"
	})
	public class Application extends SpringBootServletInitializer {
	    
	    private static final Logger logger = LoggerFactory.getLogger(Application.class);
	    
	    @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return application.sources(Application.class);
	    }
	    
	    public static void main(String[] args) {
	        try {
	            ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
	            
	            // 列出所有註冊的 Controllers
	            logger.info("=== Registered RestControllers ===");
	            String[] beanNames = context.getBeanNamesForAnnotation(RestController.class);
	            for (String beanName : beanNames) {
	                logger.info("Found controller: " + beanName);
	            }
	            
	            logger.info("Application started successfully");
	            
	        } catch (Exception e) {
	            logger.error("Error starting application", e);
	            e.printStackTrace();
	        }
	    }
	}
