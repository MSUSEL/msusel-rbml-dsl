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
package edu.montana.gsoc.msusel.rbml.model

import edu.isu.isuese.datamodel.RelationType
import edu.montana.gsoc.msusel.rbml.conformance.BlockType
import edu.montana.gsoc.msusel.rbml.conformance.RoleBlock
import groovy.transform.builder.Builder

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class SPS {

    String name
    def classifiers = []
    List<Role> relations = []
    def constraints = []
    def genHierarchies = []

    @Builder(buildMethodName = "create")
    SPS(String name) {
        this.name = name
    }

    String toString() {
        return "SPS: $name"
    }

    List<RoleBlock> roleBlocks() {
        def blocks = []
        relations.each { Role r ->

            if (r instanceof Relationship) {
                blocks << createBlock((Relationship) r)
            } else if (r instanceof AtLeastOne) {
                r.relations.each {
                    blocks << createBlock((Relationship) it)
                }
            }
        }

        genHierarchies.each { gh ->
            GeneralizationHierarchy hier = (GeneralizationHierarchy) gh
            RoleBlock block = new RoleBlock()

            block.sources = hier.children
            block.dests = hier.children
            block.type = BlockType.GENERALIZATION
            blocks << block
        }
        blocks
    }

    private def createBlock(Relationship r) {
        def sources = handleGenHierarchy(r.source(), r.srcPort)
        def dests = handleGenHierarchy(r.dest(), r.destPort)

        RoleBlock rb = RoleBlock.of(sources, dests)
        setBlockType(rb, r)
        rb
    }

    def handleGenHierarchy(Role role, String port) {
        def roles = []

        if (role instanceof GeneralizationHierarchy) {
            GeneralizationHierarchy g = (GeneralizationHierarchy) role
            if (!g.hasPort(port)) {
                g.getChildren().each {
                    roles << it
                }
            } else {
                roles << g.getPort(port)
            }
        } else {
            roles << role
        }

        roles
    }

    static void setBlockType(block, Relationship r) {
        switch (r) {
            case Generalization:
                block.type = BlockType.GENERALIZATION
                break
            case Realization:
                block.type = BlockType.GENERALIZATION
                break
            case Usage:
                block.type = BlockType.DEPENDENCY
                break
            case Aggregation:
                block.type = BlockType.ASSOCIATION
                break
            case Composition:
                block.type = BlockType.ASSOCIATION
                break
            case Association:
                block.type = BlockType.ASSOCIATION
                break
        }
    }

    Role findTypeRoleByName(String name) {
        if (!name)
            throw new IllegalArgumentException()

        Role role = classifiers.find { it.name == name }
        if (!role)
        {
            genHierarchies.each {
                if (it instanceof GeneralizationHierarchy) {
                    GeneralizationHierarchy gh = (GeneralizationHierarchy) it

                    Role r = gh.children.find { it.name == name }
                    if (r)
                        role = r
                }
            }
        }

        role
    }

    def getSrcRelations(role) {
        Map<RelationType, List<Role>> map = [:]

        relations.each { rel ->
            if (rel instanceof Relationship) {
                if (rel.dest() == role) {
                    RelationType type = getRelationType((Relationship) rel)
                    if (map[type])
                        map[type] << rel.source()
                    else
                        map[type] = [rel.source()]
                }
            }
        }

        return map
    }

    def getDestRelations(role) {
        Map<RelationType, List<Role>> map = [:]

        relations.each { rel ->
            if (rel instanceof Relationship) {
                if (rel.source() == role) {
                    RelationType type = getRelationType((Relationship) rel)
                    if (map[type])
                        map[type] << rel.dest()
                    else
                        map[type] = [rel.dest()]
                }
            }
        }

        return map
    }

    static def getRelationType(Relationship r) {
        switch (r) {
            case Generalization:
                return RelationType.GENERALIZATION
            case Realization:
                return RelationType.GENERALIZATION
            case Usage:
                return RelationType.DEPENDENCY
            case Aggregation:
                return RelationType.ASSOCIATION
            case Composition:
                return RelationType.ASSOCIATION
            case Association:
                return RelationType.ASSOCIATION
            default:
                return RelationType.DEPENDENCY
        }
    }
}
