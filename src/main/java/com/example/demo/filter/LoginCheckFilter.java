package com.example.demo.filter;

import com.example.demo.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import com.alibaba.fastjson2.JSON;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String url = request.getRequestURI();

        log.info("filter url => {}", url);

        String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/front/**"

        };

        // check url and isLogin
        if (check(urls, url) || request.getSession().getAttribute("employee") != null) {
            filterChain.doFilter(request, response);
            return;
        }

        response.getWriter().write(JSON.toJSONString(R.error("filter error")));
    }

    public boolean check(String[] urls, String url) {
        for (String s : urls) {
            boolean match = PATH_MATCHER.match(s, url);

            if (match) return true;
        }

        return false;
    }
}
