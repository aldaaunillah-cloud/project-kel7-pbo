package service;

import dao.PSDAO;
import java.util.List;

public class PSService {

    private final PSDAO psDAO = new PSDAO();

    public List<String> getAvailablePS() {
        return psDAO.getAllPS();
    }
}
