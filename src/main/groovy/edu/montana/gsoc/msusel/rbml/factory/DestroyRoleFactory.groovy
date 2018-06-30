/**
 * The MIT License (MIT)
 *
 * MSUSEL RBML DSL
 * Copyright (c) 2015-2018 Montana State University, Gianforte School of Computing,
 * Software Engineering Laboratory
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

import edu.montana.gsoc.msusel.rbml.model.DestroyMessage
import edu.montana.gsoc.msusel.rbml.model.Gate
import edu.montana.gsoc.msusel.rbml.model.Lifeline
import edu.montana.gsoc.msusel.rbml.model.Trace
import edu.montana.gsoc.msusel.rbml.model.UnknownLifeline
import edu.montana.gsoc.msusel.rbml.PatternManager

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class DestroyRoleFactory extends AbstractFactory {

    boolean isLeaf() {
        return true
    }

    def newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        DestroyMessage msg = new DestroyMessage()

        msg.setName(value)

        if (attributes) {
            if (!attributes['source'])
                msg.setSource(new Gate())
            else {
                if (PatternManager.current != null) {
                    Lifeline src = PatternManager.findLifeline(attributes["source"])
                    if (src)
                        msg.setSource(src)
                    else
                        msg.setSource(new UnknownLifeline(name: attributes["source"]))
                }
            }

            if (attributes['dest']) {
                if (PatternManager.current != null) {
                    Lifeline dest = PatternManager.findLifeline(attributes["dest"])
                    if (dest)
                        msg.setDest(dest)
                    else
                        msg.setDest(new UnknownLifeline(name: attributes["dest"]))
                } else {
                    msg.setDest(new UnknownLifeline(name: attributes["dest"]))
                }
            }
        }

        attributes.clear()

        msg
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent && parent instanceof Trace) {
            parent.fragments << child
        }
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object child) {
    }
}
