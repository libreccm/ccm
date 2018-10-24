package org.libreccm.workflow;

import com.arsdigita.kernel.Party;
import com.arsdigita.kernel.User;
import com.arsdigita.workflow.simple.UserTask;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssignableTasksExporter extends AbstractTasksExporter<UserTask> {

    @Override
    public Class<UserTask> exportsType() {
        return UserTask.class;
    }

    @Override
    public String exportsBaseDataObjectType() {

        return UserTask.BASE_DATA_OBJECT_TYPE;

    }

    @Override
    public String convertsToType() {
        return "org.libreccm.workflow.AssignableTask";
    }

    @Override
    protected void exportTaskProperties(final UserTask task,
                                        final JsonGenerator jsonGenerator)
        throws IOException {

        jsonGenerator.writeBooleanField("locked", task.isLocked());

        final User lockingUser = task.getLockedUser();
        final String lockingUserUuid = generateUuid(lockingUser);
        jsonGenerator.writeStringField("lockingUser", lockingUserUuid);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);
        jsonGenerator.writeStringField("startDate",
                                       dateFormat.format(task.getStartDate()));
        jsonGenerator.writeStringField("dueDate",
                                       dateFormat.format(task.getDueDate()));
        jsonGenerator.writeNumberField("durationMinutes",
                                       task.getDuration().getDuration());

        final Party notificationSender = task.getNotificationSender();
        final String notificationSenderUuid = generateUuid(notificationSender);
        jsonGenerator.writeStringField("notificationSender",
                                       notificationSenderUuid);
    }

}
