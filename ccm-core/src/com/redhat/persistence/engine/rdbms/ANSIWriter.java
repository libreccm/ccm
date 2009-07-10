/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.redhat.persistence.engine.rdbms;

import com.redhat.persistence.Condition;
import com.redhat.persistence.common.Path;
import com.redhat.persistence.metadata.Column;
import java.util.Collection;
import java.util.Iterator;

/**
 * ANSIWriter
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #6 $ $Date: 2004/08/16 $
 **/


public class ANSIWriter extends SQLWriter {

    public void write(Select select) {
        write(select.getQuery().generate(getEngine().getSession().getRoot()));
    }

    public void write(Insert insert) {
        Collection cols = insert.getColumns();

        write("insert into ");
        write(insert.getTable().getName());
        write("\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")\nvalues\n(");

        for (Iterator it = cols.iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(insert.get(col));

            if (it.hasNext()) {
                write(", ");
            }
        }

        write(")");
    }

    public void write(Update update) {
        write("update ");
        write(update.getTable().getName());
        write("\nset ");

        for (Iterator it = update.getColumns().iterator(); it.hasNext(); ) {
            Column col = (Column) it.next();
            write(col.getName());
            write(" = ");
            write(update.get(col));
            if (it.hasNext()) {
                write(",\n    ");
            }
        }

        Condition cond = update.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }

    public void write(Delete delete) {
        write("delete from ");
        write(delete.getTable().getName());
        Condition cond = delete.getCondition();
        if (cond != null) {
            write("\nwhere ");
            write(cond);
        }
    }


    public void write(StaticJoin join) {
        write("(");
        // XXX: this is a hack, for binding to work properly we need to call
        // the Operation version of write.
        write((Operation) join.getStaticOperation());
        write(") ");
        write(join.getAlias());
    }

    public void write(SimpleJoin join) {
        write(join.getTable().getName());
        write(" ");
        write(join.getAlias());
    }


    void writeCompound(CompoundJoin join) {
        write(join.getLeft());
        write("\n     ");
        write(join.getType().toString());
        write(" ");
        write(join.getRight());
        Condition cond = join.getCondition();
        if (cond != null) {
            write(" on ");
            write(cond);
        }
    }


    public void write(InnerJoin join) {
        writeCompound(join);
    }

    public void write(LeftJoin join) {
        writeCompound(join);
    }

    public void write(RightJoin join) {
        writeCompound(join);
    }

    public void write(CrossJoin join) {
        writeCompound(join);
    }

}

class RetainUpdatesWriter extends ANSIWriter {

    @Override
    public void write(StaticOperation sop) {
	write(sop.getSQLBlock().getSQL());
    }

}

class UnboundWriter extends ANSIWriter {

    public void write(Path path) {
        write(path.getPath());
    }

}
