package org.libreccm.workflow;

import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.workflow.simple.UserTask;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractAssignableTasksExporter<T extends UserTask>
    extends AbstractTasksExporter<T> {

    @Override
    protected final void exportTaskProperties(final T task,
                                              final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeBooleanField("locked", task.isLocked());

        final User lockingUser = task.getLockedUser();
        if (lockingUser != null) {
            final String lockingUserUuid = generateUuid(lockingUser);
            jsonGenerator.writeStringField("lockingUser", lockingUserUuid);
        }

        final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);
        jsonGenerator.writeStringField("startDate",
                                       dateFormat.format(task.getStartDate()));
        jsonGenerator.writeStringField("dueDate",
                                       dateFormat.format(task.getDueDate()));
        jsonGenerator.writeNumberField("durationMinutes",
                                       task.getDuration().getDuration());

        final Party notificationSender = task.getNotificationSender();
        if (notificationSender != null) {
            final String notificationSenderUuid = generateUuid(
                notificationSender);
            jsonGenerator.writeStringField("notificationSender",
                                           notificationSenderUuid);
        }

        exportAssignableTaskProperties(task, jsonGenerator);
    }

    protected abstract void exportAssignableTaskProperties(
        final T task, final JsonGenerator jsonGenerator)
        throws IOException;

}
