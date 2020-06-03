package converters;

import Model.Exertion;
import javafx.util.StringConverter;

public class ExertionConverter extends StringConverter<Exertion> {
    @Override
    public String toString(Exertion exertion) {
        return exertion.getPatient().getName() + " "
                + exertion.getPatient().getLastName() + " " + exertion.getStart();
    }

    @Override
    public Exertion fromString(String s) {
        return null;
    }
}
