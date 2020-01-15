package edu.montana.gsoc.msusel.rbml.conformance

import edu.isu.isuese.datamodel.Component
import edu.montana.gsoc.msusel.rbml.model.Feature

class FeatureBinding {

    Feature feat
    Component comp

    private FeatureBinding(feat, comp) {
        if (!feat || !comp)
            throw new IllegalArgumentException()

        this.feat = feat
        this.comp = comp
    }

    static FeatureBinding of(Feature feat, Component comp) {
        new FeatureBinding(feat, comp)
    }
}
