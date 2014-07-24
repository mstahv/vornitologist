package com.vornitologist.model;

import java.util.Collection;
import java.util.LinkedList;

public class ClassificationGroup extends ClassificationItem {
    private Collection<ClassificationItem> children = new LinkedList<ClassificationItem>();

    public ClassificationGroup(String latinName) {
        setName(latinName);
    }

    public Collection<ClassificationItem> getChildren() {
        return children;
    }

    public void setChildren(Collection<ClassificationItem> children) {
        this.children = children;
    }

    public static ClassificationGroup AVES;

    @Override
    public ClassificationItem find(String speciesOrGroup) throws Exception {
        if (!speciesOrGroup.isEmpty()) {

            ClassificationItem f = super.find(speciesOrGroup);
            if (f == null) {
                for (ClassificationItem classificationItem : children) {
                    ClassificationItem found = classificationItem
                            .find(speciesOrGroup);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return f;
        }
        return null;
    }

}
