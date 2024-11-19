package com.tiger.cores.configs.cache;

public enum UserEnums {
    /**
     * Vai trò
     */
    MEMBER("member"),
    STORE("store"),
    MANAGER("manager"),
    SYSTEM("system");
    private final String role;

    UserEnums(String r) {
        this.role = r;
    }

    public String getRole() {
        return role;
    }
}
