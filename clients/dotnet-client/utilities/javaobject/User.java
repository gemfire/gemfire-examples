package javaobject;

import java.util.*;
import java.io.*;
import org.apache.geode.*;
import org.apache.geode.cache.Declarable;

public class User implements DataSerializable {

    String name;
    int id;

    static {
        Instantiator.register(new Instantiator(javaobject.User.class, 500) {
            public DataSerializable newInstance() {
                return new User();
            }
        });
    }

    public User() {
    }

    public User(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", id=" + id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (id != other.id)
            return false;
        return true;
    }

    public void fromData(DataInput in) throws IOException, ClassNotFoundException {
        this.name = DataSerializer.readString(in);
        this.id = DataSerializer.readInteger(in);
    }

    public void toData(DataOutput out) throws IOException {
        DataSerializer.writeString(this.name, out);
        DataSerializer.writeInteger(this.id, out);
    }

}
