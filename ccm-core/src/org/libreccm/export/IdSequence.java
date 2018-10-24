package org.libreccm.export;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class IdSequence {

    private static final IdSequence INSTANCE = new IdSequence();

    private long idSequence = 0;

    private IdSequence() {
        // Nothing
    }

    public static IdSequence getInstance() {
        return INSTANCE;
    }

    public long nextId() {
        idSequence++;
        return idSequence;
    }

}
