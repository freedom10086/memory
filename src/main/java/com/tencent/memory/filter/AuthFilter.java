package com.tencent.memory.filter;

import com.tencent.memory.config.Attrs;
import com.tencent.memory.config.Config;
import com.tencent.memory.api.ApiResult;
import com.tencent.memory.api.ApiResultBuilder;
import com.tencent.memory.model.MyException;
import com.tencent.memory.util.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import java.io.IOException;

// 用于身份认证
public class AuthFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("do filter");
        String token = servletRequest.getParameter(Attrs.token);
        if (token == null || token.length() == 0) {
            unAuth(servletResponse, "需要token参数");
        } else {
            Token t;
            try {
                t = Token.decodeToken(token, Config.tokenSecretKey);
            } catch (MyException e) {
                unAuth(servletResponse, e.message);
                return;
            }

            if (t.isExpired()) {
                unAuth(servletResponse, "token 已经过期请重新登陆");
                return;
            }

            servletRequest.setAttribute(Attrs.uid, t.uid);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }

    private void unAuth(ServletResponse res, String error) throws IOException {
        res.setCharacterEncoding("utf-8");
        res.setContentType("application/json;charset=utf-8");

        ApiResult result = new ApiResultBuilder<>().error(HttpStatus.UNAUTHORIZED.value(), error).build();
        res.getWriter().write(result.toString());
    }
}
