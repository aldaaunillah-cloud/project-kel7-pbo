package repository.remote;

import app.AppConfig;
import client_api.PSApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.PS;
import model.PSItem;
import repository.DataAccessException;
import repository.PSRepository;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class RemotePSRepository implements PSRepository {

    private final Gson gson = new Gson();

    @Override
    public List<PS> findAll() throws DataAccessException {
        try {
            String json = PSApi.listJson(AppConfig.API_BASE_URL);
            Type type = new TypeToken<List<PS>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            throw new DataAccessException("Gagal load data PS dari server.", e);
        }
    }

    @Override
    public List<PSItem> findAvailableItems() throws DataAccessException {
        try {
            String json = PSApi.availableItemsJson(AppConfig.API_BASE_URL);
            Type type = new TypeToken<List<PSItem>>() {}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            throw new DataAccessException("Gagal load PS tersedia dari server.", e);
        }
    }

    @Override
    public boolean isAvailable(int psId) throws DataAccessException {
        // opsional: cukup refresh list
        return true;
    }

    @Override
    public boolean updateStatus(int psId, String status) throws DataAccessException {
        // biasanya server yang update
        return true;
    }

    @Override
    public String getPSNameById(int psId) throws DataAccessException {
        return "PS-" + psId;
    }

    @Override
    public void resetAllToAvailable() throws DataAccessException {
        // server yang handle; client tidak reset
    }
}
