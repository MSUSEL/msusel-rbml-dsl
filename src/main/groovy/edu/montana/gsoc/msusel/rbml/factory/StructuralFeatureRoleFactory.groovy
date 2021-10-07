/*
 * The MIT License (MIT)
 *
 * MSUSEL RBML DSL
 * Copyright (c) 2015-2019 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory and Idaho State University, Informatics and
 * Computer Science, Empirical Software Engineering Laboratory
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package edu.montana.gsoc.msusel.rbml.factory

import edu.montana.gsoc.msusel.rbml.PatternManager
import edu.montana.gsoc.msusel.rbml.events.StructuralTypeResolution
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.Multiplicity
import edu.montana.gsoc.msusel.rbml.model.StructuralFeature
import edu.montana.gsoc.msusel.rbml.model.UnspecifiedType

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class StructuralFeatureRoleFactory extends AbstractFactory {

    boolean isLeaf() {
        return false
    }

    def newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        StructuralFeature inst = new StructuralFeature()

        if (value) {
            if (value.contains(":")) {
                String n = value.split(":")[0].trim()
                String type = value.split(":")[1].trim()

                inst.name = n

                PatternManager.instance.events << new StructuralTypeResolution(role: inst, type: type)
            } else {
                inst.name = value
                inst.type = new UnspecifiedType()
            }
        }

        int lower = 1
        int upper = 1
        if (attributes) {
            if (attributes["mult"] != null) {
                String mult = attributes["mult"]
                mult = mult.replaceAll("\\*", "-1")
                if (mult.contains("..")) {
                    lower = new Integer(mult.split("\\.\\.")[0])
                    upper = new Integer(mult.split("\\.\\.")[1])
                }
            }

            if (attributes["properties"])
                inst.props = attributes["properties"]
        }
        attributes.clear()
        inst.setMult(new Multiplicity(lower: lower, upper: upper))
        inst
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent != null && parent instanceof Classifier) {
            parent.structFeats << child
        }
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object child) {
    }
}
