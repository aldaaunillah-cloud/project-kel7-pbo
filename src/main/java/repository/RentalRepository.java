package repository;

public interface RentalRepository {
    boolean createRental(int userId, int psId, int hours) throws DataAccessException;
    String getUserRentalsJson(int userId) throws DataAccessException;
}
