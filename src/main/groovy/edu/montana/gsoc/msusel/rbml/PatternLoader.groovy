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
package edu.montana.gsoc.msusel.rbml

import com.google.common.collect.ImmutableList
import edu.montana.gsoc.msusel.rbml.io.SpecificationReader
import edu.montana.gsoc.msusel.rbml.model.SPS
import org.yaml.snakeyaml.Yaml

/**
 * A Singleton class used to load patterns contained in this
 * module into an SPS for use by other components of the system
 */
@Singleton
class PatternLoader {

    /**
     * A Simple mapping of the pattern names to their yaml files
     */
    private final Map<String, String> patterns = [
            'Abstract Factory' : '/rbmldef/abstract_factory.yml',
            'Adapter' : '/rbmldef/adapter.yml',
            'Bridge' : '/rbmldef/bridge.yml',
            'Builder' : '/rbmldef/builder.yml',
            'Chain of Responsibility' : '/rbmldef/chain_of_responsibility.yml',
            'Command' : '/rbmldef/command.yml',
            'Decorator' : '/rbmldef/decorator.yml',
            'Facade' : '/rbmldef/facade.yml',
            'Factory Method' : '/rbmldef/factory_method.yml',
            'Flyweight' : '/rbmldef/flyweight.yml',
            'Interpreter' : '/rbmldef/interpreter.yml',
            'Iterator' : '/rbmldef/iterator.yml',
            'Mediator' : '/rbmldef/mediator.yml',
            'Observer' : '/rbmldef/observer.yml',
            'Prototype' : '/rbmldef/prototype.yml',
            'Singleton' : '/rbmldef/singleton.yml',
            'State' : '/rbmldef/state.yml',
            'Strategy' : '/rbmldef/strategy.yml',
            'Template Method' : '/rbmldef/template_method.yml',
            'Visitor' : '/rbmldef/visitor.yml'
    ]

    /**
     * Loads the pattern from the yaml file into an SPS via
     * the Specification reader.
     *
     * @param pattern Pattern whose SPS is requested
     * @return The SPS for the named pattern
     *
     * @throws IllegalArgumentException when the provided pattern name is either
     * null or empty, or when it is not a known pattern name.
     */
    SPS loadPattern(String pattern) {
        if (!pattern)
            throw new IllegalArgumentException("Pattern name cannot be null or empty")
        if (!patterns[pattern])
            throw new IllegalArgumentException("Unknown pattern: $pattern")

        String text = getClass().getResource("/rbmldef/${resource}.yml").readLines().join('\n')
        Yaml yaml = new Yaml()
        SpecificationReader reader = new SpecificationReader()

        def map = yaml.load(text)
        reader.processSPS(map)
        reader.sps
    }

    /**
     * Returns an immutable list of the known names of patterns that may be loaded
     * @return Immutable list of pattern names
     */
    def patterns() {
        ImmutableList.builder().addAll(patterns.keySet()).build()
    }
}
