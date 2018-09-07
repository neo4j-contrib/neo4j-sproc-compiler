/*
 * Copyright 2016-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.biville.florent.sproccompiler.export;

import net.biville.florent.sproccompiler.export.collections.CollectionUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.lang.model.element.ExecutableElement;

public enum DsvSplitStrategy
{
    /**
     * Does not split data
     */
    NONE
            {
                @Override
                public Collection<MethodPartition> partition( Collection<ExecutableElement> procedures,
                        Collection<ExecutableElement> functions )
                {
                    return Collections.singletonList(
                            new MethodPartition( "", CollectionUtils.orderedUnionSet( procedures, functions ) ) );
                }
            },
    /**
     * Splits data depending of the extension kind (e.g.: {@link org.neo4j.procedure.UserFunction} or
     * {@link org.neo4j.procedure.Procedure}
     */
    KIND
            {
                @Override
                public Collection<MethodPartition> partition( Collection<ExecutableElement> procedures,
                        Collection<ExecutableElement> functions )
                {
                    return Arrays.asList( new MethodPartition( "-procedures", procedures ),
                            new MethodPartition( "-functions", functions ) );
                }
            };

    public abstract Collection<MethodPartition> partition( Collection<ExecutableElement> procedures,
            Collection<ExecutableElement> functions );
}
