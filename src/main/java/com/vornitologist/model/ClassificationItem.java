package com.vornitologist.model;


public class ClassificationItem {

    private String name;

    private ClassificationGroup parent;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParent(ClassificationGroup parent) {
        this.parent = parent;
    }

    public ClassificationGroup getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Finds classification item based on its name. Throws on unknown name.
     * 
     * @param speciesOrGroup
     * @throws Exception
     */
    public ClassificationItem find(String speciesOrGroup) throws Exception {
        if (getName().equals(speciesOrGroup)) {
            return this;
        }
        return null;
    }

}
