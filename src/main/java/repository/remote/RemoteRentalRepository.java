package repository.remote;

import app.AppConfig;
import client_api.RentalApi;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import repository.DataAccessException;
import repository.RentalRepository;

public class RemoteRentalRepository implements RentalRepository {

    private final Gson gson = new Gson();

    @Override
    public boolean createRental(int userId, int psId, int hours) throws DataAccessException {
        try {
            String json = RentalApi.createRentalJson(userId, psId, hours, AppConfig.API_BASE_URL);
            JsonObject obj = gson.fromJson(json, JsonObject.class);
            return obj.get("ok").getAsBoolean();
        } catch (Exception e) {
            throw new DataAccessException("Gagal membuat rental via server 3-tier.", e);
        }
    }

    @Override
    public String getUserRentalsJson(int userId) throws DataAccessException {
        try {
            return RentalApi.getUserRentals(userId, AppConfig.API_BASE_URL);
        } catch (Exception e) {
            throw new DataAccessException("Gagal ambil riwayat rental via server.", e);
        }
    }
}
