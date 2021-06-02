/**
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
package edu.montana.gsoc.msusel.rbml.conformance


import edu.isu.isuese.datamodel.Type
import edu.montana.gsoc.msusel.rbml.model.ClassRole
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.InterfaceRole
import edu.montana.gsoc.msusel.rbml.model.Role
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
@ToString(includes = ["sources", "dests"])
@EqualsAndHashCode
class RoleBlock {

    List<Role> sources
    List<Role> dests
    BlockType type

    static RoleBlock of (Role source, Role dest) {
        new RoleBlock(sources: [source], dests: [dest])
    }

    static RoleBlock of (List<Role> sources, List<Role> dests) {
        new RoleBlock(sources: sources, dests: dests)
    }

    boolean matchesSource(Type comp) {
        return (comp.getType() == Type.CLASS && contains(sources, ClassRole.class)) ||
                (comp.getType() == Type.INTERFACE && contains(sources, InterfaceRole.class)) ||
                (contains(sources, Classifier.class))
    }

    boolean matchesDest(Type comp) {
        return (comp.getType() == Type.CLASS && contains(dests, ClassRole.class)) ||
                (comp.getType() == Type.INTERFACE && contains(dests, InterfaceRole.class)) ||
                (contains(dests, Classifier.class))
    }

    Role findSourceMatch(Type comp) {
        findMatch(comp, sources)
    }

    Role findDestMatch(Type comp) {
        findMatch(comp, dests)
    }

    Role findMatch(Type comp, List<Role> list) {
        Role role

        switch (comp.getType()) {
            case Type.CLASS:
                role = list.find { it instanceof ClassRole || it instanceof Classifier }
                break
            case Type.INTERFACE:
                role = list.find { it instanceof InterfaceRole || it instanceof Classifier }
                break
            default:
                role = list.find { it instanceof Classifier }
                break
        }

        role
    }

    Role sharesWith(RoleBlock other) {
        if (sources.findAll { it in other.sources }) {
            sources.findAll { it in other.sources }.first()
        } else if (sources.findAll { it in other.dests }) {
            sources.findAll { it in other.dests }.first()
        } else if (dests.findAll {it in other.dests }) {
            dests.findAll { it in other.dests }.first()
        } else if (dests.findAll {it in other.sources}) {
            dests.findAll { it in other.sources }.first()
        }
    }

    private boolean contains(List<Role> list, type) {
        list.find { it.class == type }
    }
}
