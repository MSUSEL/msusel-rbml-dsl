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
/**
 * 
 */
package edu.montana.gsoc.msusel.rbml

import edu.montana.gsoc.msusel.rbml.PatternManager
import edu.montana.gsoc.msusel.rbml.factory.RBMLFactoryBuilder
import edu.montana.gsoc.msusel.rbml.model.AltFragment
import edu.montana.gsoc.msusel.rbml.model.Association
import edu.montana.gsoc.msusel.rbml.model.BehavioralFeature
import edu.montana.gsoc.msusel.rbml.model.BreakFragment
import edu.montana.gsoc.msusel.rbml.model.CallMessage
import edu.montana.gsoc.msusel.rbml.model.ClassRole
import edu.montana.gsoc.msusel.rbml.model.Classifier
import edu.montana.gsoc.msusel.rbml.model.Constraint
import edu.montana.gsoc.msusel.rbml.model.CreateMessage
import edu.montana.gsoc.msusel.rbml.model.CriticalFragment
import edu.montana.gsoc.msusel.rbml.model.DestroyMessage
import edu.montana.gsoc.msusel.rbml.model.Gate
import edu.montana.gsoc.msusel.rbml.model.Generalization
import edu.montana.gsoc.msusel.rbml.model.GeneralizationHierarchy
import edu.montana.gsoc.msusel.rbml.model.IPS
import edu.montana.gsoc.msusel.rbml.model.InterfaceRole
import edu.montana.gsoc.msusel.rbml.model.Lifeline
import edu.montana.gsoc.msusel.rbml.model.LoopFragment
import edu.montana.gsoc.msusel.rbml.model.OptFragment
import edu.montana.gsoc.msusel.rbml.model.PPS
import edu.montana.gsoc.msusel.rbml.model.ParFragment
import edu.montana.gsoc.msusel.rbml.model.Pattern
import edu.montana.gsoc.msusel.rbml.model.Realization
import edu.montana.gsoc.msusel.rbml.model.ReturnMessage
import edu.montana.gsoc.msusel.rbml.model.SPS
import edu.montana.gsoc.msusel.rbml.model.SeqFragment
import edu.montana.gsoc.msusel.rbml.model.StrictFragment
import edu.montana.gsoc.msusel.rbml.model.StructuralFeature
import edu.montana.gsoc.msusel.rbml.model.Trace
import edu.montana.gsoc.msusel.rbml.model.UnknownLifeline
import edu.montana.gsoc.msusel.rbml.model.UnknownType
import edu.montana.gsoc.msusel.rbml.model.Usage

import spock.lang.Specification

/**
 * @author Isaac Griffith
 *
 */
class RBMLDslSpec extends Specification {

    def testPattern() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a pattern"
        def instance = builder.pattern "AbstractFactory"

        then: "Instance is an actual object"
        instance != null

        then: "Instance has correct name"
        instance.name == "AbstractFactory"

        then: "Instance is of type Pattern"
        instance instanceof Pattern
    }

    def testPatternWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a pattern and pattern contents"
        def instance = builder.pattern "AbstractFactory", {
            sps { }
            ips { }
            smps { }
            pps { }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance contents are not null"
        instance.sps != null
        instance.ips != null
        instance.pps != null
        instance.smps != null

        then: "Instance has correct name"
        instance.name == "AbstractFactory"

        then: "Instance is of type Pattern"
        instance instanceof Pattern
    }

    def testSps() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a sps"
        def instance = builder.sps()

        then: "Instance is an actual object"
        instance != null

        when: "we provide a sps with a name"
        instance = builder.sps "name"

        then: "instance has correct name"
        instance.getName() == "name"

        then: "Instance is of type Pattern"
        instance instanceof SPS
    }

    def testSpsWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a sps"
        def instance = builder.sps "test", { sps "other" }

        then: "Instance is an actual object"
        instance != null

        when: "we provide a sps with a name"
        instance = builder.sps "name"

        then: "instance has correct name"
        instance.getName() == "name"

        then: "Instance is of type Pattern"
        instance instanceof SPS
    }

    def testRoles() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.roles()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List
    }

    def testRolesWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.roles {
            classifier_role "test", mult: "1..*"
            class_role "test", mult: "1..*"
            interface_role "test", mult: "1..*"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List
    }

    def testGenHierarchy() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a generalization hierarchy"
        def instance = builder.gen_hierarchy "Factory", mult: "1..1"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof GeneralizationHierarchy
    }

    def testGenHierarchyWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a generalization hierarchy"
        def instance = builder.gen_hierarchy {
            classifier_role "root", mult: "1..1", root: true
            class_role "concrete", mult: "1..*"
            classifier_role "abstract", mult: "1..*"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof GeneralizationHierarchy
    }

    def testClassifierRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a classifier"
        def instance = builder.classifier_role "test", mult: "1..*"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Classifier"
        instance instanceof Classifier

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == -1
    }

    def testClassifierRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a behavioral feature"
        def instance = builder.classifier_role "class1", mult: "1..1", {
            behavioral_feature "CreateProduct() : Product", mult: "1..1"
            behavioral_feature "Test(x:Integer)", mult: "1..1"
            structural_feature "data", mult: "1..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Classifier"
        instance instanceof Classifier

        then: "Instance has correct name"
        instance.getName() == "class1"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1

        then: "then verify that the classifier role has the desired components"
        instance.behFeats.size == 2
        instance.structFeats.size == 1
    }

    def testClassRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a classifier"
        def instance = builder.class_role "test", mult: "1..*"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type ClassRole"
        instance instanceof ClassRole

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == -1
    }

    def testClassRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a behavioral feature"
        def instance = builder.class_role "class1", mult: "1..1", {
            behavioral_feature "CreateProduct() : Product", mult: "1..1"
            behavioral_feature "Test(x:Integer)", mult: "1..1"
            structural_feature "data", mult: "1..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type ClassRole"
        instance instanceof ClassRole

        then: "Instance has correct name"
        instance.getName() == "class1"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1

        then: "then verify that the class role has the desired components"
        instance.behFeats.size == 2
        instance.structFeats.size == 1
    }

    def testInterfaceRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a classifier"
        def instance = builder.interface_role "test", mult: "1..*"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type InterfaceRole"
        instance instanceof InterfaceRole

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == -1
    }

    def testInterfaceRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a behavioral feature"
        def instance = builder.interface_role "interface1", mult: "1..1", {
            behavioral_feature "CreateProduct() : Product", mult: "1..1"
            behavioral_feature "Test(x:Integer)", mult: "1..1"
            structural_feature "data", mult: "1..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type InterfaceRole"
        instance instanceof InterfaceRole

        then: "Instance has correct name"
        instance.getName() == "interface1"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1

        then: "then verify that the interface role has the desired components"
        instance.behFeats.size == 2
        instance.structFeats.size == 1
    }

    def testBehavioralFeatureRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a behavioral feature"
        def instance = builder.pattern {
            sps "test", {
                roles {
                    class_role "Product", mult: "1..1"

                    class_role "class1", mult: "1..1", { behavioral_feature "CreateProduct() : Product", mult: "1..1" }
                }
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type BehavioralFeature"
        Classifier holder = PatternManager.findClassifier("class1")
        holder.behFeats.size() > 0
        holder.behFeats[0] != null
        holder.behFeats[0] instanceof BehavioralFeature

        then: "Type instanceof ClassRole with correct name"
        holder.behFeats[0].type instanceof ClassRole
        holder.behFeats[0].type.getName() == "Product"

        then: "Multiplicity is correct"
        holder.behFeats[0].mult.lower == 1
        holder.behFeats[0].mult.upper == 1

        then: "Parameters is empty"
        holder.behFeats[0].params.size() == 0
    }

    def testStructuralFeatureRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a behavioral feature"
        def instance = builder.pattern "test", {
            sps "test", {
                roles {
                    class_role "Product", mult: "1..1"

                    class_role "class1", mult: "1..1", {  structural_feature "CreateProduct : Product", mult: "1..1"  }
                }
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type BehavioralFeature"
        Classifier holder = PatternManager.findClassifier("class1")
        holder.structFeats[0] != null
        holder.structFeats[0] instanceof StructuralFeature

        then: "Instance name is correct"
        holder.structFeats[0].name == "CreateProduct"

        then: "Type instanceof ClassRole with correct name"
        holder.structFeats[0].type instanceof ClassRole
        holder.structFeats[0].type.getName() == "Product"

        then: "Multiplicity is correct"
        holder.structFeats[0].mult.lower == 1
        holder.structFeats[0].mult.upper == 1
    }

    def testRelations() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with relations"
        def instance = builder.relations()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List
    }

    def testRelationsWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with relations"
        def instance = builder.relations()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List
    }

    def testAssociationRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with an association"
        def instance = builder.association_role "test"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Association"
        instance instanceof Association

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1
    }

    def testAssociationRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with an association"
        def instance = builder.association_role "Adaptation", {
            source "Ade", mult: "0..*"
            dest "Adr", mult: "0..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Association"
        instance instanceof Association

        then: "Instance has correct name"
        instance.getName() == "Adaptation"

        then: "Instance has correct multiplicity"
        instance.getMult().getLower() == 1
        instance.getMult().getUpper() == 1

        then: "Source should be correct"
        instance.getSourceName() == "Ade"
        instance.getSourceMult().getLower() == 0
        instance.getSourceMult().getUpper() == -1

        then: "Dest should be correct"
        instance.getDestMult().getLower() == 0
        instance.getDestMult().getUpper() == 1
        instance.getDestName() == "Adr"
    }

    def testAssociationSource() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with an association"
        def instance = builder.association_role "test", {
            source "Dest", role: "Role", mult: "1..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Association"
        instance instanceof Association

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1
    }

    def testAssociationDest() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with an association"
        def instance = builder.association_role "test", {
            dest "Dest", role: "Role", mult: "1..1"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Association"
        instance instanceof Association

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1
    }

    def testUsageRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a usage"
        def instance = builder.usage_role "test", mult: "1..1"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Usage"
        instance instanceof Usage

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1
    }

    def testUsageRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.sps {
            roles {
                class_role "Class1", mult: "1..1"
                class_role "Class2", mult: "1..1"
            }

            relations {
                usage_role "test", mult: "1..1", source: "Class1", dest: "Class2"
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Usage"
        def usage = instance.relations[0]
        usage instanceof Usage

        then: "Instance has correct name"
        usage.getName() == "test"

        then: "Instance has correct multiplicity"
        usage.getMult().lower == 1
        usage.getMult().upper == 1

        then: "Usage has correct source"
        usage.source != null
        usage.source.getName() == "Class1"

        then: "Usage has correct parent"
        usage.dest != null
        usage.dest.getName() == "Class2"
    }

    def testRealizationRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.realization_role "test", mult: "1..1"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof Realization

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1
    }

    def testRealizationRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.sps {
            roles {
                class_role "Class1", mult: "1..1"
                class_role "Class2", mult: "1..1"
            }

            relations {
                realization_role "test", mult: "1..1", parent: "Class1", child: "Class2"
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        def real = instance.relations[0]
        real instanceof Realization

        then: "Realization has correct name"
        real.getName() == "test"

        then: "Realization has correct multiplicity"
        real.getMult().lower == 1
        real.getMult().upper == 1

        then: "Realization has correct child"
        real.child != null
        real.child.getName() == "Class2"

        then: "Realization has correct parent"
        real.parent != null
        real.parent.getName() == "Class1"
    }

    def testGeneralizationRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a generalization"
        def instance = builder.generalization_role "test", mult: "1..*"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Generalization"
        instance instanceof Generalization

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == -1
    }

    def testGeneralizationRoleWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.sps {
            roles {
                class_role "Class1", mult: "1..1"
                class_role "Class2", mult: "1..1"
            }

            relations {
                generalization_role "test", mult: "1..1", parent: "Class1", child: "Class2"
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Generalization"
        def gen = instance.relations[0]
        gen instanceof Generalization

        then: "Generalization has correct name"
        gen.getName() == "test"

        then: "Generalization has correct multiplicity"
        gen.getMult().lower == 1
        gen.getMult().upper == 1

        then: "Generalization has correct child"
        gen.child != null
        gen.child.getName() == "Class2"

        then: "Generalization has correct parent"
        gen.parent != null
        gen.parent.getName() == "Class1"
    }

    def testConstraints() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.constraints()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List
    }
    def testConstraintsWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.constraints {
            context "AbstractAdapter", inv: "self.oclIsTypeOf(Interface) or (self.oclIsTypeOf(Class) and self.isAbstract() = true)"
            context "ConcreteAdapter", inv: "self.isAbstract() = true"
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Pattern"
        instance instanceof List

        then: "Instance size is 2"
        instance.size() == 2
    }
    def testContext() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.context "AbstractAdapter"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Constraint"
        instance instanceof Constraint

        then: "Properties are correct"
        instance.getContext() == "AbstractAdapter"
        instance.getInv() == ""
        instance.getPre() == ""
        instance.getPost() == ""
    }
    def testContextWithInv() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.context "ConcreteAdapter", inv: "self.isAbstract() = true"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Constraint"
        instance instanceof Constraint

        then: "Instance properties are correct"
        instance.getContext() == "ConcreteAdapter"
        instance.getInv() == "self.isAbstract() = true"
        instance.getPre() == ""
        instance.getPost() == ""
    }
    def testContextWithPreAndPost() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with roles"
        def instance = builder.context "Adapter::Request()", pre: "true", post: "Adaptee^SpecificRequest()"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Constraint"
        instance instanceof Constraint

        then: "Instance properties are correct"
        instance.getContext() == "Adapter::Request()"
        instance.getInv() == ""
        instance.getPre() == "true"
        instance.getPost() == "Adaptee^SpecificRequest()"
    }

    def testIps() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a ips"
        def instance = builder.ips()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type IPS"
        instance instanceof IPS
    }

    def testIpsWithContents() {
        PatternManager.reset()
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a ips"
        def instance = builder.ips "CreateProductInteraction", mult: "1..1", {
            roles {
                lifeline "fact:ConcreteFactory"
                lifeline "prod:ConcreteProduct"
            }

            trace {
                call_role "CreateProduct", dest: "fact"
                create_role dest: "prod", source: "prod"
                call_return "prod", source: "prod"
            }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type IPS"
        instance instanceof IPS
        
        then: "Instance multiplicity is correct"
        instance.getMult().getLower() == 1
        instance.getMult().getUpper() == 1
        
        then: "Instance has two lifelines"
        instance.getLines().size() == 2
        
        then: "Instance trace has three fragments"
        instance.getTrace().getFragments().size() == 3
    }

    def testLifelineRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.lifeline "test:ConcreteFactory", mult: "1..1"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof Lifeline

        then: "Instance has correct name"
        instance.getName() == "test"

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1

        then: "Instance type is Unknown with name ConcreteFactory"
        instance.type instanceof UnknownType
        instance.type.name == "ConcreteFactory"
    }

    def testAnonymousLifelineRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.lifeline ":ConcreteFactory", mult: "1..1"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof Lifeline

        then: "Instance has correct name"
        instance.getName() == ""

        then: "Instance has correct multiplicity"
        instance.getMult().lower == 1
        instance.getMult().upper == 1

        then: "Instance type is Unknown with name ConcreteFactory"
        instance.type instanceof UnknownType
        instance.type.name == "ConcreteFactory"
    }

    def testCallRole() {
        PatternManager.reset()
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a Call Role"
        def instance = builder.call_role "test", dest: "x"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type CallMessage"
        instance instanceof CallMessage

        then: "Instance has correct properties"
        instance.getName() == "test"
        //instance.getMethod() instanceof UnknownBehavioralFeature
        instance.getSource() instanceof Gate
        instance.getDest() instanceof UnknownLifeline
    }

    def testCreateRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a Create Role"
        def instance = builder.create_role "test", dest: "x"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof CreateMessage

        then: "Instance has correct name"
        instance.getName() == "test"
        instance.getDest() instanceof UnknownLifeline
        instance.getSource() instanceof Gate
    }

    def testCallReturnRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a Call Return"
        def instance = builder.call_return "test", source: "x"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof ReturnMessage

        then: "Instance has correct name"
        instance.getName() == "test"
        instance.getDest() instanceof Gate
        instance.getSource() instanceof UnknownLifeline
    }

    def testDestroyRole() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.destroy_role "test", dest: "y"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof DestroyMessage

        then: "Instance has correct name"
        instance.getName() == "test"
        instance.getSource() instanceof Gate
        instance.getDest() instanceof UnknownLifeline
    }

    def testAltFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.alt()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof AltFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().isEmpty()
    }

    def testAltFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.alt {
            trace "[guard]", { }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof AltFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().size() == 1
    }

    def testOptFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.opt()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof OptFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().isEmpty()
    }

    def testOptFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.opt {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof OptFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().size() == 1
    }

    def testLoopFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.loop min: 0, max: 1, {}

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof LoopFragment

        then: "Instance has correct name"
        instance.getMin() == 0
        instance.getMax() == 1
        instance.getTrace() == null

        when: "We provide it with only a guard"
        instance = builder.loop {}

        then: "min = 0, max = infinite, and guard is not empty"
        instance.getMin() == 0
        instance.getMax() == Integer.MAX_VALUE

        when: "We provide it with only a min value"
        instance = builder.loop min: 5, {}

        then: "min = 5, max = infinite, and guard is empty"
        instance.getMin() == 5
        instance.getMax() == Integer.MAX_VALUE

        when: "We provide it with only a min value"
        instance = builder.loop max: 5, {}

        then: "min = 5, max = infinite, and guard is empty"
        instance.getMin() == 0
        instance.getMax() == 5
    }

    def testLoopFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.loop min: 0, max: 1, {
            trace "[guard]", { }
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof LoopFragment

        then: "Instance has correct name"
        instance.getTrace() != null
    }

    def testBreakFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.brk {}

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof BreakFragment

        then: "Instance has correct name"
        instance.getTrace() == null
    }

    def testBreakFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.brk {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof BreakFragment

        then: "Instance has correct name"
        instance.getTrace() != null
    }

    def testParFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.par "[guard]"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof ParFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().isEmpty()
    }

    def testParFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a par fragment"
        def instance = builder.par {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type ParFragment"
        instance instanceof ParFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().size() == 1
    }

    def testStrictFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.strict "[guard]"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof StrictFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().isEmpty()
    }

    def testStrictFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a strict fragment"
        def instance = builder.strict {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type StrictFragment"
        instance instanceof StrictFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().size() == 1
    }

    def testSeqFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.seq "[guard]"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof SeqFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().isEmpty()
    }

    def testSeqFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a seq fragment"
        def instance = builder.seq {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type ParFragment"
        instance instanceof SeqFragment

        then: "Instance has correct name"
        instance.getGuardedTraces().size() == 1
    }

    def testCriticalFragment() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a realization"
        def instance = builder.critical "[guard]"

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Realization"
        instance instanceof CriticalFragment

        then: "Instance has correct name"
        instance.getTrace() == null
    }

    def testCriticalFragmentWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a critical fragment"
        def instance = builder.critical {
            trace "[guard]", {}
        }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type CriticalFragment"
        instance instanceof CriticalFragment

        then: "Instance has correct name"
        instance.getTrace() != null
    }

    def testTrace() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a trace"
        def instance = builder.trace()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Trace"
        instance instanceof Trace

        then: "Instance has no associated fragments"
        instance.getFragments().isEmpty()
    }

    def testTraceWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a trace"
        def instance = builder.trace {
                call_role "CreateProduct", dest: "fact"
                create_role dest: "prod", source: "prod"
                call_return "prod", source: "prod"
            }

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type Trace"
        instance instanceof Trace

        then: "Instance has no associated fragments"
        instance.getFragments().size() == 3
    }

    def testPps() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a smps"
        def instance = builder.pps()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type PPS"
        instance instanceof PPS
    }
    
    // TODO
    def testPpsWithContents() {
        given: "An RBMLFactoryBuilder instance"
        def builder = new RBMLFactoryBuilder()

        when: "We provide it with a smps"
        def instance = builder.pps()

        then: "Instance is an actual object"
        instance != null

        then: "Instance is of type PPS"
        instance instanceof PPS
    }

    /*
     def testSmps() {
     given: "An RBMLFactoryBuilder instance"
     def builder = new RBMLFactoryBuilder()
     when: "We provide it with a smps"
     def instance = builder.smps()
     then: "Instance is an actual object"
     instance != null
     //        then: "Instance has correct name"
     //        instance.name == "AbstractFactory"
     then: "Instance is of type Pattern"
     instance instanceof SMPS
     }
     def testSmpsWithContents() {
     // TODO
     }
     def testTriggerRole() {
     // TODO
     }
     def testTriggerRoleWithContents() {
     // TODO
     }
     def testTransitionRole() {
     // TODO
     }
     def testTransitionRoleWithContents() {
     // TODO
     }
     def testStateRole() {
     // TODO
     }
     def testStateRoleWithContents() {
     // TODO
     }
     */
}
