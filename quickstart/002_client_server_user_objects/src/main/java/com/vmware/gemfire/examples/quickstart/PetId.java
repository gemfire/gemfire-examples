/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 */

package com.vmware.gemfire.examples.quickstart;

public class PetId implements java.io.Serializable{
    
    int idNum;
    String name;

    public PetId() {
    }

    public PetId(int idNum, String name) {
        this.idNum = idNum;
        this.name = name;
    }

    //IDE Generated Method
    @Override
    public String toString() {
        return "PetId [idNum=" + idNum + ", name=" + name + "]";
    }

    //IDE Generated Method
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idNum;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        PetId other = (PetId) obj;
        if (idNum != other.idNum)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    
}
