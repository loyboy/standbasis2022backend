package basepackage.stand.standbasisprojectonev1.sessionFactory;

import org.hibernate.SessionFactory;
import java.util.Properties;

import org.hibernate.cfg.Configuration;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
              /*  Configuration configuration = new Configuration();
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "org.hsqldb.jdbcDriver");
                settings.put(Environment.URL, "jdbc:hsqldb:mem:userrole");
                settings.put(Environment.USER, "sa");
                settings.put(Environment.PASS, "");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.HSQLDialect");
                settings.put(Environment.SHOW_SQL, "false");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                configuration.setProperties(settings);
               

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();
                
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);*/
                
                String jdbcUrl = "jdbc:mysql://localhost:3307/standbasislivejava";
                String username = "root";
                String password = "";

                Properties configurationProperties = new Properties();
                configurationProperties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
                configurationProperties.put("hibernate.connection.url", jdbcUrl);
                configurationProperties.put("hibernate.connection.username", username);
                configurationProperties.put("hibernate.connection.password", password);
                configurationProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

                sessionFactory = new Configuration()
                        .addProperties(configurationProperties)
                        .buildSessionFactory();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
