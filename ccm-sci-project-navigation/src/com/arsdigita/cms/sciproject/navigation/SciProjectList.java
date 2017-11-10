package com.arsdigita.cms.sciproject.navigation;

import com.arsdigita.globalization.Globalization;
import com.arsdigita.globalization.GlobalizationHelper;
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
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SciProjectList extends AbstractComponent {

    private static final String PROJECTS_QUERY_TEMPLATE
                                    = "SELECT cms_items.item_id, name, version, language, object_type, "
                                      + "master_id, parent_id, title, cms_pages.description as page_description, "
                                      + "projectbegin, projectbegin_skip_month, projectbegin_skip_day, "
                                      + "projectend, projectend_skip_month, projectend_skip_day, "
                                      + "shortdesc, ct_sci_projects.description AS project_description, funding, funding_volume "
                                      + "FROM cms_items "
                                          + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                      + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                      + "JOIN ct_sci_projects ON cms_items.item_id = ct_sci_projects.project_id "
                                      + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND language = ? AND version = 'live' %s"
                                      + "%s"
                                          + "LIMIT ? OFFSET ?";

    private static final String COUNT_PROJECTS_QUERY_TEMPLATE
                                    = "SELECT COUNT(*) "
                                          + "FROM cms_items "
                                          + "JOIN cms_pages ON cms_items.item_id = cms_pages.item_id "
                                      + "JOIN content_types ON cms_items.type_id = content_types.type_id "
                                      + "JOIN ct_sci_projects ON cms_items.item_id = ct_sci_projects.project_id "
                                      + "WHERE parent_id IN (SELECT object_id FROM cat_object_category_map WHERE category_id = ?) AND language = ? AND version = 'live' %s";

    private final PreparedStatement projectMembersQueryStatement;

    private int limit = 20;

    public SciProjectList() {

        try {
            final Connection connection = SessionManager
                .getSession()
                .getConnection();

            projectMembersQueryStatement = connection.prepareStatement(
                "SELECT surname, givenname, titlepre, titlepost "
                    + "FROM cms_persons "
                    + "JOIN cms_items ON cms_persons.person_id = cms_items.item_id "
                + "JOIN cms_bundles ON cms_items.parent_id = cms_bundles.bundle_id "
                + "JOIN cms_orgaunits_person_map ON cms_bundles.bundle_id = cms_orgaunits_person_map.person_id "
                + "WHERE orgaunit_id = ? "
                    + "ORDER BY surname, givenname"
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
            "sci-project-list");
        final Element filtersElem = listElem.newChildElement("filters");
        final Element sortElem = filtersElem.newChildElement("sort");

        final PreparedStatement projectsQueryStatement;
        final StringBuffer whereBuffer = new StringBuffer();
        final int page;
        final int offset;
        final String titleFilter;
        final BigDecimal researchFieldFilter;
        try {
//            final String titleFilter = request.getParameter("title");
            titleFilter = Globalization.decodeParameter(request,
                                                        "title");
//            final BigDecimal categoryFilter;
            if (request.getParameter("researchfield") == null) {
                researchFieldFilter = null;
            } else if (request.getParameter("researchfield").matches("\\d*")) {
                researchFieldFilter
                    = new BigDecimal(request.getParameter("researchfield"));
            } else {
                researchFieldFilter = null;
            }

            if (titleFilter != null && !titleFilter.trim().isEmpty()
                    || researchFieldFilter != null) {

                whereBuffer.append(" AND ");
            }
            if (titleFilter != null && !titleFilter.trim().isEmpty()) {
                whereBuffer
                    .append("LOWER(title) LIKE '%%")
                    .append(titleFilter.toLowerCase())
                    .append("%%' ");
                final Element titleFilterElem = filtersElem
                    .newChildElement("title");
                titleFilterElem.setText(titleFilter);
            }
            if (researchFieldFilter != null) {
                if (titleFilter != null && !titleFilter.trim().isEmpty()) {
                    whereBuffer.append(" AND ");
                }

                whereBuffer.append("parent_id IN (SELECT object_id "
                                       + "FROM cat_object_category_map "
                                       + "WHERE category_id = ")
                    .append(researchFieldFilter.toString())
                    .append(") ");

                final Element researchFieldFilterElem = filtersElem
                    .newChildElement("researchfield");
                researchFieldFilterElem.setText(researchFieldFilter.toString());
            }

            final String orderBy
                             = "ORDER BY projectbegin DESC, projectend DESC, title ASC ";

            projectsQueryStatement = connection
                .prepareStatement(String.format(PROJECTS_QUERY_TEMPLATE,
                                                whereBuffer.toString(),
                                                orderBy));
            projectsQueryStatement.setString(1, categoryId);
            projectsQueryStatement.setString(2, GlobalizationHelper
                                             .getNegotiatedLocale()
                                             .getLanguage());
            projectsQueryStatement.setInt(3, limit);

            if (request.getParameter("page") == null) {
                page = 1;
                projectsQueryStatement.setInt(4, 0);
                offset = 0;
            } else {
                page = Integer.parseInt(request.getParameter("page"));
                offset = (page - 1) * limit;

                projectsQueryStatement.setInt(4, offset);
            }

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }

        try (final ResultSet mainQueryResult = projectsQueryStatement
            .executeQuery()) {

            final Element paginatorElem = listElem.newChildElement("paginator");

            final PreparedStatement countProjectQueryStatement = connection
                .prepareStatement(String.format(COUNT_PROJECTS_QUERY_TEMPLATE,
                                                whereBuffer.toString()));

            countProjectQueryStatement.setString(1, categoryId);
            countProjectQueryStatement.setString(2, GlobalizationHelper
                                                 .getNegotiatedLocale()
                                                 .toString());
            final ResultSet countResultSet = countProjectQueryStatement
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

            if (page < maxPages) {
                final StringBuffer linkBuffer = new StringBuffer("?page=");
                linkBuffer.append(page + 1);
                if (titleFilter != null) {
                    linkBuffer
                        .append("&title=")
                        .append(titleFilter);
                }
                if (researchFieldFilter != null) {
                    linkBuffer
                        .append("researchfield=")
                        .append(researchFieldFilter.toString());
                }
                paginatorElem
                    .addAttribute("nextPageLink", linkBuffer.toString());
            }

            if (page > 1) {
                final StringBuffer linkBuffer = new StringBuffer("?page=");
                linkBuffer.append(page - 1);
                if (titleFilter != null) {
                    linkBuffer
                        .append("&title=")
                        .append(titleFilter);
                }
                if (researchFieldFilter != null) {
                    linkBuffer
                        .append("researchfield=")
                        .append(researchFieldFilter.toString());
                }
                paginatorElem
                    .addAttribute("prevPageLink", linkBuffer.toString());
            }

            while (mainQueryResult.next()) {

                generateResultEntry(mainQueryResult, listElem);
            }

            return listElem;

        } catch (SQLException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private void generateResultEntry(final ResultSet resultSet,
                                     final Element parent) throws SQLException {

        final Element projectElem = parent.newChildElement("project");

        final Element itemIdElem = projectElem.newChildElement("item-id");
        itemIdElem.setText(resultSet.getBigDecimal("item_id").toString());

        final Element parentIdElem = projectElem
            .newChildElement("parent-id");
        parentIdElem.setText(resultSet.getBigDecimal("parent_id").toString());

        final Element nameElem = projectElem.newChildElement("name");
        nameElem.setText(resultSet.getString("name"));

        projectElem.addAttribute("object-type",
                                 resultSet.getString("object_type"));

        final Element titleElem = projectElem.newChildElement("title");
        titleElem.setText(resultSet.getString("title"));

        final String description = resultSet.getString("page_description");
        if (description != null && !description.trim().isEmpty()) {
            final Element descriptionElem = projectElem
                .newChildElement("description");
            descriptionElem.setText(description);
        }

        if (resultSet.getDate("projectbegin") != null) {

            final Element projectBeginElem = projectElem.newChildElement(
                "project-begin");
            final Calendar projectBegin = Calendar.getInstance();
            projectBegin.setTime(resultSet.getDate("projectbegin"));

            projectBeginElem.addAttribute("year", Integer.toString(projectBegin
                                          .get(Calendar.YEAR)));
            projectBeginElem.addAttribute("month", Integer.toString(projectBegin
                                          .get(Calendar.MONTH)));
            projectBeginElem.addAttribute(
                "month-name",
                projectBegin.getDisplayName(Calendar.MONTH,
                                            Calendar.LONG,
                                            GlobalizationHelper
                                                .getNegotiatedLocale()));
            projectBeginElem.addAttribute("day", Integer.toString(projectBegin
                                          .get(Calendar.DAY_OF_MONTH)));

            projectBeginElem.addAttribute(
                "skip-month",
                Boolean
                    .toString(resultSet.getBoolean("projectbegin_skip_month")));
            projectBeginElem.addAttribute(
                "skip-day",
                Boolean.toString(resultSet.getBoolean("projectbegin_skip_day")));
        }

        if (resultSet.getDate("projectend") != null) {

            final Element projectEndElem = projectElem.newChildElement(
                "project-end");
            final Calendar projectEnd = Calendar.getInstance();
            projectEnd.setTime(resultSet.getDate("projectend"));

            projectEndElem.addAttribute("year", Integer.toString(projectEnd
                                        .get(Calendar.YEAR)));
            projectEndElem.addAttribute("month", Integer.toString(projectEnd
                                        .get(Calendar.MONTH)));
            projectEndElem.addAttribute(
                "month-name",
                projectEnd.getDisplayName(Calendar.MONTH,
                                          Calendar.LONG,
                                          GlobalizationHelper
                                              .getNegotiatedLocale()));
            projectEndElem.addAttribute("day", Integer.toString(projectEnd
                                        .get(Calendar.DAY_OF_MONTH)));

            projectEndElem.addAttribute(
                "skip-month",
                Boolean
                    .toString(resultSet.getBoolean("projectend_skip_month")));
            projectEndElem.addAttribute(
                "skip-day",
                Boolean.toString(resultSet.getBoolean("projectend_skip_day")));
        }

        final Element shortDescElem = projectElem.newChildElement(
            "project-short-desc");
        shortDescElem.setText(resultSet.getString("shortdesc"));

        final Element descriptionElem = projectElem.newChildElement(
            "project-description");
        descriptionElem.setText(resultSet.getString("project_description"));

        generateMembers(resultSet.getBigDecimal("parent_id"),
                        projectElem);
    }

    private void generateMembers(final BigDecimal projectId,
                                 final Element projectElem)
        throws SQLException {

        projectMembersQueryStatement.setBigDecimal(1, projectId);

        try (final ResultSet resultSet = projectMembersQueryStatement
            .executeQuery()) {

            final Element membersElem = projectElem.newChildElement("members");

            while (resultSet.next()) {
                final Element memberElem = membersElem.newChildElement("member");
                memberElem.addAttribute("surname", resultSet
                                        .getString("surname"));
                memberElem.addAttribute("givenname",
                                        resultSet.getString("givenname"));
                memberElem.addAttribute("titlepre", resultSet.getString(
                                        "titlepre"));
                memberElem.addAttribute("titlepost",
                                        resultSet.getString("titlepost"));
            }
        }

    }

}
