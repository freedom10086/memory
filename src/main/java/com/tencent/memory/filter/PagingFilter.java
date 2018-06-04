package com.tencent.memory.filter;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.model.MyException;
import com.tencent.memory.util.Paging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// 用于分页
public class PagingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PagingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("do filter");
        if (servletRequest instanceof HttpServletRequest) {
            try {
                Paging paging = Paging.parse((HttpServletRequest) servletRequest);
                servletRequest.setAttribute(Attrs.paging, paging);
            } catch (MyException e) {
                error(servletResponse, e.status, e.message);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void error(ServletResponse res, HttpStatus status, String error) throws IOException {
        res.setCharacterEncoding("utf-8");
        res.setContentType("application/json;charset=utf-8");

        ApiResult result = new ApiResultBuilder<>().error(status.value(), error).build();
        res.getWriter().write(result.toString());
    }
}
