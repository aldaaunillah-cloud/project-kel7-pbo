package repository;

import model.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> login(String username, String hashedPassword) throws DataAccessException;
    boolean register(String username, String hashedPassword) throws DataAccessException;
    Optional<String> getUsernameById(int userId) throws DataAccessException;
}
