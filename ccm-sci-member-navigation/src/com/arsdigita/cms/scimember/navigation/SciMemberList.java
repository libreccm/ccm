package com.arsdigita.cms.scimember.navigation;

import com.arsdigita.navigation.Navigation;
import com.arsdigita.navigation.ui.AbstractComponent;
import com.arsdigita.persistence.SessionManager;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SciMemberList extends AbstractComponent {

    private static final String MEMBERS_QUERY_TEMPLATE = ""
                                                             + "SELECT cms_items.item_id, name, version, language, object_type, "
                                                         + "master_id, parent_id, title, cms_pages.description, "
                                                         + "surname, givenname, titlepre, titlepost "
                                                         + "FROM cms_items "
                                                             + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                                         + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                                         + "JOIN cms_persons ON cms_persons.person_id = cms_items.item_id "
                                                         + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND version = 'live' %s"
                                                         + "%s"
                                                             + "LIMIT ? OFFSET ?";

    private static final String COUNT_MEMBERS_QUERY_TEMPLATE = ""
                                                                   + "SELECT COUNT(*) "
                                                               + "FROM cms_items "
                                                               + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                                               + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                                               + "JOIN cms_persons ON cms_persons.person_id = cms_items.item_id "
                                                               + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND version = 'live' %s";

    private final PreparedStatement contactQueryStatement;

    private int limit = 20;

    public SciMemberList() {

        try {
            final Connection connection = SessionManager
                .getSession()
                .getConnection();

            contactQueryStatement = connection.prepareStatement(
                "SELECT contactentry_id, key, value "
                    + "FROM cms_contactentries "
                    + "JOIN cms_contacts ON cms_contactentries.contact_id = cms_contacts.contact_id "
                + "JOIN cms_items ON cms_contacts.contact_id = cms_items.item_id "
                + "WHERE cms_items.parent_id IN (SELECT contact_id FROM cms_person_contact_map WHERE person_id = ?)"
            );

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    @Override
    public Element generateXML(final HttpServletRequest request,
                               final HttpServletResponse response) {

        final Connection connection = SessionManager
            .getSession()
            .getConnection();

        final String categoryId = getCategory().getID().toString();

        final Element listElem = Navigation.newElement(
            "sci-member-list");
        final Element filtersElem = listElem.newChildElement("filters");

        final PreparedStatement membersQueryStatement;
        final StringBuffer whereBuffer = new StringBuffer();
        final int page;
        final int offset;
        try {

            final String surnameFilter = request.getParameter("surname");

            if (surnameFilter != null && !surnameFilter.trim().isEmpty()) {
                whereBuffer
                    .append(" AND ")
                    .append("LOWER(surname) LIKE '%%")
                    .append(surnameFilter.toLowerCase())
                    .append("%%' ");
                final Element surnameFilterElem = filtersElem
                    .newChildElement("surname");
                surnameFilterElem.setText(surnameFilter);
            }

            final String orderBy = "ORDER BY surname, givenname ";

            membersQueryStatement = connection
                .prepareStatement(String.format(MEMBERS_QUERY_TEMPLATE,
                                                whereBuffer.toString(),
                                                orderBy));
            membersQueryStatement.setString(1, categoryId);
            membersQueryStatement.setInt(2, limit);

            if (request.getParameter("page") == null) {
                page = 1;
                membersQueryStatement.setInt(3, 0);
                offset = 0;
            } else {
                page = Integer.parseInt(request.getParameter("page"));
                offset = (page - 1) * limit;

                membersQueryStatement.setInt(3, offset);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        try (final ResultSet mainQueryResult = membersQueryStatement
            .executeQuery()) {

            final Element paginatorElem = listElem.newChildElement("paginator");

            final PreparedStatement countMembersQueryStatement = connection
                .prepareStatement(String.format(COUNT_MEMBERS_QUERY_TEMPLATE,
                                                whereBuffer.toString()));

            countMembersQueryStatement.setString(1, categoryId);
            final ResultSet countResultSet = countMembersQueryStatement
                .executeQuery();
            final int count;
            if (countResultSet.next()) {
                count = countResultSet.getInt(1);
                paginatorElem.addAttribute("count", Integer.toString(count));
            } else {
                count = 0;
            }

            final int maxPages = (int) Math.ceil(count / (double) limit);

            paginatorElem.addAttribute("maxPages", Integer.toString(maxPages));
            paginatorElem.addAttribute("currentPage", Integer.toString(page));
            paginatorElem.addAttribute("offset", Integer.toString(offset));
            paginatorElem.addAttribute("limit", Integer.toString(limit));

            while (mainQueryResult.next()) {

                generateResultEntry(mainQueryResult, listElem);
            }

            return listElem;

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

    }

    private void generateResultEntry(final ResultSet resultSet,
                                     final Element parent)
        throws SQLException {

        final Element memberElem = parent.newChildElement("member");

        final Element itemIdElem = memberElem.newChildElement("item-id");
        itemIdElem.setText(resultSet.getBigDecimal("item_id").toString());

        final Element parentIdElem = memberElem
            .newChildElement("parent-id");
        parentIdElem.setText(resultSet.getBigDecimal("parent_id").toString());

        final Element nameElem = memberElem.newChildElement("name");
        nameElem.setText(resultSet.getString("name"));

        memberElem.addAttribute("object-type",
                                resultSet.getString("object_type"));

        final Element titleElem = memberElem.newChildElement("title");
        titleElem.setText(resultSet.getString("title"));

        final String description = resultSet.getString("description");
        if (description != null && !description.trim().isEmpty()) {
            final Element descriptionElem = memberElem
                .newChildElement("description");
            descriptionElem.setText(description);
        }

        final Element surnameElem = memberElem.newChildElement("surname");
        surnameElem.setText(resultSet.getString("surname"));

        final Element givenNameElem = memberElem.newChildElement("givenname");
        givenNameElem.setText(resultSet.getString("givenname"));

        final Element titlePreElem = memberElem.newChildElement("title-pre");
        titlePreElem.setText(resultSet.getString("titlepre"));

        final Element titlePostElem = memberElem.newChildElement("title-post");
        titlePostElem.setText(resultSet.getString("titlePost"));

        generateContactEntries(resultSet.getBigDecimal("parent_id"),
                               memberElem);
    }

    private void generateContactEntries(final BigDecimal memberId,
                                        final Element memberElem)
        throws SQLException {

        contactQueryStatement.setBigDecimal(1, memberId);

        try (final ResultSet resultSet = contactQueryStatement.executeQuery()) {

            final Element contactEntriesElem = memberElem
                .newChildElement("contact-entries");

            while (resultSet.next()) {
                final Element contactEntryElem = contactEntriesElem
                    .newChildElement("contact-entry");
                contactEntryElem.addAttribute("key",
                                              resultSet.getString("key"));
                contactEntryElem.setText(resultSet.getString("value"));
            }
        }
    }

}
