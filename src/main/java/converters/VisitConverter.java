package converters;

import Model.Visit;
import javafx.util.StringConverter;

public class VisitConverter extends StringConverter<Visit> {
    @Override
    public String toString(Visit visit) {
        return visit.getPatient().getName()+" "+visit.getPatient().getLastName()+" "+visit.getDate();
    }

    @Override
    public Visit fromString(String s) {
        return null;
    }
}
