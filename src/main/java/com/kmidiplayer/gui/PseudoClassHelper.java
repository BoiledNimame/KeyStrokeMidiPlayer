package com.kmidiplayer.gui;

import com.kmidiplayer.util.Pair;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class PseudoClassHelper {

    public static Pair<BooleanProperty, PseudoClass> setupPseudoClass(Node control, String pseudoClassName) {

        final PseudoClass pseudo = PseudoClass.getPseudoClass(pseudoClassName);

        return new Pair<BooleanProperty,PseudoClass>(
            new PseudoClassIncludedBooleanProperty<Node>(control, pseudo),
            pseudo
        );

    }

    public static BooleanProperty setupPseudoClass(Node control, PseudoClass pseudo) {
        return new PseudoClassIncludedBooleanProperty<Node>(control, pseudo);
    }

    private static final class PseudoClassIncludedBooleanProperty<T extends Node> extends BooleanPropertyBase {

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
