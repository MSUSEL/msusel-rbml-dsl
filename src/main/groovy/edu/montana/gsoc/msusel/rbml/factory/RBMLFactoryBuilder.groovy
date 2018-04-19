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

/**
 * @author Isaac Griffith
 * @version 1.2.0
 */
class RBMLFactoryBuilder extends FactoryBuilderSupport {

    /**
     * @param init
     */
    public RBMLFactoryBuilder(boolean init = true) {
        super(init)
    }

    /**
     * @return
     */
    def registerObjectFactories() {
        registerFactory("pattern", new PatternFactory())
        registerFactory("sps", new SPSFactory())
        registerFactory("roles", new RolesFactory())
        registerFactory("gen_hierarchy", new GenHierarchyFactory())
        registerFactory("classifier_role", new ClassifierRoleFactory())
        registerFactory("behavioral_feature", new BehavioralFeatureRoleFactory())
        registerFactory("structural_feature", new StructuralFeatureRoleFactory())
        registerFactory("class_role", new ClassRoleFactory())
        registerFactory("interface_role", new InterfaceRoleFactory())
        registerFactory("relations", new RelationsFactory())
        registerFactory("association_role", new AssociationRoleFactory())
        registerFactory("aggregation_role", new AggregationRoleFactory())
        registerFactory("composition_role", new CompositionRoleFactory())
        registerFactory("usage_role", new UsageRoleFactory())
        registerFactory("create_role", new CreateRoleFactory())
        registerFactory("realization_role", new RealizationRoleFactory())
        registerFactory("generalization_role", new GeneralizationRoleFactory())
        registerFactory("source", new SourceFactory())
        registerFactory("dest", new DestFactory())
        registerFactory("ips", new IPSFactory())
        registerFactory("lifeline", new LifelineRoleFactory())
        registerFactory("call_role", new CallRoleFactory())
        registerFactory("create_msg", new CreateMessageFactory())
        registerFactory("call_return", new CallReturnRoleFactory())
        registerFactory("destroy_role", new DestroyRoleFactory())
        registerFactory("async_message", new AsyncMessageRoleFactory())
        registerFactory("at_least_one", new AtLeastOneFactory())
        registerFactory("alt", new AltFragmentFactory())
        registerFactory("opt", new OptFragmentFactory())
        registerFactory("loop", new LoopFragmentFactory())
        registerFactory("brk", new BreakFragmentFactory())
        registerFactory("par", new ParFragmentFactory())
        registerFactory("strict", new StrictFragmentFactory())
        registerFactory("seq", new SeqFragmentFactory())
        registerFactory("critical", new CriticalFragmentFactory())
        registerFactory("trace", new TraceFactory())
        registerFactory("constraints", new ConstraintsFactory())
        registerFactory("context", new ContextFactory())
        registerFactory("pps", new PPSFactory())
        registerFactory("smps", new SMPSFactory())
        
        
    }

}
