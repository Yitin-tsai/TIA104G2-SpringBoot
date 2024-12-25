package chilltrip.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateUtil {
	private static StandardServiceRegistry registry;
	private static final SessionFactory sessionFactory = createSessionFactory();
	
	@Bean
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	private static SessionFactory createSessionFactory() {
		try {
			registry = new StandardServiceRegistryBuilder()
					.configure()
					.build();

			SessionFactory sessionFactory = new MetadataSources(registry)
					.buildMetadata()
					.buildSessionFactory();

			return sessionFactory;
		}catch (Exception e ) {
			e.printStackTrace();
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static void shutdown() {
		if(registry != null)
			StandardServiceRegistryBuilder.destroy(registry);
	}
	
	
}
