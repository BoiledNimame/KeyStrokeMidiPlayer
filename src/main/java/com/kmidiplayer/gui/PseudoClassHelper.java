package com.kmidiplayer.gui;

import com.kmidiplayer.util.Pair;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class PseudoClassHelper {

    public static <T extends Node> Pair<BooleanProperty, PseudoClass> setupPseudoClass(T control, String pseudoClassName) {

        final PseudoClass pseudo = PseudoClass.getPseudoClass(pseudoClassName);

        return new Pair<BooleanProperty,PseudoClass>(
            new PseudoClassIncludedBooleanProperty<T>(control, pseudo),
            pseudo
        );

    }

    public static <T extends Node> BooleanProperty setupPseudoClass(T control, PseudoClass pseudo) {
        return new PseudoClassIncludedBooleanProperty<T>(control, pseudo);
    }

    public static final class PseudoClassIncludedBooleanProperty<T extends Node> extends BooleanPropertyBase {

        private final T node;
        private final PseudoClass pseudo;

        private PseudoClassIncludedBooleanProperty(T node, PseudoClass pseudo) {
            this.node = node;
            this.pseudo = pseudo;
        }

        @Override protected void invalidated() {
            node.pseudoClassStateChanged(pseudo, this.get());
        }

        @Override
        public Object getBean() {
            return node;
        }

        @Override
        public String getName() {
            return pseudo.toString();
        }

    }

}
