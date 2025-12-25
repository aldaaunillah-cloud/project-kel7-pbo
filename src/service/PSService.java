package service;

import dao.PSDAO;

import java.util.List;

public class PSService {

    private final PSDAO psDAO = new PSDAO();

    // untuk PSController.getAllPS()
    public String getAllPlayStation() {
        List<String> list = psDAO.getAllPS();
        return toJsonArray(list);
    }

    // untuk PSController.getAvailablePS()
    public String getAvailablePlayStation() {
        List<String> list = psDAO.getAvailablePS();
        return toJsonArray(list);
    }

    // kompatibilitas kalau ada yang masih manggil ini
    public List<String> getAvailablePS() {
        return psDAO.getAllPS();
    }

    private String toJsonArray(List<String> list) {
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
