/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.london.terms.portation.modules.core.categorization;

import com.arsdigita.kernel.ACSObject;
import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;
import com.arsdigita.portation.Portable;
import com.arsdigita.portation.modules.core.categorization.Category;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A domain is collection of categories designed a specific purpose. This entity
 * replaces the {@code Domain} entity from the old
 * {@link com.arsdigita.london.terms.Domain} module as well as the {@code
 * CategoryPurpose} entity from the old {@code ccm-core module}.
 *
 * A {@code Domain} can be mapped to multiple {@link CcmApplication}s. Normally
 * This is used to make a {@code Domain} available in the application. The
 * {@link CcmApplication}s to which a {@code Domain} is mapped are called
 * <em>owners</em> of the domain.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = DomainIdResolver.class,
                  property = "uuid")
public class Domain extends CcmObject implements Portable {

    private String domainKey;
    private String uri;
    private LocalizedString title;
    private LocalizedString description;
    private String version;
    private Date released;
    @JsonIdentityReference(alwaysAsId = true)
    private Category root;
    @JsonIgnore
    private List<DomainOwnership> owners;


    public Domain(com.arsdigita.london.terms.Domain trunkDomain) {
        super(trunkDomain.getKey());


        this.domainKey = trunkDomain.getKey();
        this.uri = trunkDomain.getURL().toString();

        this.title = new LocalizedString();
        this.title.addValue(Locale.getDefault(), trunkDomain.getTitle());
        this.description = new LocalizedString();
        this.description
                .addValue(Locale.getDefault(), trunkDomain.getDescription());

        this.version = trunkDomain.getVersion();
        this.released = trunkDomain.getReleased();

        //this.root

        this.owners = new ArrayList<>();

        NgCoreCollection.domains.put(this.getObjectId(), this);
    }


    public String getDomainKey() {
        return domainKey;
    }

    public void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Date getReleased() {
        return released;
    }

    public void setReleased(final Date released) {
        this.released = released;
    }

    public Category getRoot() {
        return root;
    }

    public void setRoot(final Category root) {
        this.root = root;
    }

    public List<DomainOwnership> getOwners() {
        return owners;
    }

    public void setOwners(final List<DomainOwnership> owners) {
        this.owners = owners;
    }

    public void addOwner(final DomainOwnership owner) {
        this.owners.add(owner);
    }

    public void removeOwner(final DomainOwnership owner) {
        this.owners.remove(owner);
    }
}
