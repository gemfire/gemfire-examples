/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 */

package com.vmware.gemfire.examples.quickstart;

public class Pet {
    String breed;
    String name;
    String owner;
    
    public Pet() {
    }

    public Pet(String breed, String name, String owner) {
        this.breed = breed;
        this.name = name;
        this.owner = owner;
    }
    
    public String getBreed() {
        return breed;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    //IDE Generated Method
    @Override
    public String toString() {
        return "Pet [breed=" + breed + ", name=" + name + ", owner=" + owner
                + "]";
    }

    //IDE Generated Method
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((breed == null) ? 0 : breed.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        return result;
    }

    //IDE Generated Method
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pet other = (Pet) obj;
        if (breed == null) {
            if (other.breed != null)
                return false;
        } else if (!breed.equals(other.breed))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (owner == null) {
            if (other.owner != null)
                return false;
        } else if (!owner.equals(other.owner))
            return false;
        return true;
    }
    
    
}
