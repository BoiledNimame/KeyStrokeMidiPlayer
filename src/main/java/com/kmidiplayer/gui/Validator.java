package com.kmidiplayer.gui;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.function.Consumer;

import com.kmidiplayer.config.Options;
import com.kmidiplayer.midi.util.MidiFileChecker;

import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.materialfx.validation.Severity;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class Validator {

    // CSSで :hover みたいなやつを自分で書いて適用されるようになる
    static final PseudoClass invalid = PseudoClass.getPseudoClass("invalid");

    static <T extends Node> void setValid(T node) {
        node.pseudoClassStateChanged(invalid, false);
    }

    static <T extends Node> void setInvalid(T node) {
        node.pseudoClassStateChanged(invalid, true);
    }

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

    static Constraint getCollectInRangeOfNoteNumberOffset(StringProperty targetProperty) {
        return Constraint.Builder.build()
            .setSeverity(Severity.ERROR)
            .setCondition(Bindings.createBooleanBinding(
                () -> isNumberCollectInRangeOfNoteNumberOffset(targetProperty.get()),
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
        return isInt(str)
            && str.length() != 0
            && 0 < Integer.valueOf(str);
    }

    public static boolean isNumberCollectInRangeOfNoteNumberOffset(String str) {
        return isInt(str)
            && str.length() != 0
            && 0 <= Options.definedNoteMin.get() + Integer.valueOf(str)
            && Options.definedNoteMax.get() + Integer.valueOf(str) <= 127;
    }

    /**
     * バリデーションの結果によってコントロールを改変する場合に使いやすいリスナーを返すメソッド
     * @param <T>       改変されるコントロールの型。
     * @param control   改変されるコントロール。
     * @param toValid   バリデータの結果が有効に遷移した際にコントロールを改変するタスク。
     * @param toInvalid バリデータの結果が無効に遷移した際にコントロールを改変するタスク。
     * @return 自分自身を引数に取るタスクをバリデーションの結果が有効/無効に遷移した時に実行するリスナーを返す。
     */
    static <T> ChangeListener<Boolean> buildValidListener(T control, Consumer<T> toValid, Consumer<T> toInvalid) {
        return (new ValidatedControlChanger<T>(control, toValid, toInvalid)).getListener();
    }

    /**
     * 無名クラスをどうしても使いたくなかったためinner classで代替
     */
    private static final class ValidatedControlChanger<T> {

        private final T mfxTextField;

        private final Consumer<T> transitionToValid;
        private final Consumer<T> transitionToInvalid;

        private final ChangeListener<Boolean> listenerMethod;

        private ValidatedControlChanger(T mfxTextField, Consumer<T> toValid, Consumer<T> toInvalid) {
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

    /**
     * バリデーションの結果が変化した際に特定のタスクを行う場合に使いやすいリスナーを返すメソッド
     * @param toValid   バリデータの結果が有効に遷移した際に行うタスク。
     * @return バリデーションの結果が有効/無効に遷移した時に実行するリスナーを返す。
     */
    static ChangeListener<Boolean> buildValidListener(Runnable onChanged) {
        return (new ValidatedTaskRunner(onChanged)).getListener();
    }

    /**
     * 無名クラスをどうしても使いたくなかったためinner classで代替
     */
    private static final class ValidatedTaskRunner {

        private final Runnable onChanged;

        private final ChangeListener<Boolean> listenerMethod;

        private ValidatedTaskRunner(Runnable onChanged) {
            listenerMethod = this::validListener;
            this.onChanged = onChanged;
        }

        private void validListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!oldValue && newValue) { // invalid -> valid
                onChanged.run();
            }
            if (oldValue && !newValue) { // valid -> invalid
                onChanged.run();
            }
        }

        private ChangeListener<Boolean> getListener() {
            return listenerMethod;
        }
    }

}
