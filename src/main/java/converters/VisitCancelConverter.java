package converters;

import Model.Visit;
import javafx.util.StringConverter;

public class VisitCancelConverter extends StringConverter<Visit> {
    @Override
    public String toString(Visit visit) {
        return visit.getDoctor().getName()+" "+visit.getDoctor().getLastName()+":\n"+visit.getPatient().getName()+" "+visit.getPatient().getLastName()+" "+visit.getStart();
    }

    @Override
    public Visit fromString(String s) {
        return null;
    }
}
