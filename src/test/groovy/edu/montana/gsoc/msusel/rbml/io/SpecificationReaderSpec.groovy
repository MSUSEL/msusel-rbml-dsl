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
package edu.montana.gsoc.msusel.rbml.io

import edu.montana.gsoc.msusel.rbml.model.Aggregation
import edu.montana.gsoc.msusel.rbml.model.Association
import edu.montana.gsoc.msusel.rbml.model.ClassRole
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.Composition
import edu.montana.gsoc.msusel.rbml.model.Generalization
import edu.montana.gsoc.msusel.rbml.model.GeneralizationHierarchy
import edu.montana.gsoc.msusel.rbml.model.InterfaceRole
import edu.montana.gsoc.msusel.rbml.model.Multiplicity
import edu.montana.gsoc.msusel.rbml.model.Realization
import edu.montana.gsoc.msusel.rbml.model.SPS
import edu.montana.gsoc.msusel.rbml.model.Usage
import org.yaml.snakeyaml.Yaml
import spock.lang.Specification


class SpecificationReaderSpec extends Specification {

    def testProcessSPS() {

    }

    def testProcessRoles() {

    }

    def "processing a class"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''\
name: Z
''')
            ClassRole role = reader.processClass(map)

        then:
            role != null
            role.name == "Z"
            role.mult == Multiplicity.fromString("1..1")
    }

    def "processing a null class"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            def spec = null

        when:
            reader.processClass(spec)

        then:
            thrown IllegalArgumentException
    }

    def "processing a class with given multiplicity"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''\
---
name: Z
mult: 1..9
...
''')
            ClassRole role = reader.processClass(map)

        then:
            role.name == "Z"
            role.mult == Multiplicity.fromString("1..9")
    }

    def "processing a classifier"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''\
name: Z
''')
            Classifier role = reader.processClassifier(map)

        then:
            role != null
            role.name == "Z"
            role.mult == Multiplicity.fromString("1..1")
    }

    def "processing a null classifier"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            def spec = null

        when:
            reader.processClassifier(spec)

        then:
            thrown IllegalArgumentException
    }

    def "processing a classifier with given multiplicity"() {
        given:
        SpecificationReader reader = new SpecificationReader()
        Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''\
---
name: Z
mult: 1..9
...
''')
            Classifier role = reader.processClassifier(map)

        then:
            role.name == "Z"
            role.mult == Multiplicity.fromString("1..9")
    }

    def "processing an interface"() {
        given:
        SpecificationReader reader = new SpecificationReader()
        Yaml yaml = new Yaml()

        when:
        def map = yaml.load('''\
name: Z
''')
        InterfaceRole role = reader.processInterface(map)

        then:
        role != null
        role.name == "Z"
        role.mult == Multiplicity.fromString("1..1")
    }

    def "processing a null interface"() {
        given:
        SpecificationReader reader = new SpecificationReader()
        def spec = null

        when:
        reader.processInterface(spec)

        then:
        thrown IllegalArgumentException
    }

    def "processing a interface with given multiplicity"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''\
---
name: Z
mult: 1..9
...
''')
            InterfaceRole role = reader.processInterface(map)

        then:
            role.name == "Z"
            role.mult == Multiplicity.fromString("1..9")
    }

    def "processing roles with 3 different types"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''
                - Class:
                    name: X
                - Interface:
                    name: Y
                - Classifier:
                    name: Z
                '''.stripIndent(16))
            reader.processRoles(map)

        then:
            reader.roles.size() == 3

    }

    def "processing roles with 3 similar types"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load('''
                - Class:
                    name: X
                - Class:
                    name: Y
                - Class:
                    name: Z
                '''.stripIndent(16))
            reader.processRoles(map)

        then:
            reader.roles.size() == 3
    }

    def "processing null roles"() {
        given:
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processRoles(null)

        then:
            reader.roles.isEmpty()
    }

    def "processing a generalization hierarchy"() {
        given:
            def spec = '''
                name: "x"
                mult: "1..1"
                Classifier:
                    name: X
                children:
                    - Class:
                        name: Y
                    - Classifier:
                        name: Z
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processGenHierarchy(map)

        then:
            reader.roles.size() == 3
            reader.ghs["x"] instanceof GeneralizationHierarchy
            reader.ghs["x"] != null
            ((GeneralizationHierarchy) reader.ghs["x"]).children.size() == 2
            ((GeneralizationHierarchy) reader.ghs["x"]).root != null
    }

    def "processing a null gen hierarchy"() {
        given:
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processGenHierarchy(null)

        then:
            reader.roles.size() == 0
    }

    def "processing an empty gen hierarchy"() {
        given:
            SpecificationReader reader = new SpecificationReader()
            def spec = ""

        when:
            reader.processGenHierarchy(spec)

        then:
            reader.roles.size() == 0
    }

    def "processing a gen hierarchy with no main defined"() {
        given:
            def spec = '''
                name: "x"
                mult: "1..1"
                children:
                    - Class:
                        name: Y
                    - Classifier:
                        name: Z
                '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processGenHierarchy(map)

        then:
            thrown MissingContentException
    }

    def "processing a gen hierarchy with no children"() {
        given:
            def spec = '''
                name: "x"
                mult: "1..1"
                Classifier:
                    name: X
                '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processGenHierarchy(map)

        then:
            thrown MissingContentException
    }

    def "processing features"() {
        given:
            def spec = '''\
                b1:
                    name: Attach
                    type: Other
                    params: [obsv: Other]
                    mult: 1..*
                s1:
                    name: subjectState
                    type: SubStateType
                    mult: 1..*
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()
            Classifier r = Classifier.builder().name("Test").create()

        when:
            def map = yaml.load(spec)
            reader.processFeatures(r, map)

        then:
            r.behFeats.size() == 1
            r.structFeats.size() == 1
            reader.roles.size() == 0
    }

    def "processing features with empty structural feature"() {
        given:
            def spec = '''\
                b1:
                    name: Attach
                    type: Other
                    params: [obsv: Other]
                    mult: 1..*
                s1: {}
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()
            Classifier r = Classifier.builder().name("Test").create()

        when:
            def map = yaml.load(spec)
            reader.processFeatures(r, map)

        then:
            r.behFeats.size() == 1
            r.structFeats.size() == 0
            reader.roles.size() == 0
    }

    def "processing features with empty behavioral feature"() {
        given:
            def spec = '''\
                b1: {}
                s1:
                    name: subjectState
                    type: SubStateType
                    mult: 1..*
            '''
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()
            Classifier r = Classifier.builder().name("Test").create()

        when:
            def map = yaml.load(spec)
            reader.processFeatures(r, map)

        then:
            r.behFeats.size() == 0
            r.structFeats.size() == 1
            reader.roles.size() == 0
    }

    def "processing features with null map"() {
        given:
            def map = null
            Classifier role = Classifier.builder().name("Test").create()
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processFeatures(role, map)

        then:
            role.structFeats.isEmpty()
            role.behFeats.isEmpty()
    }

    def "processing features with null role"() {
        given:
            def spec = """
                s1:
                    name: Test
            """.stripIndent(16)
            Classifier role = null
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load(spec)
            reader.processFeatures(role, map)

        then:
            thrown IllegalArgumentException
    }

    def "processing behavioral features"() {
        given:
            def spec = '''
                name: Attach
                type: Other
                params: [obsv: Other]
                mult: 1..*
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            def feat = reader.processBehavioralFeature(map)

        then:
            feat.name == "Attach"
            feat.type == reader.roles['Other']
            feat.params.size() == 1
            feat.mult == Multiplicity.fromString("1..*")
    }

    def "processing an empty behavioral feature"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            def feat = reader.processBehavioralFeature(map)

        then:
            feat == null
    }

    def "processing a null behavioral feature"() {
        given:
            SpecificationReader reader = new SpecificationReader()

        when:
            def feat = reader.processBehavioralFeature(null)

        then:
            feat == null
    }

    def "processing structural features"() {
        given:
            def spec = '''
                name: subjectState
                type: SubStateType
                mult: 1..*
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            def feat = reader.processStructuralFeature(map)

        then:
            feat.name == 'subjectState'
            feat.type == reader.roles['SubStateType']
            feat.mult == Multiplicity.fromString('1..*')
    }

    def "processing an empty structural feature"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            def feat = reader.processStructuralFeature(map)

        then:
            feat == null
    }

    def "processing a null structural feature"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            def feat = reader.processStructuralFeature(map)

        then:
            feat == null
    }

    def "processing a generalization"() {
        given:
            def spec = '''\
                name: ObserverGeneralization
                mult: 0..1
                child: Observer
                parent: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processGeneralization(map)
            def rel = reader.relations["ObserverGeneralization"]

        then:
            rel instanceof Generalization
            rel.child == reader.roles['Observer']
            rel.parent == reader.roles['Observer']
            rel.mult == Multiplicity.fromString("0..1")
            reader.roles.size() == 0
    }

    def "processing an empty generalization"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            def rel = reader.processGeneralization(map)

        then:
            rel == null
            reader.roles.size() == 0
    }

    def "processing a null generalization"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            def rel = reader.processGeneralization(map)

        then:
            rel == null
            reader.roles.size() == 0
    }

    def "processing a realization"() {
        given:
            def spec = '''\
                name: ObserverRealization
                mult: 0..1
                child: Observer
                parent: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processRealization(map)
            def rel = reader.relations["ObserverRealization"]

        then:
            rel instanceof Realization
            rel.child == reader.roles['Observer']
            rel.parent == reader.roles['Observer']
            rel.mult == Multiplicity.fromString("0..1")
            reader.roles.size() == 0
    }


    def "processing an empty realization"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            def rel = reader.processRealization(map)

        then:
            rel == null
            reader.roles.size() == 0
    }

    def "processing a null realization"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            def rel = reader.processGeneralization(map)

        then:
            rel == null
            reader.roles.size() == 0
    }

    def "processing a association"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Sub
                    mult: 1..*
                    type: Subject
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAssociation(map)
            def rel = reader.relations["Observes"]

        then:
            rel instanceof Association
            rel.source == reader.roles['Subject']
            rel.sourceMult == Multiplicity.fromString("1..*")
            rel.sourceName == "Sub"
            rel.dest == reader.roles['Observer']
            rel.destMult == Multiplicity.fromString("1..*")
            rel.destName == "Obs"
            rel.name == "Observes"
            rel.mult == Multiplicity.fromString("1..1")
            reader.roles.size() == 0
    }

    def "processing an association missing the source"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAssociation(map)

        then:
            thrown MissingContentException
    }

    def "processing an association missing the dest"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAssociation(map)

        then:
            thrown MissingContentException
    }

    def "processing an empty association"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processAssociation(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a null association"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processAssociation(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a composition"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Sub
                    mult: 1..*
                    type: Subject
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processComposition(map)
            def rel = reader.relations["Observes"]

        then:
            rel instanceof Composition
            rel.source == reader.roles['Subject']
            rel.sourceMult == Multiplicity.fromString("1..*")
            rel.sourceName == "Sub"
            rel.dest == reader.roles['Observer']
            rel.destMult == Multiplicity.fromString("1..*")
            rel.destName == "Obs"
            rel.name == "Observes"
            rel.mult == Multiplicity.fromString("1..1")
            reader.roles.size() == 0
    }


    def "processing a composition missing the source"() {
        given:
        def spec = '''\
                name: Observes
                mult: 1..1
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
        Yaml yaml = new Yaml()
        SpecificationReader reader = new SpecificationReader()

        when:
        def map = yaml.load(spec)
        reader.processComposition(map)

        then:
        thrown MissingContentException
    }

    def "processing a composition missing the dest"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processComposition(map)

        then:
            thrown MissingContentException
    }

    def "processing an empty composition"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processComposition(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a null composition"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processComposition(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a aggregation"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Sub
                    mult: 1..*
                    type: Subject
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAggregation(map)
            def rel = reader.relations["Observes"]

        then:
            rel instanceof Aggregation
            rel.source == reader.roles['Subject']
            rel.sourceMult == Multiplicity.fromString("1..*")
            rel.sourceName == "Sub"
            rel.dest == reader.roles['Observer']
            rel.destMult == Multiplicity.fromString("1..*")
            rel.destName == "Obs"
            rel.name == "Observes"
            rel.mult == Multiplicity.fromString("1..1")
            reader.roles.size() == 0
    }

    def "processing an aggregation missing the source"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                dest:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAggregation(map)

        then:
            thrown MissingContentException
    }

    def "processing an aggregation missing the dest"() {
        given:
            def spec = '''\
                name: Observes
                mult: 1..1
                source:
                    name: Obs
                    mult: 1..*
                    type: Observer
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processAggregation(map)

        then:
            thrown MissingContentException
    }

    def "processing an empty aggregation"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processAggregation(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a null aggregation"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processAggregation(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a usage"() {
        given:
            def spec = '''\
                name: Uses
                mult: 1..1
                source: Observer
                dest: Subject
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processUsage(map)
            def rel = reader.relations['Uses']

        then:
            rel instanceof Usage
            rel.name == "Uses"
            rel.mult == Multiplicity.fromString("1..1")
            reader.relations.size() == 1
            reader.roles.size() == 0
    }

    def "processing an empty usage"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processUsage(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a null usage"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processUsage(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a usage without source"() {
        given:
            def spec = '''\
                name: Uses
                mult: 1..1
                source: Observer
            '''
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processUsage(map)

        then:
            thrown MissingContentException
    }

    def "processing at least one"() {
        given:
            def spec = '''\
                name: Uses
                mult: 1..1
                dest: Observer
            '''
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processUsage(map)

        then:
            thrown MissingContentException
    }

    def "processing relations"() {
        given:
            def spec = '''\
                - Association:
                    name: Observes
                    mult: 1..1
                    source:
                        name: Sub
                        mult: 1..*
                        type: Subject
                    dest:
                        name: Obs
                        mult: 1..*
                        type: Observer
                - Generalization:
                    name: ObserverGeneralization
                    mult: 0..1
                    child: Observer
                    parent: Observer
                - Realization:
                    name: ObserverRealization
                    mult: 0..1
                    child: Observer
                    parent: Observer
                - Usage:
                    name: Uses
                    mult: 1..1
                    source: Observer
                    dest: Subject
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processRelations(map)

        then:
            reader.relations.size() == 4
            reader.roles.size() == 0
    }

    def "processing empty relations"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processRelations(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing null relations"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processRelations(map)

        then:
            reader.relations.size() == 0
            reader.roles.size() == 0
    }

    def "processing a SPS"() {
        given:
            def spec = '''\
                SPS:
                    name: Observer
                    roles:
                        - Class:
                            name: Subject
                        - Interface:
                            name: Observer
                        - Classifier:
                            name: Other
                    relations:
                        - Association:
                            name: Observes
                            mult: 1..1
                            source:
                                name: Sub
                                mult: 1..*
                                type: Subject
                            dest:
                                name: Obs
                                mult: 1..*
                                type: Observer
                        - Generalization:
                            name: ObserverGeneralization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Realization:
                            name: ObserverRealization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Usage:
                            name: Uses
                            mult: 1..1
                            source: Observer
                            dest: Subject
            '''.stripIndent(16)
            SpecificationReader reader = new SpecificationReader()
            Yaml yaml = new Yaml()

        when:
            def map = yaml.load(spec)
            reader.processSPS(map)

        then:
            reader.roles.size() == 3
            reader.relations.size() == 4
            reader.sps != null
            reader.sps.name == "Observer"
            reader.sps.classifiers.size() == 3
            reader.sps.relations.size() == 4
    }

    def "processing a null SPS"() {
        given:
            def map = null
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processSPS(map)

        then:
            reader.sps == null
            reader.roles.size() == 0
            reader.relations.size() == 0
    }

    def "processing an empty SPS"() {
        given:
            def map = [:]
            SpecificationReader reader = new SpecificationReader()

        when:
            reader.processSPS(map)

        then:
            reader.sps == null
            reader.roles.size() == 0
            reader.relations.size() == 0
    }

    def "processing an SPS missing roles"() {
        given:
            def spec = '''\
                SPS:
                    name: Observer
                    relations:
                        - Association:
                            name: Observes
                            mult: 1..1
                            source:
                                name: Sub
                                mult: 1..*
                                type: Subject
                            dest:
                                name: Obs
                                mult: 1..*
                                type: Observer
                        - Generalization:
                            name: ObserverGeneralization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Realization:
                            name: ObserverRealization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Usage:
                            name: Uses
                            mult: 1..1
                            source: Observer
                            dest: Subject
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processSPS(map)

        then:
            thrown MissingContentException
    }

    def "processing an SPS missing a name"() {
        given:
            def spec = '''\
                SPS:
                    roles:
                        - Classifier:
                            name: X
                    relations:
                        - Association:
                            name: Observes
                            mult: 1..1
                            source:
                                name: Sub
                                mult: 1..*
                                type: Subject
                            dest:
                                name: Obs
                                mult: 1..*
                                type: Observer
                        - Generalization:
                            name: ObserverGeneralization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Realization:
                            name: ObserverRealization
                            mult: 0..1
                            child: Observer
                            parent: Observer
                        - Usage:
                            name: Uses
                            mult: 1..1
                            source: Observer
                            dest: Subject
            '''.stripIndent(16)
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(spec)
            reader.processSPS(map)

        then:
            thrown MissingContentException
    }

    def "process actual patterns"(String resource, String name, int ghs, int cls, int rels) {
        given:
        String text = getClass().getResource("/rbmldef/${resource}.yml").readLines().join('\n')
        Yaml yaml = new Yaml()
        SpecificationReader reader = new SpecificationReader()

        when:
        def map = yaml.load(text)
        reader.processSPS(map)
        SPS sps = reader.sps

        then:
        sps.name == name
        sps.genHierarchies.size() == ghs
        sps.classifiers.size() == cls
        sps.relations.size() == rels

        where:
        resource                  | name                      | ghs | cls | rels
        'abstract_factory'        | 'Abstract Factory'        | 2   | 7   | 5
        'adapter'                 | 'Adapter'                 | 1   | 5   | 2
        'bridge'                  | 'Bridge'                  | 2   | 6   | 1
        'builder'                 | 'Builder'                 | 2   | 7   | 2
        'chain_of_responsibility' | 'Chain of Responsibility' | 1   | 4   | 2
        'command'                 | 'Command'                 | 1   | 6   | 4
        'composite'               | 'Composite'               | 1   | 5   | 2
        'decorator'               | 'Decorator'               | 2   | 6   | 3
        'facade'                  | 'Facade'                  | 0   | 2   | 1
        'factory_method'          | 'Factory Method'          | 2   | 6   | 1
        'flyweight'               | 'Flyweight'               | 1   | 7   | 4
        'interpreter'             | 'Interpreter'             | 1   | 7   | 2
        'iterator'                | 'Iterator'                | 2   | 7   | 3
        'mediator'                | 'Mediator'                | 2   | 6   | 2
        'memento'                 | 'Memento'                 | 0   | 3   | 2
        'observer'                | 'Observer'                | 2   | 6   | 2
        'prototype'               | 'Prototype'               | 1   | 4   | 1
        'singleton'               | 'Singleton'               | 1   | 3   | 0
        'strategy'                | 'Strategy'                | 1   | 4   | 1
        'state'                   | 'State'                   | 1   | 4   | 1
        'template_method'         | 'Template Method'         | 1   | 3   | 0
        'visitor'                 | 'Visitor'                 | 2   | 8   | 3
    }

    def "process decorator pattern"() {
        given:
            String text = getClass().getResource('/rbmldef/decorator.yml').readLines().join('\n')
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(text)
            reader.processSPS(map)

        then:
            SPS sps = reader.sps
            sps.name == "Decorator"
            sps.genHierarchies.size() == 2
            sps.classifiers.size() == 6
            sps.relations.size() == 3
    }

    def "process state pattern"() {
        given:
            String text = getClass().getResource('/rbmldef/state.yml').readLines().join('\n')
            Yaml yaml = new Yaml()
            SpecificationReader reader = new SpecificationReader()

        when:
            def map = yaml.load(text)
            reader.processSPS(map)

        then:
            SPS sps = reader.sps
            sps.name == "State"
            sps.genHierarchies.size() == 1
            sps.classifiers.size() == 4
            sps.relations.size() == 1
            ((Classifier) sps.classifiers.find { it.name == "Context" }).structFeats.size() == 1
            ((Classifier) sps.classifiers.find { it.name == "Context" }).behFeats.size() == 1
            ((Classifier) sps.handleGenHierarchy(sps.getGenHierarchies().find { it.name == "State" }, "State").first()).behFeats.size() == 1
    }

    def "process empty pattern"() {

    }

    def "process null pattern"() {

    }

    def "process pattern missing name"() {

    }

    def "process pattern missing sps"() {

    }
}