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

import edu.montana.gsoc.msusel.rbml.PatternManager
import edu.montana.gsoc.msusel.rbml.events.BehaviorParameterTypeResolution
import edu.montana.gsoc.msusel.rbml.events.BehaviorTypeResolution
import edu.montana.gsoc.msusel.rbml.model.*

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class BehavioralFeatureRoleFactory extends AbstractFactory {

    BehavioralFeatureRoleFactory() {
        // TODO Auto-generated constructor stub
    }

    boolean isLeaf() {
        return false
    }

    def newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes)
    throws InstantiationException, IllegalAccessException {
        BehavioralFeature inst = new BehavioralFeature()

        if (value) {
            String n
            String params = ""
            String type = ""
            String vstring = value.toString()

            if (vstring.contains("("))
                n = vstring.split(/\(/)[0]
            else
                n = vstring

            if (vstring.contains("(") && vstring.contains(")")) {
                vstring.find(/\((.*)\)/) {
                    params = it[1]
                }
            }

            if (vstring.contains(")")) {
                if (vstring.split(/\)/).length > 1) {
                    if (vstring.split(/\)/)[1].contains(":"))
                        type = vstring.split(/\)/)[1].split(":")[1].trim()
                }
            }

            inst.name = n
            if (params.contains(",")) {
                params.split(",").each {
                    if (it.contains(":")) {
                        String var = it.split(":")[0]
                        String typ = it.split(":")[1]
                        def param = new Parameter(variable: var)
                        inst.params << param
                        PatternManager.instance.events << new BehaviorParameterTypeResolution(role: param, type: typ)
                    } else {
                        String var = it
                        Classifier tc = new UnspecifiedType()
                        inst.params << new Parameter(variable: var, type: tc)
                    }
                }
            } else {
                if (params.contains(":")) {
                    String var = params.split(":")[0]
                    String typ = params.split(":")[1]
                    def param = new Parameter(variable: var)
                    inst.params << param
                    PatternManager.instance.events << new BehaviorParameterTypeResolution(role: param, type: typ)
                } else if (!params.isEmpty()) {
                    String var = params
                    Classifier tc = new UnspecifiedType()
                    inst.params << new Parameter(variable: var, type: tc)
                }
            }

            Classifier tc = null
            if (type != "") {
                println "RBML type found: " + type
                PatternManager.instance.events << new BehaviorTypeResolution(role: inst, type: type)
            } else {
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
            if (attributes["properties"]) {
                inst.props = attributes["properties"]
            }
        }
        attributes.clear()
        inst.setMult(new Multiplicity(lower: lower, upper: upper))
        inst
    }

    @Override
    void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        if (parent != null && parent instanceof Classifier) {
            parent.behFeats << child
        }
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object child) {
    }
}
