package converters;

import Model.Referral;
import javafx.util.StringConverter;

public class ReferralConverter extends StringConverter<Referral> {
    @Override
    public String toString(Referral referral) {
        return referral.getSpecialization().getPrettyName()+" - "+referral.getNote();
    }

    @Override
    public Referral fromString(String s) {
        return null;
    }
}
