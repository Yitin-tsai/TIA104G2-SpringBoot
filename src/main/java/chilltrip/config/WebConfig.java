package chilltrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import chilltrip.filter.LoginCheckInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoginCheckInterceptor()).addPathPatterns("/member/createCollectionAndAddLocation",
				"/member/addLocationToCollection", "/member/locationCollections", "/viewMyTrip/**"
		// ... 其他需要驗證的路徑
		).excludePathPatterns("/viewOtherTrip/**" // 排除公開瀏覽的頁面
		);
	}

}