package config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import dao.UserRepository;

public class AppInitializer implements org.springframework.web.WebApplicationInitializer {
	
	
	
	@Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // Tạo và cấu hình ApplicationContext của Spring
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebMvcConfig.class);
        context.register(SecurityConfig.class);
        context.register(UserRepository.class);
        // Đăng ký ContextLoaderListener
        servletContext.addListener(new ContextLoaderListener(context));
        // Đăng ký springSecurityFilterChain
        servletContext.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain"))
      .addMappingForUrlPatterns(null, false, "/*");
        
//        // Cấu hình CharacterEncodingFilter
//        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//        characterEncodingFilter.setEncoding("UTF-8");
//        characterEncodingFilter.setForceEncoding(true);
//
//        // Đăng ký Filter
//        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter("characterEncodingFilter", characterEncodingFilter);
//        filterRegistration.addMappingForUrlPatterns(null, false, "/*");
//        
        // (Tùy chọn) Đăng ký DispatcherServlet nếu sử dụng Spring MVC
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("SpringDispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }
}