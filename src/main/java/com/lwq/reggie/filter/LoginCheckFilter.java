package com.lwq.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.lwq.reggie.common.BaseContext;
import com.lwq.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//登录过程的优化
//检查用户是否完成登录的过滤器
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher pathMatcher = new AntPathMatcher();
    public boolean check(String uri,String[] urls){
        //判断请求路径是否在放行的路径中
        for (String url : urls) {
            boolean match = pathMatcher.match(url,uri);
            if(match)
                return true;
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的UPI
        String URI = request.getRequestURI();

        //判断本次请求是否需要处理
        //定义不需要处理的请求
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**"
        };

        boolean flag = check(URI,urls);

        //如果判断成立就放行
        if(flag){
            filterChain.doFilter(request,response);
            return;
        }
        //不放行
        //判断登录状态，如果已经登录就直接放行

        if(request.getSession().getAttribute("employee")!=null){
//            log.info("用户已经登录，用户id为{}",request.getSession().getAttribute("employee"));
            Long id =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);
            filterChain.doFilter(request,response);
            return;
        }

        //未登录
        //如果未登录则返回登录结果，通过输出流的方式向客户端页面响应数据
        //注意看request.js中的内容
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
}
