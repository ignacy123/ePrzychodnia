package converters;

import Model.Person;
import javafx.util.StringConverter;

public class PersonCountConverter extends StringConverter<Person> {
    @Override
    public String toString(Person person) {
        return person.getName()+" "+person.getLastName()+" - "+person.getVisitCount();
    }

    @Override
    public Person fromString(String s) {
        return null;
    }
}
