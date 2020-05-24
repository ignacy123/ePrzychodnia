package converters;

import Model.Office;
import javafx.util.StringConverter;

public class OfficeConverter extends StringConverter<Office> {
    @Override
    public String toString(Office office) {
        return office.getId()+" - "+office.getType();
    }

    @Override
    public Office fromString(String s) {
        return null;
    }
}
