package Model;

import java.util.Objects;

public class Specialization {
    Integer id;
    String prettyName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public void setPrettyName(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String toString() {
        return prettyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Specialization that = (Specialization) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(prettyName, that.prettyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, prettyName);
    }
}
