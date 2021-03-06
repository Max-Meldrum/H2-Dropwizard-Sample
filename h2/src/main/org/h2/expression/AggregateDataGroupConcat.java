/*
 * Copyright 2004-2013 H2 Group. Multiple-Licensed under the H2 License,
 * Version 1.0, and under the Eclipse Public License, Version 1.0
 * (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.expression;

import java.util.ArrayList;
import org.h2.engine.Database;
import org.h2.util.New;
import org.h2.util.ValueHashMap;
import org.h2.value.Value;
import org.h2.value.ValueNull;

/**
 * Data stored while calculating a GROUP_CONCAT aggregate.
 */
class AggregateDataGroupConcat extends AggregateData {
    private ArrayList<Value> list;
    private ValueHashMap<AggregateDataGroupConcat> distinctValues;

    @Override
    void add(Database database, int dataType, boolean distinct, Value v) {
        if (v == ValueNull.INSTANCE) {
            return;
        }
        if (distinct) {
            if (distinctValues == null) {
                distinctValues = ValueHashMap.newInstance();
            }
            distinctValues.put(v, this);
            return;
        }
        if (list == null) {
            list = New.arrayList();
        }
        list.add(v);
    }

    @Override
    Value getValue(Database database, int dataType, boolean distinct) {
        if (distinct) {
            groupDistinct(database, dataType);
        }
        return null;
    }

    ArrayList<Value> getList() {
        return list;
    }

    private void groupDistinct(Database database, int dataType) {
        if (distinctValues == null) {
            return;
        }
        for (Value v : distinctValues.keys()) {
            add(database, dataType, false, v);
        }
    }
}
