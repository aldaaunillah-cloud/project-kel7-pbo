package util;

import javax.swing.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Jalankan kerja berat (DB/HTTP) di background agar UI tidak freeze. */
public final class SwingAsync {
    private SwingAsync() {}

    public static <T> void run(Supplier<T> backgroundWork,
                              Consumer<T> onSuccess,
                              Consumer<Throwable> onError) {

        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() {
                return backgroundWork.get();
            }

            @Override
            protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Throwable t) {
                    onError.accept(t);
                }
            }
        }.execute();
    }
}
