package util;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Helper supaya bisa pakai lambda untuk DocumentListener.
 */
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {

    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) { update(e); }

    @Override
    default void removeUpdate(DocumentEvent e) { update(e); }

    @Override
    default void changedUpdate(DocumentEvent e) { update(e); }
}
