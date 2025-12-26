package service;

import model.PS;
import model.PSItem;
import repository.PSRepository;

import java.util.List;

public class PSService {

    private final PSRepository psRepo;

    public PSService() {
        this(new repository.jdbc.JdbcPSRepository());
    }

    public PSService(PSRepository psRepo) {
        this.psRepo = psRepo;
    }

    public List<PS> findAll() {
        return psRepo.findAll();
    }

    public List<PSItem> findAvailableItems() {
        return psRepo.findAvailableItems();
    }


    // ========== BACKWARD COMPATIBILITY (untuk PSController lama) ==========
    public String getAllPlayStation() {
        java.util.List<String> names = new dao.PSDAO().getAllPS();
        return toJsonArray(names);
    }

    public String getAvailablePlayStation() {
        java.util.List<String> names = new dao.PSDAO().getAvailablePS();
        return toJsonArray(names);
    }

    private String toJsonArray(java.util.List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(escape(list.get(i))).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
