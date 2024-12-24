package com.tiger.cores.serializers.sensitive;

public class SensitiveProperties {

    /**
     * Chế độ demo
     */
    private Boolean demoMode = false;

    /**
     * Chế độ thử nghiệm
     * Mã xác thực SMS là 6 chữ số 1
     */
    private Boolean testMode = false;

    /**
     * Mức độ ẩn thông tin:
     * 0: Không ẩn thông tin
     * 1: Ẩn thông tin của người dùng quản trị (như số điện thoại)
     * 2: Ẩn thông tin của cửa hàng (nếu là 2, thì cả quản trị và cửa hàng đều ẩn thông tin)
     * <p>
     * PS:
     */
    private Integer dataMaskingLevel = 0;

    public Boolean getDemoMode() {
        if (demoMode == null) {
            return false;
        }
        return demoMode;
    }

    public Boolean getTestMode() {
        if (testMode == null) {
            return false;
        }
        return testMode;
    }

    public Integer getDataMaskingLevel() {
        if (dataMaskingLevel == null) {
            return 0;
        }
        return dataMaskingLevel;
    }
}
