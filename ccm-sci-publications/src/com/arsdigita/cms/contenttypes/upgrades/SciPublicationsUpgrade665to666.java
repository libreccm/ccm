package com.arsdigita.cms.contenttypes.upgrades;

import com.arsdigita.cms.contenttypes.Publication;
import com.arsdigita.domain.DomainObjectFactory;
import com.arsdigita.persistence.DataCollection;
import com.arsdigita.persistence.DataObject;
import com.arsdigita.persistence.Session;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.cmd.Program;
import org.apache.commons.cli.CommandLine;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SciPublicationsUpgrade665to666 extends Program {

    public SciPublicationsUpgrade665to666() {
        super("SciPublicationsUpgrade665to666", "1.0.0", "", true, true);
    }

    @Override
    public void doRun(final CommandLine cmdLine) {
        final Session session = SessionManager.getSession();
        final DataCollection dataCollection = session.retrieve(Publication.BASE_DATA_OBJECT_TYPE);

        while (dataCollection.next()) {
            setContentItemInfo(dataCollection.getDataObject());
        }

    }

    private void setContentItemInfo(final DataObject dataObject) {
        final Publication publication = (Publication) DomainObjectFactory.newInstance(dataObject);

        publication.setAdditionalInfo((String) publication.get("authorsStr"));
    }

    public static void main(final String[] args) {
        new SciPublicationsUpgrade665to666().run(args);
    }

}
