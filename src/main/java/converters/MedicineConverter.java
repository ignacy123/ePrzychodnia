package converters;

import Model.Medicine;
import javafx.util.StringConverter;

public class MedicineConverter extends StringConverter<Medicine> {
    @Override
    public String toString(Medicine medicine) {
        return medicine.getName()+" - "+medicine.getInstruction();
    }

    @Override
    public Medicine fromString(String s) {
        return null;
    }
}
