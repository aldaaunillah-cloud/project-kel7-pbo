package controller;

import service.PSService;

public class PSController {

    private static final PSService psService = new PSService();

    public static String getAllPS() {
        return psService.getAllPlayStation();
    }

    public static String getAvailablePS() {
        return psService.getAvailablePlayStation();
    }
}
