package converters;

import Model.Exertion;
import javafx.util.StringConverter;

public class ExertionCancelConverter extends StringConverter<Exertion> {
    @Override
    public String toString(Exertion exertion) {
        return exertion.getNurse().getName() + " " + exertion.getNurse().getLastName() + ":\n" + exertion.getPatient().getName() + " "
                + exertion.getPatient().getLastName() + " " + exertion.getStart();
    }

    @Override
    public Exertion fromString(String s) {
        return null;
    }
}
