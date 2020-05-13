package converters;

import Model.Person;
import javafx.util.StringConverter;

public class PersonConverter extends StringConverter<Person> {
    @Override
    public String toString(Person person) {
        return person.getName()+" "+person.getLastName();
    }

    @Override
    public Person fromString(String s) {
        return null;
    }
}
