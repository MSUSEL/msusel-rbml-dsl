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
/**
 * 
 */
package edu.montana.gsoc.msusel.rbml

/**
 * @author Isaac Griffith
 *
 */
class LogBuilder extends BuilderSupport {

    def indent = 0

    /**
     * {@inheritDoc}
     */
    @Override
    def createNode(name) {
        indent.times {print "  "}
        println "createNode(${name})"
        indent++
        return name
    }

    /**
     * {@inheritDoc}
     */
    @Override
    def createNode(name, value) {
        indent.times {print "  "}
        println "createNode(${name}, ${value})"
        indent++
        return name
    }

    /**
     * {@inheritDoc}
     */
    @Override
    def createNode(name, Map attributes) {
        indent.times {print "  "}
        println "createNode(${name}, ${attributes})"
        indent++
        return name
    }

    /**
     * {@inheritDoc}
     */
    @Override
    def createNode(name, Map attributes, value) {
        indent.times {print "  "}
        println "createNode(${name}, ${attributes}, ${value})"
        indent++
        return name
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setParent(parent, child) {
        indent.times {print "  "}
        println "setParent(${parent}, ${child})"
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void nodeCompleted(parent, node) {
        indent--
        indent.times {print "  "}
        println "nodeCompleted(${parent}, ${node})"
    }
    
    public static void main(String[] args) {
        
        def builder = new LogBuilder()
        
        /**
         * C# (pronounced "see sharp") is a multi-paradigm programming language encompassing imperative, declarative, functional, generic,
         * object-oriented (class-based), and component-oriented programming disciplines. It was developed by Microsoft within the .NET initiative and
         * later approved as a standard by Ecma (ECMA-334) and ISO (ISO/IEC 23270). C# is one of the programming languages designed for the Common
         * Language Infrastructure.
         */
        builder.model("csharp-tools", partof: "csharp") {
            name "C#"
            
            entities {
                
                // A delegate is a reference type in C# that is used for implementing the delegate pattern in event handling.
                product_part "_fI1okJsIEd-BEeIfUepsAQ", name: "Delegate" {
                    isa "_HpFrIOtNEd-XsNJ8L7AOPA", from: "root.qm"
                    part of: "_AxdPYNRqEeabqMSyNfnMRQ", from: "root.qm#_0fAiULm8Ed-rZPMnS6wt_g"
                }
                
                usecase "asldkfjasl;dkf", name: "something" {
                    isa "a;odkfjasldkfj"
                    part of: "a;lkdjfal;sdkfj"
                }
        
                stakeholder "a;ldkfjasl;kdfj", name: "Manager" {
                    isa "alkdjfal;skdfj"
                    part of: "alkdjfa;sldkfj"
                }
            }
        
            factors {
                // A classes' interface and implementation are consistent if the implementation fits to the interface and the interface allows for the intended implementation.
                factor "id", name: "Interface and Implementation Consistency" {
                    characterizes entity: "_Ve0-kJP7Ed-a_JX6fZrQQA", from: "O-O"
                    influences factor: "_hCU1oeP0Ed6mXujsf-O9qQ", from: "root", justification: """Time Behavior is positively influenced, because inconsistent interfaces and implementations could lead to the usage of inefficient standard algorithms, e.g. for object comparison."""
                    influences factor: "_hCU1k-P0Ed6mXujsf-O9qQ", from: "root", effect: "Positive", rank: 1
                    influences factor: "_hCU1weP0Ed6mXujsf-O9qQ", from: "root", effect: "Positive", rank: 1
                    refines "_U2ZzQoeIEeCvOcxPw9PG9g", from: "root"
                }
            }
        
            measures {
                // "This rule looks for types that have an empty finalizer (a.k.a. destructor in C# or Finalize method). Finalizers that simply set fields to null are considered to be empty because this does not help the garbage collection. You should remove the empty finalizer to alleviate pressure on the garbage collector and finalizer thread." [1]
                measure "_xqEWAJjBEd-eGp1I3fEuaQ", name: "Empty", type: "Findings" {
                    quantifies factor: "_9urmgIXWEeCT8pJoQsn4HQ", from: "root", max_points: "100" {
                        function "Decreasing", ub: 1.0, lb: 0.0
                        normalized by: "LOC", range: "Method"
                    }
                    refines measure: "_9L0KAYXWEeCT8pJoQsn4HQ", from: "root"
                }
            }
        
            instruments {
                metric_instrument "adfijasdlkfeh" {
                    determines measure: "_PLzVELm9Ed-rZPMnS6wt_g", from: "root.qm" 
                    using tool: "_ZrDrAR-_EeC3ve5rt4NpvA"
                    yielding metric: "Overly Long File"
                }
                rule_instrument "adfijasdlkfeh" {
                    determines measure: "_PLzVELm9Ed-rZPMnS6wt_g", from: "root.qm" 
                    using tool: "_ZrDrAR-_EeC3ve5rt4NpvA"
                    yielding rule: "Overly Long File"
                }
                manual_instrument "XXXX" {
                    determines measure: "XXXXX", from: "root"
                    using file: "filename.csv"
                    yielding metric: "Some metric"
                }
            }
        
            tools {
                tool "C# Basic metrics", desc: "Determines basic metrics for normalization, like LoC, #Methods, etc." {
                    annotation key: "Block-Id", value: "edu.tum.cs.conqat.quamoco.model.QCSNormalizationMeasuresIL"
                }
                tool "C# Clone Detection", desc: "C# Clone Detection in ConQAT." {
                    annotation key: "Block-Id", value: "edu.tum.cs.conqat.quamoco.model.QCSCloneDetection"
                }
                tool name: "fxcop", desc: "FxCop is an application that analyzes managed code assemblies (code that targets the .NET Framework common language runtime) and reports information about the assemblies, such as possible design, localization, performance, and security improvements. Many of the issues concern violations of the programming and design rules set forth in the Design Guidelines, which are the Microsoft guidelines for writing robust and easily maintainable code by using the .NET Framework."
                tool name: "stylecop", desc: "StyleCop analyzes C# source code to enforce a set of style and consistency rules. It can be run from inside of Visual Studio or integrated into an MSBuild project. StyleCop has also been integrated into many third-party development tools.&#x9;"
                tool name: "csharpsquid", title: "SonarQube C# Plugin Rules"
                tool name: "common-cs", title: "C# Common Rules"
            }
        }
    }
}
