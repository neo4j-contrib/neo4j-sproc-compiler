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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.lang.String.format;

public class Either<L, R>
{

    private final Optional<L> left;
    private final Optional<R> right;

    private Either( Optional<L> left, Optional<R> right )
    {
        this.left = left;
        this.right = right;
        if ( left.isPresent() == right.isPresent() )
        {
            throw new IllegalStateException(
                    format( "Either left or right should be defined. Currently: [left: %s], [right: %s]", left,
                            right ) );
        }
    }

    public static <L, R> Either<L,R> left( L value )
    {
        return new Either<L,R>( Optional.of( value ), Optional.empty() );
    }

    public static <L, R> Either<L,R> right( R value )
    {
        return new Either<L,R>( Optional.empty(), Optional.of( value ) );
    }

    public static <L, R> Either<Stream<L>,Stream<R>> combine( Stream<Either<L,R>> either )
    {
        Collection<L> errors = new ArrayList<>();
        Collection<R> results = new ArrayList<>();
        either.forEach( ( x ) -> x.consume( errors::add, results::add ) );
        if ( !errors.isEmpty() )
        {
            return Either.left( errors.stream() );
        }
        return Either.right( results.stream() );
    }

    public <T> T map( Function<L,T> leftMapper, Function<R,T> rightMapper )
    {
        return left.map( leftMapper ).orElseGet( () -> right.map( rightMapper ).get() );
    }

    public <T> Either<T,R> mapLeft( Function<L,T> leftMapper )
    {
        return new Either<T,R>( left.map( leftMapper ), right );
    }

    public <T> Either<L,T> mapRight( Function<R,T> rightMapper )
    {
        return new Either<L,T>( left, right.map( rightMapper ) );
    }

    public void consume( Consumer<L> leftConsumer, Consumer<R> rightConsumer )
    {
        left.ifPresent( leftConsumer );
        right.ifPresent( rightConsumer );
    }

    @Override
    public String toString()
    {
        return "Either{" + "left=" + left + ", right=" + right + '}';
    }
}
