package repository.jdbc;

import dao.UserDAO;
import model.User;
import repository.DataAccessException;
import repository.UserRepository;

import java.util.Optional;

public class JdbcUserRepository implements UserRepository {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public Optional<User> login(String username, String hashedPassword) throws DataAccessException {
        try {
            Integer id = userDAO.loginAndGetId(username, hashedPassword);
            if (id == null) return Optional.empty();
            User u = new User();
            u.setId(id);
            u.setUsername(username);
            return Optional.of(u);
        } catch (Exception e) {
           e.printStackTrace(); // sementara untuk debug
throw new DataAccessException("Gagal login ke database. Detail: " + e.getMessage(), e);

        }
    }

    @Override
    public boolean register(String username, String hashedPassword) throws DataAccessException {
        try {
            return userDAO.register(username, hashedPassword);
        } catch (Exception e) {
            throw new DataAccessException("Gagal register user ke database.", e);
        }
    }

    @Override
    public Optional<String> getUsernameById(int userId) throws DataAccessException {
        try {
            String name = userDAO.getUsernameById(userId);
            return Optional.ofNullable(name);
        } catch (Exception e) {
            throw new DataAccessException("Gagal ambil username dari database.", e);
        }
    }
}
