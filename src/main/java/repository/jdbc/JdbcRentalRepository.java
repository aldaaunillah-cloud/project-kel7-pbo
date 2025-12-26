package repository.jdbc;

import dao.RentalDAO;
import repository.DataAccessException;
import repository.RentalRepository;

public class JdbcRentalRepository implements RentalRepository {

    private final RentalDAO rentalDAO = new RentalDAO();

    @Override
    public boolean createRental(int userId, int psId, int hours) throws DataAccessException {
        try {
            return rentalDAO.createRental(userId, psId, hours);
        } catch (Exception e) {
            throw new DataAccessException("Gagal membuat transaksi rental di database.", e);
        }
    }

    @Override
    public String getUserRentalsJson(int userId) throws DataAccessException {
        try {
            return rentalDAO.getUserRentals(userId);
        } catch (Exception e) {
            throw new DataAccessException("Gagal ambil riwayat rental.", e);
        }
    }
}
