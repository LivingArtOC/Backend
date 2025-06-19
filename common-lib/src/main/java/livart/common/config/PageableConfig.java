package livart.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class PageableConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        // 기본값 해제: 요청에 size, page가 없으면 Pageable.UNPAGED로 처리
        pageableResolver.setOneIndexedParameters(false); // 0-based page index
        pageableResolver.setFallbackPageable(Pageable.unpaged()); // 핵심 설정

        resolvers.add(pageableResolver);
    }
}

