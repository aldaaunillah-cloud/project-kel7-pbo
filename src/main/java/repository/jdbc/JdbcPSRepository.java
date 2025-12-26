package repository.jdbc;

import dao.PSDAO;
import model.PS;
import model.PSItem;
import repository.DataAccessException;
import repository.PSRepository;

import java.util.List;

public class JdbcPSRepository implements PSRepository {

    private final PSDAO psDAO = new PSDAO();

    @Override
    public List<PS> findAll() throws DataAccessException {
        try {
            return psDAO.getAllPSWithStatus();
        } catch (Exception e) {
            throw new DataAccessException("Gagal mengambil data PS dari database.", e);
        }
    }

    @Override
    public List<PSItem> findAvailableItems() throws DataAccessException {
        try {
            return psDAO.getAvailablePSItems();
        } catch (Exception e) {
            throw new DataAccessException("Gagal mengambil PS tersedia dari database.", e);
        }
    }

    @Override
    public boolean isAvailable(int psId) throws DataAccessException {
        try {
            return psDAO.isAvailable(psId);
        } catch (Exception e) {
            throw new DataAccessException("Gagal cek status PS.", e);
        }
    }

    @Override
    public boolean updateStatus(int psId, String status) throws DataAccessException {
        try {
            // psDAO.updateStatus(...) DI DAO KAMU return void, jadi jangan di-return
            psDAO.updateStatus(psId, status);
            return true;
        } catch (Exception e) {
            throw new DataAccessException("Gagal update status PS.", e);
        }
    }

    @Override
    public String getPSNameById(int psId) throws DataAccessException {
        try {
            return psDAO.getPSNameById(psId);
        } catch (Exception e) {
            throw new DataAccessException("Gagal ambil nama PS.", e);
        }
    }

    @Override
    public void resetAllToAvailable() throws DataAccessException {
        try {
            psDAO.resetAllToAvailable();
        } catch (Exception e) {
            throw new DataAccessException("Gagal reset status PS.", e);
        }
    }
}
