package converters;

import Model.Specialization;
import javafx.util.StringConverter;

public class SpecializationConverter extends StringConverter<Specialization> {
    @Override
    public String toString(Specialization specialization) {
        return specialization.getPrettyName();
    }

    @Override
    public Specialization fromString(String s) {
        return null;
    }
}
