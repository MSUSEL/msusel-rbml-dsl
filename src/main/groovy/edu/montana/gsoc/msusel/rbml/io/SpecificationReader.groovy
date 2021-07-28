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
package edu.montana.gsoc.msusel.rbml.io

import edu.montana.gsoc.msusel.rbml.model.*

/**
 * @author Isaac Griffith
 * @version 1.3.0
 */
class SpecificationReader {

    Map<String, Classifier> roles = [:]
    Map<String, GeneralizationHierarchy> ghs = [:]
    Map<String, Relationship> relations = [:]
    SPS sps
    GeneralizationHierarchy current = null

    void processSPS(map) {
        if (!map)
            return
        if (!map.SPS)
            throw new MissingContentException("Specification does not start with 'SPS'")
        if (!map.SPS.name || !map.SPS.roles)
            throw new MissingContentException("SPS is missing one of the required: name or roles")

        String name = map.SPS.name
        processRoles(map.SPS.roles)
        processRelations(map.SPS.relations)
        processRolesStep2(map.SPS.roles)

        sps = SPS.builder().name(name).create()
        sps.classifiers += roles.values()
        sps.genHierarchies += ghs.values()
        if (relations)
            sps.relations += relations.values()
    }

    def processRoles(map) {
        def roles = []
        map.each { r ->
            r.each { k, v ->
                switch (k) {
                    case "Class":
                        roles << processClass(v)
                        break
                    case "Classifier":
                        roles << processClassifier(v)
                        break
                    case "Interface":
                        roles << processInterface(v)
                        break
                    case "GenHierarchy":
                        processGenHierarchy(v)
                        break
                }
            }
        }
        roles
    }

    void processRolesStep2(map) {
        map.each { r ->
            r.each { k, v ->
                if (k == "GenHierarchy")
                {
                    if (v.Classifier)
                        processTypeStep2(v.Classifier)
                    else if (v.Class)
                        processTypeStep2(v.Class)
                    processRolesStep2(v.children)
                    copyOverFeatures(v.name)
                } else {
                    processTypeStep2(v)
                }
            }
        }
    }

    def processClassifier(map) {
        if (map == null)
            throw new IllegalArgumentException()

        String name = map.name
        String mult = map.mult ?: "1..1"

        def r = Classifier.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .create()

        if (!current)
            roles[r.name] = r
        else {
            r.ghMember = true
            String t = "${r.name}@${current.name}"
            t = r.name
            roles[t] = r
        }

        r
    }

    def processTypeStep2(map) {
        if (map == null)
            throw new IllegalArgumentException()

        Classifier r = roles[map.name]

        if (r) {
            if (map.features) {
                processFeatures(r, map.features)
            }
        }
    }

    def processInterface(map) {
        if (map == null)
            throw new IllegalArgumentException()

        String name = map.name
        String mult = map.mult ?: "1..1"

        def r = InterfaceRole.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .create()
        if (!current)
            roles[r.name] = r
        else {
            r.ghMember = true
            String t = "${r.name}@${current.name}"
            t = r.name
            roles[t] = r
        }

        r
    }

    def processClass(map) {
        if (map == null)
            throw new IllegalArgumentException()

        String name = map.name
        String mult = map.mult ?: "1..1"

        def r = ClassRole.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .create()
        if (!current)
            roles[r.name] = r
        else {
            r.ghMember = true
            String t = "${r.name}@${current.name}"
            t = r.name
            roles[t] = r
        }

        r
    }

    void processGenHierarchy(map) {
        if (!map || map.isEmpty())
            return

        if (!map.name || !(map.Classifier || map.Class) || !map.children)
            throw new MissingContentException()

        String name = map.name
        String mult = map.mult ? map.mult : "1..1"

        GeneralizationHierarchy gen = GeneralizationHierarchy.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .create()
        ghs[name] = gen
        current = gen
        if (map.Classifier)
            gen.root = processClassifier(map.Classifier)
        else if (map.Class)
            gen.root = processClass(map.Class)
        gen.children = processRoles(map.children)

        current = null
        roles
    }

    void processFeatures(Classifier r, map) {
        if (!map)
            return
        if (!r)
            throw new IllegalArgumentException()

        map.each { s, v ->
            def feat
            switch (s) {
                case ~/s\d+/:
                    feat = processStructuralFeature(v)
                    if (feat) r.structFeats << feat
                    break
                case ~/b\d+/:
                    feat = processBehavioralFeature(v)
                    if (feat) r.behFeats << feat
                    break
            }
        }
    }

    def processBehavioralFeature(map) {
        if (!map || map.isEmpty())
            return null

        String name = map.name
        String mult = map.mult
        boolean abstrct = map."abstract" ? Boolean.parseBoolean(map.'abstract') : false
        boolean statc = map.'static' ? Boolean.parseBoolean(map.'static') : false
        def t = map.type ?: null
        Classifier type = null
        if (t)
            type = findType(t)

        def feat = BehavioralFeature.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .type(type)
                .isStatic(statc)
                .isAbstract(abstrct)
                .create()

        processParams(feat, map.params)

        feat
    }

    void processParams(BehavioralFeature feat, params) {
        params.each { p ->
            p.each { String k, String v ->
                String var = k
                Classifier type = findType(v)

                feat.params << Parameter.builder()
                        .mult(Multiplicity.fromString("1..1"))
                        .variable(var)
                        .type(type)
                        .create()
            }
        }
    }

    def processStructuralFeature(map) {
        if (!map || map.isEmpty())
            return null

        String name = map.name
        String mult = map.mult
        String t = map.type
        boolean statc = map.static ? Boolean.parseBoolean(map.static) : false
        boolean readOnly = map.readOnly ? Boolean.parseBoolean(map.readOnly) : false
        Classifier type = findType(t)

        StructuralFeature.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .type(type)
                .isStatic(statc)
                .isReadOnly(readOnly)
                .create()
    }

    private def findType(String t) {
        Classifier type = null
        if (t.contains("@")) {
            t = t.split("@")[1]
        }
        if (roles[t]) {
            type = (Classifier) roles[t]
//        } else {
//            type = UnknownType.builder().name(t).create()
//            roles[t] = type
        }
        type
    }

    void processRelations(map) {
        map.each { r ->
            r.each { k, v ->
                switch (k) {
                    case "Association":
                        processAssociation(v)
                        break
                    case "Composition":
                        processComposition(v)
                        break
                    case "Aggregation":
                        processAggregation(v)
                        break
                    case "Usage":
                        processUsage(v)
                        break
                    case "Generalization":
                        processGeneralization(v)
                        break
                    case "Realization":
                        processRealization(v)
                        break
                    case "Create":
                        processCreate(v)
                        break
                }
            }
        }
    }

    void processGeneralization(map) {
        processGenReal(map, Generalization.&builder)
    }

    void processRealization(map) {
        processGenReal(map, Realization.&builder)
    }

    private processGenReal(map, builder) {
        if (!map)
            return
        if (!map.parent || !map.child)
            throw new MissingContentException()

        String name = map.name
        String mult = map.mult ?: "0..1"
        String child = map.child
        String parent = map.parent
        String atLeastOne = map.atLeastOne ?: null

        def (srcRole, srcPort) = processRelationType(child)
        def (destRole, destPort) = processRelationType(parent)

        Relationship rel = builder().name(name)
                .mult(Multiplicity.fromString(mult))
                .child((Role) srcRole)
                .parent((Role) destRole)
                .create()

        rel.srcPort = srcPort
        rel.destPort = destPort
        rel.atLeastOne = atLeastOne
        relations[name] = rel
    }

    void processAssociation(map) {
        procAssoc(map, Association.&builder)
    }

    void processComposition(map) {
        procAssoc(map, Composition.&builder)
    }

    void processAggregation(map) {
        procAssoc(map, Aggregation.&builder)
    }

    private void procAssoc(map, builder) {
        if (!map)
            return
        if (!map.source || !map.dest)
            throw new MissingContentException()

        String name = map.name
        String mult = map.mult
        String srcName = map.source.name
        String srcMult = map.source.mult
        String srcType = map.source.type
        String destName = map.dest.name
        String destMult = map.dest.mult
        String destType = map.dest.type
        String atLeastOne = map.atLeastOne ?: null

        def (srcRole, srcPort) = processRelationType(srcType)
        def (destRole, destPort) = processRelationType(destType)

        Relationship rel = builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .srcName(srcName)
                .srcMult(Multiplicity.fromString(srcMult))
                .srcType((Role) srcRole)
                .destName(destName)
                .destMult(Multiplicity.fromString(destMult))
                .destType((Role) destRole)
                .create()

        rel.atLeastOne = atLeastOne
        rel.srcPort = srcPort
        rel.destPort = destPort
        relations[name] = rel
    }

    void processUsage(map) {
        if (!map)
            return
        if (!map.source || !map.dest)
            throw new MissingContentException("Missing source or dest fields of Usage")

        String name = map.name
        String mult = map.mult
        String src = map.source
        String dest = map.dest
        String atLeastOne = map.atLeastOne ?: null

        def (srcRole, srcPort) = processRelationType(src)
        def (destRole, destPort) = processRelationType(dest)

        Usage use = Usage.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .source((Role) srcRole)
                .dest((Role) destRole)
                .create()

        use.srcPort = srcPort
        use.destPort = destPort
        use.atLeastOne = atLeastOne
        relations[name] = use
    }

    void processCreate(map) {
        if (!map)
            return
        if (!map.source || !map.dest)
            throw new MissingContentException()

        String name = map.name
        String mult = map.mult
        String src = map.source
        String dest = map.dest
        String atLeastOne = map.atLeastOne ?: null

        def (srcRole, srcPort) = processRelationType(src)
        def (destRole, destPort) = processRelationType(dest)

        Create create = Create.builder()
                .name(name)
                .mult(Multiplicity.fromString(mult))
                .source((Role) srcRole)
                .dest((Role) destRole)
                .create()

        create.srcPort = srcPort
        create.destPort = destPort
        create.atLeastOne = atLeastOne
        relations[name] = create
    }

    def processRelationType(String type) {
        Role role
        String port = ""
        if (type.contains("@")) {
            role = (GeneralizationHierarchy) ghs[type.split(/@/)[0]]
            port = type.split(/@/)[1]
        } else {
            role = findType(type)
        }

        return [role, port]
    }

    def copyOverFeatures(String ghName) {
        GeneralizationHierarchy gh = ghs[ghName]

        Classifier root = gh.getRoot()
        root.behFeats.each { BehavioralFeature feat ->
            gh.children.each { child ->
                if (child instanceof ClassRole) {
                    if(!child.behFeats.contains(feat) && (feat.isStatic || !feat.isAbstract)) {
                        child.behFeats << feat
                    }
                } else if (child instanceof Classifier) {
                    if (child.isAbstrct()) {
                        if (child.behFeats.contains(feat) && (!feat.isStatic || feat.isAbstract)) {
                            child.behFeats << feat
                        }
                    }
                }
            }
        }
        root.structFeats.each { StructuralFeature feat ->
            gh.children.each { child ->
                if (child instanceof ClassRole) {
                    if(!child.structFeats.contains(feat) && feat.isStatic) {
                        child.structFeats << feat
                    }
                } else if (child instanceof Classifier) {
                    if (child.isAbstrct()) {
                        if (child.structFeats.contains(feat) && !feat.isStatic) {
                            child.structFeats << feat
                        }
                    }
                }
            }
        }
    }
}
