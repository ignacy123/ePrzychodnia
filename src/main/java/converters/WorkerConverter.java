package converters;


import Model.Worker;
import javafx.util.StringConverter;

public class WorkerConverter extends StringConverter<Worker> {
    @Override
    public String toString(Worker worker) {
        return worker.getName()+" "+worker.getLastName()+" - "+worker.getRole();
    }

    @Override
    public Worker fromString(String s) {
        return null;
    }
}
