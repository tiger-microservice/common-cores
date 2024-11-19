package com.tiger.cores.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Công cụ Cookie
 */
@Slf4j
public class CookieUtil {

    /**
     * Thêm cookie
     *
     * @param cookieKey   Giá trị khóa
     * @param cookieValue Giá trị tương ứng
     * @param maxAge      Thời gian hiệu lực của cookie
     * @param response    Phản hồi
     */
    public static void addCookie(String cookieKey, String cookieValue, Integer maxAge, HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie(cookieKey, cookieValue);
            cookie.setMaxAge(maxAge);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            log.error("Thêm cookie lỗi", e);
        }
    }

    /**
     * Xóa cookie
     *
     * @param cookieKey Giá trị khóa
     * @param response  Phản hồi
     */
    public static void delCookie(String cookieKey, HttpServletResponse response) {
        try {
            Cookie cookie = new Cookie(cookieKey, "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        } catch (Exception e) {
            log.error("Xóa cookie lỗi", e);
        }
    }

    /**
     * Lấy cookie
     *
     * @param cookieKey Giá trị khóa
     * @param request   Yêu cầu
     * @return Giá trị cookie
     */
    public static String getCookie(String cookieKey, HttpServletRequest request) {
        try {
            if (request.getCookies() == null) {
                return null;
            }
            for (int i = 0; i < request.getCookies().length; i++) {
                if (request.getCookies()[i].getName().equals(cookieKey)) {
                    return request.getCookies()[i].getValue();
                }
            }
        } catch (Exception e) {
            log.error("Lấy cookie lỗi", e);
        }
        return null;
    }
}
