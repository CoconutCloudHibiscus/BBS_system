package com.bbs.interceptor;

import com.bbs.util.JwtUtil;
import com.bbs.util.TokenBlacklist;
import com.bbs.util.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

/**
 * JWT认证拦截器
 * <p>从请求头中提取Token，验证有效性并设置用户上下文</p>
 * <p>对公开只读接口的GET请求放行（无需Token），写操作必须认证</p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    /** 公开只读接口路径（GET请求无需Token） */
    private static final Set<String> PUBLIC_GET_PATHS = Set.of(
            "/api/boards",
            "/api/posts"
    );

    /**
     * 请求预处理
     * <p>从Authorization头提取Token，解析验证，检查黑名单，设置UserContext</p>
     * <p>对于公开接口的GET请求，允许无Token访问（但如果有Token仍会解析设置UserContext）</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @return true-继续执行，false-拒绝请求
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // 判断是否为公开GET请求
        boolean isPublicGetRequest = "GET".equalsIgnoreCase(method) && isPublicPath(requestURI);

        String authHeader = request.getHeader("Authorization");

        // 公开GET请求：无Token也放行，有Token则解析设置UserContext
        if (isPublicGetRequest && (authHeader == null || !authHeader.startsWith("Bearer "))) {
            return true;
        }

        // 非公开请求：必须有Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"未登录或Token缺失\",\"data\":null}");
            return false;
        }

        String token = authHeader.substring(7);
        try {
            // 检查Token是否在黑名单中
            if (tokenBlacklist.isBlacklisted(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"Token已失效，请重新登录\",\"data\":null}");
                return false;
            }

            // 解析Token
            Claims claims = jwtUtil.parseToken(token);

            // 设置用户上下文
            UserContext.setUserId(Long.parseLong(claims.getSubject()));
            UserContext.setAccount(claims.get("account", String.class));
            UserContext.setRole(claims.get("role", Integer.class));

            return true;
        } catch (Exception e) {
            // 公开GET请求Token解析失败也放行
            if (isPublicGetRequest) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\",\"data\":null}");
            return false;
        }
    }

    /**
     * 判断请求路径是否为公开路径
     *
     * @param uri 请求URI
     * @return 是否为公开路径
     */
    private boolean isPublicPath(String uri) {
        if (PUBLIC_GET_PATHS.contains(uri)) {
            return true;
        }
        // /api/posts/{id} 和 /api/posts/{id}/comments
        if (uri.startsWith("/api/posts/")) {
            String subPath = uri.substring("/api/posts/".length());
            // /api/posts/123 或 /api/posts/123/comments
            if (subPath.matches("\\d+") || subPath.matches("\\d+/comments")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 请求完成后清除用户上下文
     * <p>防止ThreadLocal内存泄漏</p>
     *
     * @param request  HTTP请求
     * @param response HTTP响应
     * @param handler  处理器
     * @param ex       异常（如果有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
