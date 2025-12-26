package repository;

import model.PS;
import model.PSItem;

import java.util.List;

public interface PSRepository {
    List<PS> findAll() throws DataAccessException;
    List<PSItem> findAvailableItems() throws DataAccessException;
    boolean isAvailable(int psId) throws DataAccessException;
    boolean updateStatus(int psId, String status) throws DataAccessException;
    String getPSNameById(int psId) throws DataAccessException;
    void resetAllToAvailable() throws DataAccessException;
}
