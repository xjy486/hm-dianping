package com.hmdp.utils;

import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的session
        HttpSession session = request.getSession();
        // 从session中获取用户信息
        UserDTO userDTO = (UserDTO) session.getAttribute("user");
        // 如果用户信息不存在，拦截
        if (userDTO == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        UserHolder.saveUser(userDTO);
        // 如果用户信息存在，放行请求
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 可以在这里添加一些后处理逻辑
        UserHolder.removeUser(); // 清除线程本地变量，避免内存泄漏
    }
}
