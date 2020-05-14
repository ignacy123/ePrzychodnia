package converters;

import Model.Disease;
import javafx.util.StringConverter;

public class DiseaseConverter extends StringConverter<Disease> {
    @Override
    public String toString(Disease disease) {
        return disease.getPrettyName();
    }

    @Override
    public Disease fromString(String s) {
        return null;
    }
}
