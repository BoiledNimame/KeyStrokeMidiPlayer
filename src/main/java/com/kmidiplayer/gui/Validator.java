package com.kmidiplayer.gui;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Consumer;

import com.kmidiplayer.midi.util.MidiFileChecker;

import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class Validator {

    static Constraint getIntConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isInt(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    static Constraint getPositiveIntConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isPositiveInt(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    // これ見...パクった
    // https://github.com/palexdev/MaterialFX/blob/main/demo/src/main/java/io/github/palexdev/materialfx/demo/controllers/TextFieldsController.java
    static Constraint getLengthConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(targetProperty.length().greaterThan(0))
            .get();
    }

    static Constraint getExistedMidiFileConstraint(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isExistedMidiFile(targetProperty.get()),
                targetProperty
            ))
            .get();
    }

    public static boolean isExistedMidiFile(String str) {
        final File file = new File(str);
        return (file).exists() && MidiFileChecker.isValid(file);
    }

    public static boolean isInt(String str) {
        final ParsePosition pos = new ParsePosition(0);
        NumberFormat.getIntegerInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }

    public static boolean isPositiveInt(String str) {
        return isInt(str) && str.length()!=0 && 0 < Integer.valueOf(str);
    }

    static ChangeListener<Boolean> buildValidListener(MFXTextField control, Consumer<MFXTextField> toValid, Consumer<MFXTextField> toInvalid) {
        return (new MFXTextFieldControlListener(control, toValid, toInvalid)).getListener();
    }

    /**
     * 無名クラスをどうしても使いたくなかったためinner classで代替
     */
    private static class MFXTextFieldControlListener {

        private final MFXTextField mfxTextField;

        private final Consumer<MFXTextField> transitionToValid;
        private final Consumer<MFXTextField> transitionToInvalid;

        private final ChangeListener<Boolean> listenerMethod;

        private MFXTextFieldControlListener(MFXTextField mfxTextField, Consumer<MFXTextField> toValid, Consumer<MFXTextField> toInvalid) {
            this.mfxTextField = mfxTextField;
            listenerMethod = this::validListener;
            transitionToValid = toValid;
            transitionToInvalid = toInvalid;
        }

        private void validListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!oldValue && newValue) { // invalid -> valid
                transitionToValid.accept(mfxTextField);
            }
            if (oldValue && !newValue) { // valid -> invalid
                transitionToInvalid.accept(mfxTextField);
            }
        }

        private ChangeListener<Boolean> getListener() {
            return listenerMethod;
        }
    }

}
