package service;

import model.User;
import repository.DataAccessException;
import repository.UserRepository;
import util.PasswordUtil;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepo;

    public AuthService() {
        this(new repository.jdbc.JdbcUserRepository());
    }

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /** Return User agar UI tidak perlu input userId manual. */
    public Optional<User> login(String username, String passwordPlain) throws DataAccessException {
        username = username == null ? "" : username.trim();
        passwordPlain = passwordPlain == null ? "" : passwordPlain;

        String hashed = PasswordUtil.hash(passwordPlain);
        return userRepo.login(username, hashed);
    }

    /** Register versi "bagus": validasi + confirm + pesan user-friendly */
    public void register(String username, String passwordPlain, String confirmPlain) throws AuthException, DataAccessException {
        username = username == null ? "" : username.trim();
        passwordPlain = passwordPlain == null ? "" : passwordPlain;
        confirmPlain  = confirmPlain == null ? "" : confirmPlain;

        if (username.isEmpty()) throw new AuthException("Username wajib diisi.");
        if (username.length() < 3) throw new AuthException("Username minimal 3 karakter.");
        if (!username.matches("[a-zA-Z0-9_]+")) throw new AuthException("Username hanya boleh huruf/angka/underscore.");

        if (passwordPlain.isEmpty()) throw new AuthException("Password wajib diisi.");
        if (passwordPlain.length() < 4) throw new AuthException("Password minimal 4 karakter.");
        if (!passwordPlain.equals(confirmPlain)) throw new AuthException("Konfirmasi password tidak sama.");

        String hashed = PasswordUtil.hash(passwordPlain);

        boolean ok = userRepo.register(username, hashed);
        if (!ok) {
            // Artinya username sudah dipakai
            throw new AuthException("Username sudah dipakai. Coba yang lain.");
        }
    }

    /** Backward compatible method lama (kalau ada code lama masih panggil ini) */
    public boolean register(String username, String passwordPlain) {
        try {
            username = username == null ? "" : username.trim();
            passwordPlain = passwordPlain == null ? "" : passwordPlain;
            if (username.isEmpty() || passwordPlain.isEmpty()) return false;

            String hashed = PasswordUtil.hash(passwordPlain);
            return userRepo.register(username, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
