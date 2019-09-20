package org.genose.java.implementation.javafx.applicationtools.views.customviewscontroller;

import javafx.scene.Node;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

abstract public class JFXApplicationTransitionnalPaneView implements JFXApplicationScreens, Function {
// here we store some views (AnchorPane, Pane,<Type Node>) to flip / switch between them ...
    private HashMap<String, Node> aAttachedTransitionableViews = null;

    /**
     *
     */
    public JFXApplicationTransitionnalPaneView() {
        aAttachedTransitionableViews = new HashMap<>();
    }

    /**
     *
     * @param iDurationMillis
     * @return
     */
    public JFXApplicationTransitionnalPaneView fadeOut(Integer iDurationMillis) {
        return this;
    }

    /**
     *
     * @param iDurationMillis
     * @return
     */
    public JFXApplicationTransitionnalPaneView fadeOutThenFadeInWithView(Integer iDurationMillis, String sArgAttachedViewsName) {
        return this;
    }
    /**
     *
     * @param iDurationMillis
     * @return
     */
    public JFXApplicationTransitionnalPaneView fadeIn(Integer iDurationMillis) {
        return this;
    }

    /**
     *
     * @return
     */
    public HashMap<String, Node> getAttachedTransitionableViews() {
        return aAttachedTransitionableViews;
    }

    /**
     * @param sArgTransionIndexName
     * @param oArgAttachedTransitionableView
     */
    public void addAttachedView(String sArgTransionIndexName, Node oArgAttachedTransitionableView) {
        aAttachedTransitionableViews = Objects.requireNonNullElse(aAttachedTransitionableViews, (new HashMap<>()));
        aAttachedTransitionableViews.put(sArgTransionIndexName, oArgAttachedTransitionableView);
    }
    /**
     * @param sArgTransionIndexName
     */
    public Node getAttachedView(String sArgTransionIndexName) {
        aAttachedTransitionableViews = Objects.requireNonNullElse(aAttachedTransitionableViews, (new HashMap<>()));
        return aAttachedTransitionableViews.get(sArgTransionIndexName);
    }
    /**
     * @param sArgTransionIndexName
     * @param oArgAttachedTransitionableView
     */
    public void removeAttachedView(String sArgTransionIndexName, Node oArgAttachedTransitionableView) {
        aAttachedTransitionableViews = Objects.requireNonNullElse(aAttachedTransitionableViews, (new HashMap<>()));
        aAttachedTransitionableViews.put(sArgTransionIndexName, oArgAttachedTransitionableView);
    }


    public Object andThen()
    {
        return this;
    }
    @Override
    public Object apply(Object o) {
        return this;
    }
}
