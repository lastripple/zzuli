package com.troublemaker.clockin.interceptor;

import com.troublemaker.clockin.annotation.JWTToken;
import com.troublemaker.clockin.config.JWTTokenConfiguration;
import com.troublemaker.clockin.entity.ResultEnum;
import com.troublemaker.clockin.entity.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * @BelongsProject: zzuli
 * @BelongsPackage: com.troublemaker.clockin.interceptor
 * @Author: troublemaker
 * @CreateTime: 2022-08-27  20:46
 * @Version: 1.0
 */
public class JWTAuthenticationInterceptor implements HandlerInterceptor {

    @Autowired
    private JWTTokenConfiguration jwtToken;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("Authorization");  //从http请求头中取出token
        // //如果是SpringMVC请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查有没有需要用户权限的注释
        if (method.isAnnotationPresent(JWTToken.class)) {
            JWTToken JWTToken = method.getAnnotation(JWTToken.class);
            if (JWTToken.required()) {
                // 执行认证
                if (token == null) {
                    // 返回错误的状态码，跳转到登录界面
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter writer = response.getWriter();
                    writer.append(new ResultResponse(ResultEnum.SIGNATURE_NOT_MATCH).toString());
                    return false;
                } else {
                    // 调用JWT解密方法，可以在解密方法中定义返回数据，这里可以继续根据返回值进行判断，也可以在解密方法中判断，具体自行定义
                    jwtToken.verifyToken(token);
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}

