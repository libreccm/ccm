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
package com.arsdigita.london.terms.portation.modules.core.core;

import com.arsdigita.london.terms.portation.conversion.NgCoreCollection;
import com.arsdigita.london.terms.portation.modules.core.web.CcmApplication;
import com.arsdigita.portation.modules.core.core.CcmObject;
import com.arsdigita.portation.modules.core.l10n.LocalizedString;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The {@code Resource} class is a base class for several other classes, for
 * example the {@link CcmApplication} class.
 *
 * Resources can be nested, a resource can have multiple child resources.
 *
 * This class is an adopted variant of the class
 * {@code com.arsdigita.kernel.Resource} from the old structure. This class is
 * maybe removed in future releases. Therefore it is strictly recommend not to
 * use this class directly.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 7/27/17
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = ResourceIdResolver.class,
                  property = "uuid")
public class Resource extends CcmObject {

    private LocalizedString title;
    private LocalizedString description;
    @JsonIdentityReference(alwaysAsId = true)
    private ResourceType resourceType;
    private Date created;
    @JsonIgnore
    private List<Resource> childs;
    @JsonIdentityReference(alwaysAsId = true)
    private Resource parent;


    public Resource(com.arsdigita.kernel.Resource trunkObject) {
        super(trunkObject);

        this.title = new LocalizedString();
        this.title.addValue(Locale.getDefault(), trunkObject.getTitle());
        this.description = new LocalizedString();
        this.description
                .addValue(Locale.getDefault(), trunkObject.getDescription());

        //this.resourceType;
        this.created = trunkObject.getTimestamp();
        this.childs = new ArrayList<>();
        //this.parent

        NgCoreCollection.resources.put(this.getObjectId(), this);
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

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(final ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public List<Resource> getChilds() {
        return childs;
    }

    public void setChilds(final List<Resource> childs) {
        this.childs = childs;
    }

    public void addChild(final Resource child) {
        this.childs.add(child);
    }

    public void removeChild(final Resource child) {
        this.childs.remove(child);
    }

    public Resource getParent() {
        return parent;
    }

    public void setParent(final Resource parent) {
        this.parent = parent;
    }
}
